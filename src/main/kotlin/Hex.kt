package io.github.coolbong.hex

import java.nio.charset.Charset


open class Hex protected constructor(protected val data: ByteArray) : Comparable<Hex> {


    /**
     * Returns the number of bytes in this Hex object.
     *
     * @return Byte array length
     */
    val size: Int get() = data.size

    /**
     * Checks if this Hex object contains no bytes.
     *
     * @return true if empty, false otherwise
     */
    fun isEmpty(): Boolean = data.isEmpty()


    /**
     * Returns a new Hex object containing the leftmost bytes.
     *
     * @param length Number of bytes to take from the left.
     *               If length is greater than size, it will return the full Hex.
     *               If length <= 0, it will return an empty Hex.
     * @return A new Hex containing the leftmost `length` bytes.
     */
    fun left(length: Int): Hex {
        if (length <= 0) return empty()
        val safeLength = minOf(length, size)
        return Hex(data.copyOfRange(0, safeLength))
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
        require(start >= 0) { "start must be non-negative" }
        require(length >= 0) { "Length must be non-negative" }

        if (start !in 0..<size) return empty()
        val end = (start + length).coerceAtMost(size)
        return Hex(data.copyOfRange(start, end))
    }


    /**
     * Returns a new Hex object containing the rightmost bytes.
     *
     * @param length Number of bytes to take from the right.
     *               If length is greater than size, it will return the full Hex.
     *               If length <= 0, it will return an empty Hex.
     * @return A new Hex containing the rightmost `length` bytes.
     */
    fun right(length: Int): Hex {
        if (length <= 0) return empty()
        val safeLength = minOf(length, size)
        return Hex(data.copyOfRange(size - safeLength, size))
    }


    /**
     * Left-pad this Hex with the given byte until it reaches the specified total length.
     *
     * @param totalLength The desired total length of the Hex in bytes.
     *                    If totalLength <= current size, the original Hex is returned.
     * @param padByte The byte used for padding. Default is 0x00.
     * @return A new Hex object left-padded to totalLength.
     */
    @JvmOverloads
    fun lpad(totalLength: Int, padByte: Byte = 0x00): Hex {
        if (totalLength <= size) return this
        val padding = ByteArray(totalLength - size) { padByte }
        return Hex(padding + data)
    }

    /**
     * Right-pad this Hex with the given byte until it reaches the specified total length.
     *
     * @param totalLength The desired total length of the Hex in bytes.
     *                    If totalLength <= current size, the original Hex is returned.
     * @param padByte The byte used for padding. Default is 0x00.
     * @return A new Hex object right-padded to totalLength.
     */
    @JvmOverloads
    fun rpad(totalLength: Int, padByte: Byte = 0x00): Hex {
        if (totalLength <= size) return this
        val padding = ByteArray(totalLength - size) { padByte }
        return Hex(data + padding)
    }

    /**
     * Slice the Hex value using Python-like slicing semantics.
     *
     * @param start The start index (inclusive). Defaults to 0.
     * @param end   The end index (exclusive). Defaults to size.
     * @return A new Hex containing the sliced range. If indices are out of bounds,
     *         they are adjusted to the valid range. If start >= end, returns empty.
     */
    fun slice(start: Int = 0, end: Int = size): Hex {
        val safeStart = start.coerceAtLeast(0).coerceAtMost(size)
        val safeEnd = end.coerceAtLeast(0).coerceAtMost(size)
        if (safeStart >= safeEnd) return Hex.empty()
        return Hex(data.copyOfRange(safeStart, safeEnd))
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