package io.github.coolbong.hex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class HexOperatorOverloadTest {

    @Test
    fun `plus should concatenate two Hex values`() {
        val hex1 = Hex.from("1234")
        val hex2 = Hex.from("5678")
        val result = hex1 + hex2
        assertEquals("12345678", result.toString())
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
    }

    @Test
    fun `set operator should create new Hex with updated byte`() {
        val hex = Hex.from("1234")

        //val modifiedHex = hex[1] = 0xFF
        hex[1] = 0xff
        assertEquals("12FF", hex.toString())
    }
}