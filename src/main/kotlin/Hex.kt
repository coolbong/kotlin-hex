package io.github.coolbong.hex

import java.nio.charset.Charset


open class Hex protected constructor(protected val data: ByteArray) : Comparable<Hex> {



    val size: Int get() = data.size
    fun isEmpty(): Boolean = data.isEmpty()


    fun left(length: Int): Hex {
        require(length >= 0) { "Length must be non-negative" }
        if (length >= size) return Hex(data.copyOf())
        return Hex(data.copyOfRange(0, length))
    }

    /**
     * Safe subrange extraction.
     *
     * @param start starting index (0-based)
     * @param length optional length (default = until end of array).
     *        If (start + length) exceeds size, it will be clamped automatically.
     * @return new Hex instance containing the subrange.
     *
     * Example:
     *   Hex.from("A0B1C2D3").mid(1, 2) -> "B1C2"
     *   Hex.from("A0B1C2D3").mid(2) -> "C2D3"
     *   Hex.from("A0B1C2D3").mid(5, 10) -> "" (empty Hex, safe)
     */
    @JvmOverloads
    fun mid(start: Int, length: Int = size - start): Hex {
        if (start !in 0..<size) return empty()
        val safeLength = length.coerceAtMost(size - start).coerceAtLeast(0)
        return from(data, start, safeLength)
    }


    /**
     * Returns the internal value as a hexadecimal string.
     *
     * @return Hexadecimal string
     */
    override fun toString(): String {
        return data.joinToString("") { "%02X".format(it) }
    }

    /**
     * Returns the internal value as a byte array.
     *
     * @return byte array
     */
    fun toBytes(): ByteArray = data.copyOf()



    /**
     * Lexicographical comparison based on unsigned byte values.
     *
     * @param other Another Hex instance
     * @return Negative if this < other, zero if equal, positive if this > other
     */
    override fun compareTo(other: Hex): Int {
        val minLength = minOf(this.data.size, other.data.size)
        for (i in 0 until minLength) {
            val diff = (this.data[i].toInt() and 0xFF) - (other.data[i].toInt() and 0xFF)
            if (diff != 0) return diff
        }
        return this.data.size - other.data.size
    }

    /**
     * Equality is based on the byte content.
     *
     * @param other Object to compare
     * @return true if both represent the same byte sequence
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hex

        if (!data.contentEquals(other.data)) return false
        if (size != other.size) return false

        return true
    }

    /**
     * Hash code is based on the byte content.
     *
     * @return Hash code suitable for use in HashMap/HashSet
     */
    override fun hashCode(): Int = data.contentHashCode()


    // operator overload
    operator fun plus(other: Hex): Hex = Hex(this.data + other.data)

    operator fun get(index: Int): Byte {
        require(index >= 0 && index < data.size) { "Index out of bounds" }
        //return data[index].toInt() and 0xff
        return data[index]
    }

    companion object {

        /**
         * @return an empty Hex (zero length)
         */
        @JvmStatic
        fun empty(): Hex = Hex(ByteArray(0))

        /**
         * Creates a Hex from a hex-encoded string.
         * The string may contain whitespaces, colons (:), dashes (-), or underscores (_) which will be ignored.
         *
         * Example: "A0 FF-01:02_03" → Hex([0xA0, 0xFF, 0x01, 0x02, 0x03])
         *
         * @param hexString hex-encoded string
         * @return a new Hex instance
         */
        @JvmStatic
        fun from(hexString: String): Hex {
            val cleaned = clean(hexString)
            require(cleaned.length % 2 == 0) {
                "Hex length must be even: ${cleaned.length}"
            }
            require(isHex(cleaned)) {
                "Invalid hex string: contains non-hex characters"
            }
            //return Hex(cleaned.hexToByteArray())
            return Hex(cleaned.chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray())
        }

        /**
         * Creates a Hex from a byte array.
         *
         * @param bytes the source byte array
         * @param offset optional starting index (default 0)
         * @param length optional number of bytes (default bytes.size - offset)
         * @return a new Hex instance
         */
        @JvmStatic
        fun from(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): Hex {
            require(offset >= 0) { "Offset must be non-negative" }
            require(length >= 0) { "Length must be non-negative" }
            require(offset + length <= bytes.size) { "Offset and length must be within byte array bounds" }
            return Hex(bytes.copyOfRange(offset, offset + length))
        }


        /**
         *
         * Creates a Hex from a byte array.
         * @return a new Hex instance
         */
        @JvmStatic
        fun fromAscii(ascii: String): Hex = Hex(ascii.toByteArray(Charset.forName("ASCII")))

        fun isHex(s: String): Boolean =
            s.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }

        /**
         * 모든 공백(스페이스, 탭, 개행 등)과 구분자(: - _) 제거
         */
        fun clean(s: String): String =
            buildString(s.length) {
                for (c in s) {
                    if (c.isWhitespace() || c == ':' || c == '-' || c == '_') continue
                    append(c)
                }
            }
    }

}