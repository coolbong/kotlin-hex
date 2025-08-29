package io.github.coolbong.hex.hex

import io.github.coolbong.hex.Hex
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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

        val hex2 = Hex.from(byteArray, 1, 2)
        assertEquals(2, hex2.size)
        assertEquals("02AA", hex2.toString())
    }
}