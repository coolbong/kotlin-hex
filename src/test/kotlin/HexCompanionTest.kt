package io.github.coolbong.hex

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HexCompanionTest {

    @Test
    fun `empty() should create an empty Hex object`() {
        val emptyHex = Hex.empty()
        assertTrue(emptyHex.isEmpty)
        assertEquals(0, emptyHex.size)
        assertEquals("", emptyHex.toString())
    }

    @Test
    fun `from(ByteArray) should create Hex from full byte array`() {
        val byteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val hex = Hex.from(byteArray)

        assertEquals(4, hex.size)
        assertEquals("01020304", hex.toString())
        assertNotSame(byteArray, hex.toBytes()) // 복사본인지 확인
    }

    @Test
    fun `from(ByteArray, offset, length) should create Hex from part of byte array`() {
        val byteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)

        // 전체 배열의 일부분
        val partialHex1 = Hex.from(byteArray, 2)
        assertEquals("030405", partialHex1.toString())

        // 특정 부분 선택
        val partialHex2 = Hex.from(byteArray, 1, 2)
        assertEquals("0203", partialHex2.toString())
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
    fun `from(String) should create Hex from hexadecimal string`() {
        // 대소문자 구분 없이 생성 가능
        val hex1 = Hex.from("01020304")
        val hex2 = Hex.from("01020304".lowercase())
        val hex3 = Hex.from("01 02 03 04")  // 공백 무시

        assertEquals("01020304", hex1.toString())
        assertEquals(hex1, hex2)
        assertEquals(hex1, hex3)
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
        assertEquals("48656C6C6F", hex.toString())
    }

    @Test
    fun `fromAscii should handle empty string`() {
        val hex = Hex.fromAscii("")
        assertTrue(hex.isEmpty)
    }

}