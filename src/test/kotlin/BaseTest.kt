package io.github.coolbong.hex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BaseTest {


    @Test
    fun `size of Hex`() {
        val h = Hex.from("0011")
        assertEquals(2, h.size)
    }

    @Test
    fun `last index of Hex`() {
        val h = Hex.from("0011")
        assertEquals(1, h.lastIndex)
    }

    @Test
    fun `plus should concatenate two Hex values 1`() {
        val hex1 = Hex.from("AABB")
        val hex2 = Hex.from("CCDD")
        val result = hex1 + hex2

        assertEquals(0xAA.toByte(), result[0])
        assertEquals("AABBCCDD", result.toString())
    }

    @Test
    fun `plus should concatenate two Hex values 2`() {
        val hex1 = Hex.from("1234")
        val hex2 = Hex.from("5678")
        val result = hex1 + hex2
        assertEquals("12345678", result.toString())
    }

    @Test
    fun `plus should concatenate two Hex values 3`() {
        val hex1 = Hex.from("87")
        val hex2 = Hex.from("01")
        val hex3 = Hex.from("01")
        val result = hex1 + hex2 + hex3
        assertEquals("870101", result.toString())
        assertEquals(3, result.size)
        assertEquals(0x87.toByte(), result[0])
        assertEquals(0x01.toByte(), result[1])
        assertEquals(0x01.toByte(), result[2])
    }

    @Test
    fun `plus should work with empty Hex`() {
        val hex1 = Hex.from("1234")
        val hex2 = Hex.from("")
        val result1 = hex1 + hex2
        val result2 = hex2 + hex1

        assertEquals("1234", result1.toString())
        assertEquals("1234", result2.toString())
    }

    @Test
    fun `plus should handle multiple Hex concatenations`() {
        val hex1 = Hex.from("12")
        val hex2 = Hex.from("34")
        val hex3 = Hex.from("56")
        val result = hex1 + hex2 + hex3
        assertEquals("123456", result.toString())
    }


    @Test
    fun `get operator should return correct byte`() {
        val hex = Hex.from("1234")

        assertEquals(0x12, hex[0])
        assertEquals(0x34, hex[1])
        assertThrows<IllegalArgumentException> { hex[2] }
    }


    @Test
    fun `slice should return range from start to end`() {
        val h = Hex.from("A1B2C3D4E5")
        val sliced = h.slice(1, 3)
        assertEquals("B2C3", sliced.toString())
    }

    @Test
    fun `slice should return from start index to end of string when only start is given`() {
        val h = Hex.from("A1B2C3D4E5")
        val sliced = h.slice(2)
        assertEquals("C3D4E5", sliced.toString())
    }

    @Test
    fun `slice should return from beginning when end is specified`() {
        val h = Hex.from("A1B2C3D4E5")
        val sliced = h.slice(0, 3)
        assertEquals("A1B2C3", sliced.toString())
    }

    @Test
    fun `slice should return empty when start equals end`() {
        val h = Hex.from("A1B2C3D4E5")
        val sliced = h.slice(2, 2)
        assertEquals("", sliced.toString())
    }

    @Test
    fun `slice should handle end index beyond length`() {
        val h = Hex.from("A1B2C3D4E5")
        val sliced = h.slice(3, 20)
        assertEquals("D4E5", sliced.toString())
    }

    @Test
    fun `slice should return empty when start is beyond length`() {
        val h = Hex.from("A1B2C3D4E5")
        val sliced = h.slice(10, 20)
        assertEquals("", sliced.toString())
    }

    @Test
    fun `slice should treat negative start as zero`() {
        val h = Hex.from("A1B2C3D4E5")
        val sliced = h.slice(-3, 3)
        assertEquals("A1B2C3", sliced.toString())
    }

    @Test
    fun `slice should return empty when end is negative`() {
        val h = Hex.from("A1B2C3D4E5")
        val sliced = h.slice(0, -2)
        assertEquals("", sliced.toString())
    }

    @Test
    fun `not should flip all bits`() {
        val h = Hex.from("00FF")
        val result = !h
        assertEquals("FF00", result.toString())
    }

    @Test
    fun `not should invert each byte independently`() {
        val h = Hex.from("A1B2")
        val result = !h
        assertEquals("5E4D", result.toString())
        assertEquals("5E4D", h.inverse().toString())
    }

    @Test
    fun `not should return empty when applied to empty Hex`() {
        val h = Hex.empty()
        val result = !h
        assertEquals("", result.toString())
    }

    @Test
    fun `and should compute bitwise AND`() {
        val a = Hex.from("F0F0")
        val b = Hex.from("0FF0")
        val result = a and b
        assertEquals("00F0", result.toString())
        assertEquals("00F0", a.andOp(b).toString())
    }

    @Test
    fun `or should compute bitwise OR`() {
        val a = Hex.from("F0F0")
        val b = Hex.from("0FF0")
        val result = a or b
        assertEquals("FFF0", result.toString())
        assertEquals("FFF0", a.orOp(b).toString())
    }

    @Test
    fun `xor should compute bitwise XOR`() {
        val a = Hex.from("F0F0")
        val b = Hex.from("0FF0")
        val result = a xor b
        assertEquals("FF00", result.toString())
        assertEquals("FF00", a.xorOp(b).toString())
    }

    @Test
    fun `xor operation should work correctly`() {
        val hex1 = Hex.from("FF00")
        val hex2 = Hex.from("00FF")

        val result = hex1 xor hex2
        assertEquals("FFFF", result.toString())
    }

    @Test
    fun `or operation should work correctly`() {
        val hex1 = Hex.from("1010")
        val hex2 = Hex.from("0101")

        val result = hex1 or hex2
        assertEquals("1111", result.toString())
    }

    @Test
    fun `and operation should work correctly`() {
        val hex1 = Hex.from("1100")
        val hex2 = Hex.from("1010")

        val result = hex1 and hex2
        assertEquals("1000", result.toString())
    }

    @Test
    fun `bitwise ops should throw when lengths differ`() {
        val a = Hex.from("FFFF")
        val b = Hex.from("0F")
        assertFailsWith<IllegalArgumentException> { a and b }
        assertFailsWith<IllegalArgumentException> { a or b }
        assertFailsWith<IllegalArgumentException> { a xor b }
    }

    @Test
    fun `bitwise ops should return empty when both are empty`() {
        val a = Hex.empty()
        val b = Hex.empty()
        assertEquals("", (a and b).toString())
        assertEquals("", (a or b).toString())
        assertEquals("", (a xor b).toString())
    }
}