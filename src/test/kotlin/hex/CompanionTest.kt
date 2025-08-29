package io.github.coolbong.hex.hex

import io.github.coolbong.hex.Hex
import io.github.coolbong.hex.Hex3
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CompanionTest {

    @Test
    fun `empty() should create an empty Hex object`() {
        val emptyHex = Hex.empty()
        assertTrue(emptyHex.isEmpty())
        assertEquals(0, emptyHex.size)
        assertArrayEquals(ByteArray(0), emptyHex.toBytes())
        assertEquals("", emptyHex.toString())
    }


    @Test
    fun `from(ByteArray) should create Hex from full byte array`() {
        val byteArray = byteArrayOf(0x01, 0x02, 0x03, 0xA0.toByte())
        val hex = Hex.from(byteArray)

        assertEquals(4, hex.size)
        assertEquals("010203A0", hex.toString())
        assertNotSame(byteArray, hex.toBytes())
        assertArrayEquals(byteArray, hex.toBytes())

        assertEquals(0x01.toByte(), hex[0])
        assertEquals(0x02.toByte(), hex[1])
        assertEquals(0x03.toByte(), hex[2])
        assertEquals(0xA0.toByte(), hex[3])
    }


    @Test
    fun `from(ByteArray, offset, length) should create Hex from part of byte array`() {
        val byteArray = byteArrayOf(0x01, 0x02, 0xAA.toByte(), 0xBB.toByte())

        val hex1 = Hex.from(byteArray, 2)
        assertEquals(2, hex1.size)
        assertEquals("AABB", hex1.toString())
        assertEquals(0xAA.toByte(), hex1[0])
        assertEquals(0xBB.toByte(), hex1[1])

        val hex2 = Hex.from(byteArray, 1, 2)
        assertEquals(2, hex2.size)
        assertEquals("02AA", hex2.toString())
        assertEquals(0x02.toByte(), hex2[0])
        assertEquals(0xAA.toByte(), hex2[1])
    }

    @Test
    fun `from(String) should create Hex from hexadecimal string`() {
        // 대소문자 구분 없이 생성 가능
        val hex1 = Hex.from("0102AABB")
        val hex2 = Hex.from("0102AABB".lowercase())
        val hex3 = Hex.from("01 02 AA BB")
        val hex4 = Hex.from("01:02:AA:BB")
        val hex5 = Hex.from("01_02_AA_BB")
        val hex6 = Hex.from("01-02-AA-BB")
        val hex7 = Hex.from("01:02-\nAA_ BB")

        assertEquals("0102AABB", hex1.toString())
        assertEquals("0102AABB", hex2.toString())
        assertEquals("0102AABB", hex3.toString())
        assertEquals("0102AABB", hex4.toString())
        assertEquals("0102AABB", hex5.toString())
        assertEquals("0102AABB", hex6.toString())
        assertEquals("0102AABB", hex7.toString())

        assertEquals(4, hex1.size)
        assertEquals(4, hex2.size)
        assertEquals(4, hex3.size)
        assertEquals(4, hex4.size)
        assertEquals(4, hex5.size)
        assertEquals(4, hex6.size)
        assertEquals(4, hex7.size)
    }

    @Test
    fun `from(ByteArray, offset, length) should throw exception for invalid parameters`() {
        val byteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)

        // 음수 offset
        assertThrows<IllegalArgumentException> {
            Hex.from(byteArray, -1)
        }

        // 음수 length
        assertThrows<IllegalArgumentException> {
            Hex.from(byteArray, 0, -1)
        }

        // 범위를 벗어나는 offset과 length
        assertThrows<IllegalArgumentException> {
            Hex.from(byteArray, 6)
        }

        assertThrows<IllegalArgumentException> {
            Hex.from(byteArray, 3, 3)
        }
    }

    @Test
    fun `from(String) should throw exception for invalid hex string`() {
        // 홀수 길이 문자열
        assertThrows<IllegalArgumentException> {
            Hex.from("0102030")
        }

        // 16진수가 아닌 문자 포함
        assertThrows<IllegalArgumentException> {
            Hex.from("0102030G")
        }
    }


    @Test
    fun `fromAscii should create Hex from ASCII string`() {
        val hex = Hex.fromAscii("Hello")

        val answer = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F)
        assertEquals(5, hex.size)
        assertEquals("48656C6C6F", hex.toString())
        assertArrayEquals(answer, hex.toBytes())

        assertEquals(0x48.toByte(), hex[0])
        assertEquals(0x65.toByte(), hex[1])
        assertEquals(0x6C.toByte(), hex[2])
        assertEquals(0x6C.toByte(), hex[3])
        assertEquals(0x6F.toByte(), hex[4])

    }

}