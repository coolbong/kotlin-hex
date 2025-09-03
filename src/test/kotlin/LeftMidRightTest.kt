package io.github.coolbong.hex

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class LeftMidRightTest {

    @Test
    fun `left should return correct hex`() {
        val hex = Hex.from("0102AABB")

        //val hex = Hex.from("1234567890")

        val hex1 = hex.left(2)
        Assertions.assertEquals(2, hex1.size)
        Assertions.assertEquals("0102", hex1.toString())
        Assertions.assertEquals(0x01.toByte(), hex1[0])
        Assertions.assertEquals(0x02.toByte(), hex1[1])

        val hex2 = hex.left(10)
        assertEquals("0102AABB", hex2.toString())
        assertEquals(4, hex2.size)
        Assertions.assertEquals(0x01.toByte(), hex2[0])
        Assertions.assertEquals(0x02.toByte(), hex2[1])
        Assertions.assertEquals(0xAA.toByte(), hex2[2])
        Assertions.assertEquals(0xBB.toByte(), hex2[3])
    }

    @Test
    fun `right should return correct substring`() {
        val hex = Hex.from("1234567890")

        val hex1 = hex.right(4)
        assertEquals("34567890", hex1.toString())
        assertEquals(4, hex1.size)

        val hex2 = hex.right(10)
        assertEquals("1234567890", hex2.toString())
        assertEquals(5, hex2.size)

        val hex3 = hex.right(2)
        assertEquals("7890", hex3.toString())
        assertEquals(2, hex3.size)
        Assertions.assertEquals(0x78.toByte(), hex3[0])
        Assertions.assertEquals(0x90.toByte(), hex3[1])


    }

    @Test
    fun `mid should return correct substring`() {
        val hex = Hex.from("112233445566")

        // When & Then
        assertEquals("2233", hex.mid(1, 2).toString())
        assertEquals("334455", hex.mid(2, 3).toString())
        assertEquals("3344", hex.mid(2, 2).toString())
        assertEquals("5566", hex.mid(4).toString()) // length를 지정하지 않으면 끝까지

        // Edge cases
        assertThrows<IllegalArgumentException> { hex.mid(-1, 2) }
        assertThrows<IllegalArgumentException> { hex.mid(2, -1) }

        assertEquals("3344", hex.mid(2, 2).toString())
        assertEquals("33445566", hex.mid(2, 10).toString())
    }

    @Test
    fun `mid should throw exception for invalid parameters`() {
        val hex = Hex.from("1234567890")

        assertThrows<IllegalArgumentException> { hex.mid(-1, 4) }
        assertThrows<IllegalArgumentException> { hex.mid(0, -1) }
        val hex1 =  hex.mid(6, 5)
        assertEquals("", hex1.toString())
    }
}