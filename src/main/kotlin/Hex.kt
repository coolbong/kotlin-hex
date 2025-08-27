package io.github.coolbong.hex


open class Hex protected constructor(protected val data: ByteArray) : Iterable<Byte> {

    override fun iterator(): Iterator<Byte> = data.iterator()


    companion object {
        /**
         * Hex 문자열에서 Hex 객체 생성
         */
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
         * ByteArray에서 Hex 객체 생성
         * @param offset 시작 위치 (기본값 0)
         * @param length 길이 (기본값: 배열 끝까지)
         */
        fun from(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): Hex {
            require(offset >= 0 && length >= 0 && offset + length <= bytes.size) {
                "Invalid offset/length (offset=$offset, length=$length, size=${bytes.size})"
            }
            return Hex(bytes.copyOfRange(offset, offset + length))
        }

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