package io.github.coolbong.hex.hex

import io.github.coolbong.hex.Hex
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class BaseTest {
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

}