package io.github.coolbong.hex.hex

import io.github.coolbong.hex.Hex
import io.github.coolbong.hex.Hex3
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LeftMidRightTest {

    @Test
    fun `left should return correct hex`() {
        val hex = Hex.from("0102AABB")

        //val hex = Hex3.from("1234567890")

        val hex1 = hex.left(2)
        Assertions.assertEquals(2, hex1.size)
        Assertions.assertEquals("0102", hex1.toString())
        Assertions.assertEquals(0x01.toByte(), hex1[0])
        Assertions.assertEquals(0x02.toByte(), hex1[1])

        val leftPart2 = hex.left(10)
        assertEquals("1234567890", leftPart2.toString())
        assertEquals(5, leftPart2.size)
    }
}