package io.github.coolbong.hex

import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

/**
 * 16진수 데이터를 다루는 클래스
 *
 * @property bytes 내부 바이트 배열
 */
data class Hex3 private constructor(private val bytes: ByteArray) : Comparable<Hex3> {

    // 바이트 배열의 크기
    val size: Int get() = bytes.size

    // 빈 Hex 객체인지 확인
    val isEmpty: Boolean get() = bytes.isEmpty()

    /**
     * 16진수 문자열로 변환
     */
    override fun toString(): String = bytes.joinToString("") { "%02X".format(it) }

    /**
     * 바이트 배열로 변환
     */
    fun toBytes(): ByteArray = bytes.copyOf()

    /**
     * ASCII 문자열로 변환
     */
    fun toAscii(): String = String(bytes, Charset.forName("ASCII"))


    /**
     * 지정된 길이만큼 자르기
     */
    fun slice(from: Int, to: Int): Hex3 {
        require(from in 0..to && to <= size) {
            "Invalid slice range: $from..<$to> for size=$size"
        }
        return Hex3(bytes.copyOfRange(from, to))
    }

    /**
     * MD5 해시 생성
     */
    fun md5(): Hex3 = hash("MD5")

    /**
     * SHA-1 해시 생성
     */
    fun sha1(): Hex3 = hash("SHA-1")

    /**
     * SHA-256 해시 생성
     */
    fun sha256(): Hex3 = hash("SHA-256")

    /**
     * 내부 해시 메서드
     */
    private fun hash(algorithm: String): Hex3 {
        val digest = MessageDigest.getInstance(algorithm)
        return Hex3(digest.digest(bytes))
    }

    infix fun xor(other: Hex3): Hex3 {
        require(this.bytes.size == other.bytes.size) { "Hex sizes must be equal for XOR" }
        return Hex3(ByteArray(bytes.size) { (bytes[it] xor other.bytes[it]).toByte() })
    }

    infix fun or(other: Hex3): Hex3 {
        require(this.bytes.size == other.bytes.size) { "Hex sizes must be equal for OR" }
        return Hex3(ByteArray(bytes.size) { (bytes[it] or other.bytes[it]).toByte() })
    }

    infix fun and(other: Hex3): Hex3 {
        require(this.bytes.size == other.bytes.size) { "Hex sizes must be equal for AND" }
        return Hex3(ByteArray(bytes.size) { (bytes[it] and other.bytes[it]).toByte() })
    }


    // operator overload
    operator fun plus(other: Hex3): Hex3 = Hex3(this.bytes + other.bytes)

    operator fun get(index: Int): Int {
        require(index >= 0 && index < bytes.size) { "Index out of bounds" }
        return bytes[index].toInt() and 0xff
    }

    operator fun set(index: Int, value: Byte): Unit {
        require(index in bytes.indices) { "Index out of bounds" }
        bytes[index] = value
    }

    operator fun set(index: Int, value: UByte) {
        require(index in bytes.indices) { "Index out of bounds" }
        bytes[index] = value.toByte()
    }

    operator fun set(index: Int, value: Short) {
        require(index in bytes.indices) { "Index out of bounds" }
        bytes[index] = value.toByte()
    }

    operator fun set(index: Int, value: Int) {
        require(index in bytes.indices) { "Index out of bounds" }
        bytes[index] = value.toByte()
    }


    /**
     * 16진수 값 비교
     */
    override fun compareTo(other: Hex3): Int = this.toString().compareTo(other.toString())

    /**
     * 동등성 비교
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Hex3
        return bytes.contentEquals(other.bytes)
    }

    fun left(length: Int): Hex3 {
        require(length >= 0) { "Length must be non-negative" }

        // 요청된 길이가 현재 길이보다 크면 현재 Hex 반환
        if (length >= size) return this

        // 왼쪽부터 지정된 길이만큼 자름
        return Hex3(toBytes().copyOfRange(0, length))
    }

    fun right(length: Int): Hex3 {
        require(length >= 0) { "Length must be non-negative" }

        // 요청된 길이가 현재 길이보다 크면 현재 Hex 반환
        if (length >= size) return this

        // 오른쪽부터 지정된 길이만큼 자름
        return Hex3(toBytes().copyOfRange(size - length, size))
    }

    fun mid(start: Int, length: Int = size - start): Hex3 {
        require(start >= 0 && start < bytes.size) { "Invalid start index" }
        require(length >= 0 && start + length <= bytes.size) { "Invalid length" }
        return Hex3(bytes.copyOfRange(start, start + length))
    }

    fun u1(index: Int): Hex3 {
        require(index in bytes.indices) { "Index out of bounds" }
        return Hex3(byteArrayOf(bytes[index]))
    }

    fun u2(index: Int): Hex3 {
        require(index in bytes.indices) { "Index out of bounds" }
        require(index + 1 in bytes.indices) { "Index out of bounds" }
        return Hex3(byteArrayOf(bytes[index], bytes[index + 1]))
    }

    fun un(index: Int, length: Int): Hex3 {
        require(index >= 0 && index < bytes.size) { "Invalid start index" }
        require(length >= 0 && index + length <= bytes.size) { "Invalid length" }
        return Hex3(bytes.copyOfRange(index, index + length))
    }


    fun lpad(length: Int, padByte: Byte = 0x00): Hex3 {
        //require(length >= size) { "Padding length must be greater than or equal to current hex length" }
        if (length <= size) return this

        val paddingLength = length - size
        val paddingBytes = ByteArray(paddingLength) { padByte }

        return Hex3(paddingBytes + toBytes())
    }

    fun rpad(length: Int, padByte: Byte = 0x00): Hex3 {
        //require(length >= size) { "Padding length must be greater than or equal to current hex length" }
        if (length <= size) return this

        val paddingLength = length - size
        val paddingBytes = ByteArray(paddingLength) { padByte }

        return Hex3(toBytes() + paddingBytes)
    }



    /**
     * 해시 코드
     */
    override fun hashCode(): Int = bytes.contentHashCode()

    companion object {
        /**
         * 빈 Hex 객체 생성
         */
        @JvmStatic
        fun empty(): Hex3 = Hex3(ByteArray(0))

        /**
         * 바이트 배열에서 Hex 객체 생성
         */
        //@JvmStatic
        //fun from(bytes: ByteArray): Hex = Hex(bytes.copyOf())

        /**
         * 바이트 배열의 특정 부분으로 Hex 객체 생성
         *
         * @param bytes 원본 바이트 배열
         * @param offset 시작 위치
         * @param length 복사할 길이
         * @throws IllegalArgumentException 잘못된 범위일 경우
         */
        @JvmStatic
        @JvmOverloads
        fun from(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): Hex3 {
            require(offset >= 0) { "Offset must be non-negative" }
            require(length >= 0) { "Length must be non-negative" }
            require(offset + length <= bytes.size) { "Offset and length must be within byte array bounds" }

            return Hex3(bytes.copyOfRange(offset, offset + length))
        }

        /**
         * 16진수 문자열에서 Hex 객체 생성
         */
        @JvmStatic
        fun from(hexString: String): Hex3 {
            if (hexString.isEmpty()) {
                return Hex3(byteArrayOf())
            }

            val cleanHexString = hexString.replace("\\s".toRegex(), "")

            require(cleanHexString.length % 2 == 0) { "Hex string must have an even length" }
            require(cleanHexString.matches(Regex("[0-9A-Fa-f]+"))) { "Invalid hexadecimal string" }

            return Hex3(cleanHexString.chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray())
        }

        /**
         * ASCII 문자열에서 Hex 객체 생성
         */
        @JvmStatic
        fun fromAscii(ascii: String): Hex3 = Hex3(ascii.toByteArray(Charset.forName("ASCII")))

        @JvmStatic
        fun left(hex: Hex3, length: Int): Hex3 = hex.left(length)

        @JvmStatic
        fun right(hex: Hex3, length: Int): Hex3 = hex.right(length)

        @JvmStatic
        fun mid(hex: Hex3, start: Int, length: Int): Hex3 = hex.mid(start, length)

        @JvmStatic
        fun lpad(hex: Hex3, length: Int): Hex3 = hex.lpad(length)

        @JvmStatic
        fun lpad(hex: Hex3, length: Int, padByte: Byte): Hex3 = hex.lpad(length, padByte)

        @JvmStatic
        fun rpad(hex: Hex3, length: Int): Hex3 = hex.rpad(length)

        @JvmStatic
        fun rpad(hex: Hex3, length: Int, padByte: Byte): Hex3 = hex.rpad(length, padByte)

    }
}