package io.github.coolbong.hex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.toString

class HexLeftMidRightTest {
    @Test
    fun `left should return correct substring`() {
        val hex = Hex.from("1234567890")

        val leftPart1 = hex.left(4)
        assertEquals("12345678", leftPart1.toString())
        assertEquals(4, leftPart1.size)

        val leftPart2 = hex.left(10)  // 현재 길이보다 크면 전체 반환
        assertEquals("1234567890", leftPart2.toString())
        assertEquals(5, leftPart2.size)
    }

    @Test
    fun `right should return correct substring`() {
        val hex = Hex.from("1234567890")

        val rightPart1 = hex.right(4)
        assertEquals("34567890", rightPart1.toString())
        assertEquals(4, rightPart1.size)

        val rightPart2 = hex.right(10)  // 현재 길이보다 크면 전체 반환
        assertEquals("1234567890", rightPart2.toString())
        assertEquals(5, rightPart2.size)
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

        // 길이가 가능한 최대 길이보다 클 경우
        assertEquals("3344", hex.mid(2, 2).toString())
        //assertEquals("33445566", hex.mid(2, 10).toString())
    }

    @Test
    fun `mid should throw exception for invalid parameters`() {
        val hex = Hex.from("1234567890")

        assertThrows<IllegalArgumentException> { hex.mid(-1, 4) }
        assertThrows<IllegalArgumentException> { hex.mid(0, -1) }
        assertThrows<IllegalArgumentException> { hex.mid(6, 5) }
    }
}