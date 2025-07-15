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
data class Hex private constructor(private val bytes: ByteArray) : Comparable<Hex> {

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
    fun slice(start: Int, length: Int): Hex {
        require(start >= 0 && start < bytes.size) { "Invalid start index" }
        require(length >= 0 && start + length <= bytes.size) { "Invalid length" }
        return Hex(bytes.copyOfRange(start, start + length))
    }

    /**
     * MD5 해시 생성
     */
    fun md5(): Hex = hash("MD5")

    /**
     * SHA-256 해시 생성
     */
    fun sha256(): Hex = hash("SHA-256")

    /**
     * 내부 해시 메서드
     */
    private fun hash(algorithm: String): Hex {
        val digest = MessageDigest.getInstance(algorithm)
        return Hex(digest.digest(bytes))
    }

    infix fun xor(other: Hex): Hex {
        require(this.bytes.size == other.bytes.size) { "Hex sizes must be equal for XOR" }
        return Hex(ByteArray(bytes.size) { (bytes[it] xor other.bytes[it]).toByte() })
    }

    infix fun or(other: Hex): Hex {
        require(this.bytes.size == other.bytes.size) { "Hex sizes must be equal for OR" }
        return Hex(ByteArray(bytes.size) { (bytes[it] or other.bytes[it]).toByte() })
    }

    infix fun and(other: Hex): Hex {
        require(this.bytes.size == other.bytes.size) { "Hex sizes must be equal for AND" }
        return Hex(ByteArray(bytes.size) { (bytes[it] and other.bytes[it]).toByte() })
    }


    /**
     * 16진수 값 비교
     */
    override fun compareTo(other: Hex): Int = this.toString().compareTo(other.toString())

    /**
     * 동등성 비교
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Hex
        return bytes.contentEquals(other.bytes)
    }

    fun left(length: Int): Hex {
        require(length >= 0) { "Length must be non-negative" }

        // 요청된 길이가 현재 길이보다 크면 현재 Hex 반환
        if (length >= size) return this

        // 왼쪽부터 지정된 길이만큼 자름
        return Hex(toBytes().copyOfRange(0, length))
    }

    fun right(length: Int): Hex {
        require(length >= 0) { "Length must be non-negative" }

        // 요청된 길이가 현재 길이보다 크면 현재 Hex 반환
        if (length >= size) return this

        // 오른쪽부터 지정된 길이만큼 자름
        return Hex(toBytes().copyOfRange(size - length, size))
    }

    fun mid(start: Int, length: Int = size - start): Hex {
        require(start >= 0 && start < bytes.size) { "Invalid start index" }
        require(length >= 0 && start + length <= bytes.size) { "Invalid length" }
        return Hex(bytes.copyOfRange(start, start + length))
    }

    fun u1(index: Int): Hex {
        require(index in bytes.indices) { "Index out of bounds" }
        return Hex(byteArrayOf(bytes[index]))
    }

    fun u2(index: Int): Hex {
        require(index in bytes.indices) { "Index out of bounds" }
        require(index + 1 in bytes.indices) { "Index out of bounds" }
        return Hex(byteArrayOf(bytes[index], bytes[index + 1]))
    }

    fun un(index: Int, length: Int): Hex {
        require(index >= 0 && index < bytes.size) { "Invalid start index" }
        require(length >= 0 && index + length <= bytes.size) { "Invalid length" }
        return Hex(bytes.copyOfRange(index, index + length))
    }


    fun lpad(length: Int, padByte: Byte = 0x00): Hex {
        //require(length >= size) { "Padding length must be greater than or equal to current hex length" }
        if (length <= size) return this

        val paddingLength = length - size
        val paddingBytes = ByteArray(paddingLength) { padByte }

        return Hex(paddingBytes + toBytes())
    }

    fun rpad(length: Int, padByte: Byte = 0x00): Hex {
        //require(length >= size) { "Padding length must be greater than or equal to current hex length" }
        if (length <= size) return this

        val paddingLength = length - size
        val paddingBytes = ByteArray(paddingLength) { padByte }

        return Hex(toBytes() + paddingBytes)
    }

    // operator overload
    // 연산자 오버로딩: + 연산자로 두 Hex 객체 연결
    operator fun plus(other: Hex): Hex = Hex(this.bytes + other.bytes)

    operator fun get(index: Int): Int {
        require(index >= 0 && index < bytes.size) { "Index out of bounds" }
        return bytes[index].toInt() and 0xff
    }

    operator fun set(index: Int, value: Byte) {
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

    // 연산자 오버로딩: set 연산자로 특정 인덱스의 Int 값 설정
    operator fun set(index: Int, value: Int) {
        require(index in bytes.indices) { "Index out of bounds" }
        bytes[index] = value.toByte()
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
        fun empty(): Hex = Hex(ByteArray(0))

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
        fun from(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): Hex {
            require(offset >= 0) { "Offset must be non-negative" }
            require(length >= 0) { "Length must be non-negative" }
            require(offset + length <= bytes.size) { "Offset and length must be within byte array bounds" }

            return Hex(bytes.copyOfRange(offset, offset + length))
        }

        /**
         * 16진수 문자열에서 Hex 객체 생성
         */
        @JvmStatic
        fun from(hexString: String): Hex {
            if (hexString.isEmpty()) {
                return Hex(byteArrayOf())
            }

            val cleanHexString = hexString.replace("\\s".toRegex(), "")

            require(cleanHexString.length % 2 == 0) { "Hex string must have an even length" }
            require(cleanHexString.matches(Regex("[0-9A-Fa-f]+"))) { "Invalid hexadecimal string" }

            return Hex(cleanHexString.chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray())
        }

        /**
         * ASCII 문자열에서 Hex 객체 생성
         */
        @JvmStatic
        fun fromAscii(ascii: String): Hex = Hex(ascii.toByteArray(Charset.forName("ASCII")))

        @JvmStatic
        fun left(hex: Hex, length: Int): Hex = hex.left(length)

        @JvmStatic
        fun right(hex: Hex, length: Int): Hex = hex.right(length)

        @JvmStatic
        fun mid(hex: Hex, start: Int, length: Int): Hex = hex.mid(start, length)

        @JvmStatic
        fun lpad(hex: Hex, length: Int): Hex = hex.lpad(length)

        @JvmStatic
        fun lpad(hex: Hex, length: Int, padByte: Byte): Hex = hex.lpad(length, padByte)

        @JvmStatic
        fun rpad(hex: Hex, length: Int): Hex = hex.rpad(length)

        @JvmStatic
        fun rpad(hex: Hex, length: Int, padByte: Byte): Hex = hex.rpad(length, padByte)

    }
}