package io.github.coolbong.hex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class HexUnTest {
    @Test
    fun `u1 should return single byte Hex`() {
        val hex = Hex.from("1234567890")

        assertEquals("12", hex.u1(0).toString())
        assertEquals("34", hex.u1(1).toString())
        assertEquals("90", hex.u1(4).toString())
    }

    @Test
    fun `u2 should return two byte Hex`() {
        val hex = Hex.from("1234567890")

        assertEquals("1234", hex.u2(0).toString())
        assertEquals("3456", hex.u2(1).toString())
        assertEquals("7890", hex.u2(3).toString())
    }

    @Test
    fun `un should return variable length Hex`() {
        val hex = Hex.from("1234567890")

        assertEquals("123456", hex.un(0, 3).toString())
        assertEquals("567890", hex.un(2, 3).toString())
    }

    @Test
    fun `u1 u2 un should throw exception for out of bounds`() {
        val hex = Hex.from("1234567890")

        assertThrows<IllegalArgumentException> { hex.u1(5) }
        assertThrows<IllegalArgumentException> { hex.u2(4) }
        assertThrows<IllegalArgumentException> { hex.un(4, 3) }
    }
}