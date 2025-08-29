좋아요! 재사용하기 편한 **Hex** 유틸/값 래퍼를 코틀린으로 정리했습니다.
핵심은 “Hex 문자열 ↔ ByteArray” 변환, 유효성 검증, 불변 관리, 편의 연산(슬라이스/결합/탐색)입니다.

```kotlin
@file:Suppress("MemberVisibilityCanBePrivate")

package util.hex

/**
 * Hex 문자열을 파싱해 불변 ByteArray로 관리하는 래퍼.
 *
 * 지원:
 * - 공백/구분자(: - _) 무시 파싱
 * - 대소문자 무시, 짝수 길이 강제
 * - toHex(대문자/소문자 선택), bytes 복사 반환
 * - 결합(+), 부분 추출(slice), 인덱싱, 서브 배열 탐색(indexOf)
 * - equals/hashCode 는 내용 기반
 */
class Hex private constructor(private val data: ByteArray) : Iterable<Byte> {

    val size: Int get() = data.size

    operator fun get(index: Int): Byte = data[index]

    fun toByteArray(): ByteArray = data.copyOf()

    fun toHex(upperCase: Boolean = true, separator: String? = null): String {
        val hex = data.toHexString(upperCase)
        return if (separator.isNullOrEmpty()) hex
        else hex.chunked(2).joinToString(separator)
    }

    fun slice(fromIndex: Int, toIndexExclusive: Int): Hex {
        require(fromIndex in 0..toIndexExclusive && toIndexExclusive <= size) {
            "Invalid slice range: $fromIndex..<$toIndexExclusive> for size=$size"
        }
        return Hex(data.copyOfRange(fromIndex, toIndexExclusive))
    }

    operator fun plus(other: Hex): Hex =
        Hex(this.data + other.data)

    /**
     * 부분 배열 검색 (Knuth-Morris-Pratt).
     * @return 첫 매칭 시작 인덱스, 없으면 -1
     */
    fun indexOf(needle: Hex, startIndex: Int = 0): Int {
        val hay = this.data
        val nee = needle.data
        if (nee.isEmpty()) return startIndex.coerceIn(0, hay.size)
        if (nee.size > hay.size) return -1
        val lps = buildLps(nee)
        var i = startIndex.coerceAtLeast(0)
        var j = 0
        while (i < hay.size) {
            if (hay[i] == nee[j]) {
                i++; j++
                if (j == nee.size) return i - j
            } else {
                j = if (j != 0) lps[j - 1] else { i++; 0 }
            }
        }
        return -1
    }

    override fun iterator(): Iterator<Byte> = data.iterator()

    override fun equals(other: Any?): Boolean =
        other is Hex && data.contentEquals(other.data)

    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String = toHex()

    companion object {
        /**
         * 허용 구분자/공백을 제거하고 짝수 길이 Hex만 파싱.
         * 예) "A0 ff:01-02_03" -> A0FF010203
         */
        fun parse(hexString: String): Hex {
            val cleaned = clean(hexString)
            require(cleaned.length % 2 == 0) {
                "Hex length must be even: ${cleaned.length}"
            }
            require(isHex(cleaned)) {
                "Invalid hex string: contains non-hex characters"
            }
            return Hex(cleaned.hexToByteArray())
        }

        fun fromBytes(bytes: ByteArray): Hex = Hex(bytes.copyOf())

        fun isHex(s: String): Boolean =
            s.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }

        /**
         * 공백/구분자 제거: space, tab, ':', '-', '_'
         */
        fun clean(s: String): String =
            buildString(s.length) {
                for (c in s) {
                    if (c == ' ' || c == '\t' || c == ':' || c == '-' || c == '_') continue
                    append(c)
                }
            }
    }
}

/* ===================== 확장 함수들 ===================== */

/** "0A1bFF" 같은 문자열을 ByteArray로 (짝수 길이 전제) */
fun String.hexToByteArray(): ByteArray {
    require(length % 2 == 0) { "Hex length must be even" }
    val out = ByteArray(length / 2)
    var i = 0
    while (i < length) {
        val hi = hexNibble(this[i])
        val lo = hexNibble(this[i + 1])
        out[i / 2] = ((hi shl 4) or lo).toByte()
        i += 2
    }
    return out
}

/** ByteArray → "0A1BFF" 혹은 "0a1bff" */
fun ByteArray.toHexString(upperCase: Boolean = true): String {
    val digits = if (upperCase) HEX_UPPER else HEX_LOWER
    val out = CharArray(size * 2)
    var j = 0
    for (b in this) {
        val v = b.toInt() and 0xFF
        out[j++] = digits[v ushr 4]
        out[j++] = digits[v and 0x0F]
    }
    return String(out)
}

private fun hexNibble(c: Char): Int = when (c) {
    in '0'..'9' -> c - '0'
    in 'a'..'f' -> c - 'a' + 10
    in 'A'..'F' -> c - 'A' + 10
    else -> throw IllegalArgumentException("Invalid hex char: '$c'")
}

private val HEX_UPPER = charArrayOf(
    '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
)
private val HEX_LOWER = charArrayOf(
    '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
)

/** KMP LPS 테이블 */
private fun buildLps(pat: ByteArray): IntArray {
    val lps = IntArray(pat.size)
    var len = 0
    var i = 1
    while (i < pat.size) {
        if (pat[i] == pat[len]) {
            lps[i++] = ++len
        } else if (len != 0) {
            len = lps[len - 1]
        } else {
            lps[i++] = 0
        }
    }
    return lps
}

/* ===================== 사용 예시 =====================
val h1 = Hex.parse("A0 FF 01:02-03")
println(h1.size)                    // 5
println(h1.toHex())                 // "A0FF010203"
println(h1.toHex(false, ":"))       // "a0:ff:01:02:03"

val h2 = Hex.parse("DEADBEEF")
val h3 = h1 + h2
println(h3)                         // "A0FF010203DEADBEEF"

println(h3.indexOf(Hex.parse("0102"))) // 2
println(h3.slice(0, 3))                // "A0FF01"

val bytes = h1.toByteArray()        // 복사본 반환 (불변성 보장)
*/
```

원하는 API가 더 있으면(예: `compareTo`, `MutableHex`, `Base64` 변환, `HexDecoder` 예외 타입 분리 등) 바로 확장해 드릴게요.


좋은 방향이에요 👍
`Hex`는 불변(immutable) 클래스로 두고, `MutableHex`는 **상속을 통해 `Hex`를 확장**하면서 내부를 바꿀 수 있게 만드는 방식이군요.

다만 여기서 한 가지 제약이 있어요:

* 현재 `Hex`는 `private constructor`라서 상속이 불가능합니다.
* 상속 가능하게 하려면 `open class`로 바꾸고, 생성자를 `protected`로 열어줘야 해요.

---

### 📝 개선된 설계

```kotlin
package util.hex

open class Hex protected constructor(protected val data: ByteArray) : Iterable<Byte> {

    val size: Int get() = data.size

    operator fun get(index: Int): Byte = data[index]

    fun toByteArray(): ByteArray = data.copyOf()

    fun toHex(upperCase: Boolean = true, separator: String? = null): String {
        val hex = data.toHexString(upperCase)
        return if (separator.isNullOrEmpty()) hex
        else hex.chunked(2).joinToString(separator)
    }

    fun slice(fromIndex: Int, toIndexExclusive: Int): Hex {
        require(fromIndex in 0..toIndexExclusive && toIndexExclusive <= size) {
            "Invalid slice range: $fromIndex..<$toIndexExclusive> for size=$size"
        }
        return Hex(data.copyOfRange(fromIndex, toIndexExclusive))
    }

    operator fun plus(other: Hex): Hex =
        Hex(this.data + other.data)

    fun indexOf(needle: Hex, startIndex: Int = 0): Int {
        val hay = this.data
        val nee = needle.data
        if (nee.isEmpty()) return startIndex.coerceIn(0, hay.size)
        if (nee.size > hay.size) return -1
        val lps = buildLps(nee)
        var i = startIndex.coerceAtLeast(0)
        var j = 0
        while (i < hay.size) {
            if (hay[i] == nee[j]) {
                i++; j++
                if (j == nee.size) return i - j
            } else {
                j = if (j != 0) lps[j - 1] else { i++; 0 }
            }
        }
        return -1
    }

    override fun iterator(): Iterator<Byte> = data.iterator()

    override fun equals(other: Any?): Boolean =
        other is Hex && data.contentEquals(other.data)

    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String = toHex()

    companion object {
        fun parse(hexString: String): Hex {
            val cleaned = clean(hexString)
            require(cleaned.length % 2 == 0) {
                "Hex length must be even: ${cleaned.length}"
            }
            require(isHex(cleaned)) {
                "Invalid hex string: contains non-hex characters"
            }
            return Hex(cleaned.hexToByteArray())
        }

        fun fromBytes(bytes: ByteArray): Hex = Hex(bytes.copyOf())

        fun isHex(s: String): Boolean =
            s.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }

        fun clean(s: String): String =
            buildString(s.length) {
                for (c in s) {
                    if (c == ' ' || c == '\t' || c == ':' || c == '-' || c == '_') continue
                    append(c)
                }
            }
    }
}

/**
 * 가변 Hex: Hex를 상속받아 set 기능 추가
 */
class MutableHex private constructor(data: ByteArray) : Hex(data) {

    operator fun set(index: Int, value: Byte) {
        require(index in 0 until size) { "Index out of bounds: $index" }
        data[index] = value
    }

    fun toImmutable(): Hex = Hex.fromBytes(data)

    companion object {
        fun fromBytes(bytes: ByteArray): MutableHex = MutableHex(bytes.copyOf())
        fun parse(hexString: String): MutableHex = MutableHex(Hex.parse(hexString).toByteArray())
    }
}
```

---

### ✅ 사용 예시

```kotlin
val h1 = Hex.parse("A0FF01")
val h2 = h1.slice(0, 2)         // Hex 불변
println(h1)                     // A0FF01
println(h2)                     // A0FF

val m1 = MutableHex.parse("A0FF01")
m1[1] = 0x55.toByte()           // set 허용
println(m1)                     // A05501

val imm = m1.toImmutable()      // 다시 불변 객체로 변환
println(imm)                    // A05501
```

---

👉 이렇게 하면 `Hex`는 **불변 데이터 래퍼**로 쓰고,
필요할 때만 `MutableHex`로 생성해서 내부를 바꾸는 패턴을 가질 수 있어요.

---

혹시 `MutableHex`에서도 `plus`, `slice`, `indexOf` 같은 함수는 그대로 **가변 버전**으로 동작하길 원하시나요,
아니면 항상 **새로운 Hex/MutableHex**를 반환하는 쪽이 나을까요?



좋습니다 👍
요구사항을 반영해서 `fromHex` → `from` 으로 통일했고, \*\*Java에서도 사용하기 쉽도록 `@JvmStatic`\*\*을 붙였습니다.
또한 각 API에 대한 \*\*영문 설명(JavaDoc 스타일)\*\*도 추가했습니다.

```kotlin
/**
 * Immutable representation of a hexadecimal byte sequence.
 */
class Hex private constructor(
    private val bytes: ByteArray
) {

    /**
     * @return the number of bytes in this Hex
     */
    fun size(): Int = bytes.size

    /**
     * @return true if this Hex contains no bytes
     */
    fun isEmpty(): Boolean = bytes.isEmpty()

    /**
     * @return a copy of the underlying byte array
     */
    fun toByteArray(): ByteArray = bytes.copyOf()

    /**
     * @return this Hex encoded as an uppercase hex string (e.g., "0A1B2C")
     */
    fun toHexString(): String =
        buildString(bytes.size * 2) {
            for (b in bytes) {
                append(String.format("%02X", b))
            }
        }

    /**
     * Extracts a subsequence of this Hex.
     *
     * @param offset the starting index (0-based)
     * @param length the number of bytes to include
     * @return a new Hex object containing the subsequence
     */
    fun substring(offset: Int, length: Int): Hex {
        require(offset >= 0 && length >= 0 && offset + length <= bytes.size) {
            "Invalid offset/length"
        }
        return Hex(bytes.copyOfRange(offset, offset + length))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hex) return false
        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = bytes.contentHashCode()

    override fun toString(): String = toHexString()

    companion object {
        /**
         * Creates a Hex from a hex-encoded string.
         * The string may contain whitespaces, colons (:), dashes (-), or underscores (_) which will be ignored.
         *
         * Example: "A0 FF-01:02_03" → Hex([0xA0, 0xFF, 0x01, 0x02, 0x03])
         *
         * @param s hex-encoded string
         * @return a new Hex instance
         */
        @JvmStatic
        fun from(s: String): Hex {
            val cleaned = clean(s)
            require(cleaned.length % 2 == 0) { "Hex string must have even length" }
            val out = ByteArray(cleaned.length / 2)
            for (i in out.indices) {
                val index = i * 2
                val byteStr = cleaned.substring(index, index + 2)
                out[i] = byteStr.toInt(16).toByte()
            }
            return Hex(out)
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
            require(offset >= 0 && length >= 0 && offset + length <= bytes.size) {
                "Invalid offset/length"
            }
            return Hex(bytes.copyOfRange(offset, offset + length))
        }

        /**
         * @return an empty Hex (zero length)
         */
        @JvmStatic
        fun empty(): Hex = Hex(ByteArray(0))

        private fun clean(s: String): String =
            buildString(s.length) {
                for (c in s) {
                    if (c.isWhitespace() || c == ':' || c == '-' || c == '_') continue
                    append(c)
                }
            }
    }
}
```

### Example usage in Kotlin

```kotlin
val hex1 = Hex.from("A0 FF 01-02:03")
println(hex1) // A0FF010203

val hex2 = Hex.from(byteArrayOf(0x10, 0x20, 0x30, 0x40), offset = 1, length = 2)
println(hex2) // 2030

println(Hex.empty().isEmpty()) // true
```

### Example usage in Java

```java
Hex h1 = Hex.from("A0FF010203");
System.out.println(h1.toHexString()); // A0FF010203

byte[] data = {0x10, 0x20, 0x30, 0x40};
Hex h2 = Hex.from(data, 1, 2);
System.out.println(h2); // 2030

System.out.println(Hex.empty().isEmpty()); // true
```

---

👉 혹시 이 Hex 객체를 \*\*자바 컬렉션(Map, Set 등)\*\*에서도 자연스럽게 key/value로 쓰실 건가요? (equals/hashCode는 구현했지만 `Comparable<Hex>` 같은 정렬 지원도 추가할 수 있습니다.)



좋습니다 🙆 `Comparable<Hex>`만 우선 적용한 전체 클래스 버전을 Java 스타일로 정리해드릴게요.
아래는 **Kotlin 코드지만 Java 프로젝트에서도 사용 가능**하며, API 설명은 영문 주석으로 `/** ... */` 형식으로 작성했습니다.

```kotlin
/**
 * Hex represents an immutable hexadecimal byte array value object.
 *
 * This class provides:
 * - Factory methods to create Hex instances from byte arrays or hex strings.
 * - Utility methods to convert Hex back to string or byte array.
 * - Equality, hashing, and lexicographical comparison for collection usage.
 *
 * Comparable<Hex> is implemented, allowing Hex objects to be ordered
 * lexicographically (unsigned byte-based).
 */
class Hex private constructor(private val bytes: ByteArray) : Comparable<Hex> {

    companion object {
        /**
         * Creates a Hex instance from a byte array.
         *
         * @param bytes Byte array input
         * @return Hex instance wrapping the provided byte array
         */
        @JvmStatic
        fun from(bytes: ByteArray): Hex = Hex(bytes.copyOf())

        /**
         * Creates a Hex instance from a hexadecimal string.
         *
         * @param hexString Hexadecimal string (must contain only [0-9a-fA-F] and have even length)
         * @return Hex instance representing the parsed bytes
         * @throws IllegalArgumentException if the string is invalid
         */
        @JvmStatic
        fun from(hexString: String): Hex {
            require(hexString.length % 2 == 0) { "Hex string must have even length" }
            val byteArray = ByteArray(hexString.length / 2)
            for (i in hexString.indices step 2) {
                val byteValue = hexString.substring(i, i + 2).toInt(16)
                byteArray[i / 2] = byteValue.toByte()
            }
            return Hex(byteArray)
        }
    }

    /**
     * Returns the internal value as a hexadecimal string.
     *
     * @return Hexadecimal string (lowercase, without prefix)
     */
    override fun toString(): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Returns a copy of the underlying byte array.
     *
     * @return New byte array containing the same data
     */
    fun toByteArray(): ByteArray = bytes.copyOf()

    /**
     * Equality is based on the byte content.
     *
     * @param other Object to compare
     * @return true if both represent the same byte sequence
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hex) return false
        return this.bytes.contentEquals(other.bytes)
    }

    /**
     * Hash code is based on the byte content.
     *
     * @return Hash code suitable for use in HashMap/HashSet
     */
    override fun hashCode(): Int = bytes.contentHashCode()

    /**
     * Lexicographical comparison based on unsigned byte values.
     *
     * @param other Another Hex instance
     * @return Negative if this < other, zero if equal, positive if this > other
     */
    override fun compareTo(other: Hex): Int {
        val minLength = minOf(this.bytes.size, other.bytes.size)
        for (i in 0 until minLength) {
            val diff = (this.bytes[i].toInt() and 0xFF) - (other.bytes[i].toInt() and 0xFF)
            if (diff != 0) return diff
        }
        return this.bytes.size - other.bytes.size
    }
}
```

---

### ✅ 특징 정리

* `Comparable<Hex>` 구현 → `TreeMap`, `TreeSet` 등 정렬 컬렉션에서 바로 사용 가능
* `equals` & `hashCode` 구현 → `HashMap`, `HashSet` 등 해시 기반 컬렉션에서도 안전하게 사용 가능
* `@JvmStatic` → Java에서 `Hex.from(...)` 형태로 호출 가능
* 내부 `bytes`는 불변 보장을 위해 항상 `copyOf()` 사용

---

👉 혹시 제가 `Comparable`의 정렬 방식을 **unsigned byte lexicographical order**로 정했는데,
원하시는 게 **단순 길이 우선 → 같은 길이면 사전식 비교** 같은 다른 기준인가요, 아니면 지금 방식이 맞을까요?
