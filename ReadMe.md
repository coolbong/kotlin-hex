# Hex Library for Kotlin

A lightweight immutable `Hex` utility class written in **Kotlin**, designed for safe and convenient handling of hexadecimal strings and byte arrays.  
This class is fully interoperable with **Java**.

---

## Features

- Immutable design (internal bytes are safely copied)
- Factory methods for creating `Hex` objects from hex strings or byte arrays
- Validation of hex strings (ignores whitespace, supports uppercase/lowercase)
- Comparable support for sorting (`Comparable<Hex>`)
- Core operations:
    - `+` operator for concatenation
    - Bitwise operations (`notOp`, `andOp`, `orOp`, `xorOp`)
    - `mid`, `left`, `right`, `slice` for sub-hex extraction
    - `lpad`, `rpad` for padding
- Safe handling: no exceptions for out-of-range slicing (returns as much as possible)

---

## Example (Kotlin)

```kotlin
fun main() {
    val hex1 = Hex.from("0A0B0C")
    val hex2 = Hex.from("010203")

    println(hex1)                     // "0A0B0C"
    println(hex1.toByteArray().size)  // 3

    // Concatenation
    val combined = hex1 + hex2
    println(combined)                 // "0A0B0C010203"

    // Bitwise operations
    val anded = hex1 and hex2
    println(anded)                    // "000200"

    val xored = hex1 xor hex2
    println(xored)                    // "0B090F"

    // Sub-hex
    println(combined.mid(2, 3))       // "0B0C01"
    println(combined.left(4))         // "0A0B0C01"
    println(combined.right(2))        // "0203"

    // Padding
    println(hex1.lpad(5, 0xFF.toByte())) // "FFFF0A0B0C"
    println(hex1.rpad(5, 0x00.toByte())) // "0A0B0C0000"
}
```

### Maven
```xml
<dependency>
    <groupId>io.github.coolbong</groupId>
    <artifactId>kotlin-hex</artifactId>
    <version>1.0.0</version>
</dependency>