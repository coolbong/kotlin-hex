package io.github.coolbong.hex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.DefaultAsserter.assertEquals


class HexInfixTest {
    @Test
    fun `xor operation should work correctly`() {
        val hex1 = Hex3.from("FF00")
        val hex2 = Hex3.from("00FF")

        val result = hex1 xor hex2
        assertEquals("FF00 xor 00FF should be FFFF", "FFFF", result.toString())
    }

    @Test
    fun `or operation should work correctly`() {
        val hex1 = Hex3.from("1010")
        val hex2 = Hex3.from("0101")

        val result = hex1 or hex2
        assertEquals("1010 or 0101 should be 1111", "1111", result.toString())
    }

    @Test
    fun `and operation should work correctly`() {
        val hex1 = Hex3.from("1100")
        val hex2 = Hex3.from("1010")

        val result = hex1 and hex2
        assertEquals("1100 and 1010 should be 1000", "1000", result.toString())
    }

    @Test
    fun `bitwise operations should throw exception for different sized Hex`() {
        val hex1 = Hex3.from("1234")
        val hex2 = Hex3.from("123456")

        assertThrows<IllegalArgumentException> { hex1 xor hex2 }
        assertThrows<IllegalArgumentException> { hex1 or hex2 }
        assertThrows<IllegalArgumentException> { hex1 and hex2 }
    }
}