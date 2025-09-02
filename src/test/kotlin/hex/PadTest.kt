package io.github.coolbong.hex.hex

import io.github.coolbong.hex.Hex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PadTest {

    @Test
    fun `lpad should pad when length is larger`() {
        val hex = Hex.from("1234")
        val paddedHex = hex.lpad(8)

        assertEquals("0000000000001234", paddedHex.toString())
        assertEquals(8, paddedHex.size)
    }

    @Test
    fun `lpad should return self when length is less or equal`() {
        val hex = Hex.from("1234")

        val paddedSame = hex.lpad(4)
        assertEquals("00001234", paddedSame.toString())
        assertEquals(4, paddedSame.size)

        val paddedSmaller = hex.lpad(2)
        assertEquals("1234", paddedSmaller.toString())
        assertEquals(2, paddedSmaller.size)
    }

    @Test
    fun `rpad should pad when length is larger`() {
        val hex = Hex.from("1234")
        val paddedHex = hex.rpad(8)

        assertEquals("1234000000000000", paddedHex.toString())
        assertEquals(8, paddedHex.size)
    }

    @Test
    fun `rpad should return self when length is less or equal`() {
        val hex = Hex.from("1234")

        val paddedSame = hex.rpad(4)
        assertEquals("12340000", paddedSame.toString())
        assertEquals(4, paddedSame.size)

        val paddedSmaller = hex.rpad(2)
        assertEquals("1234", paddedSmaller.toString())
        assertEquals(2, paddedSmaller.size)
    }


    @Test
    fun `rpad should add padding bytes to the right`() {
        // Given
        val hex = Hex.from("1122")

        // When & Then
        assertEquals("112200", hex.rpad(3).toString())
        assertEquals("11220000", hex.rpad(4).toString())
        assertEquals("1122", hex.rpad(2).toString()) // 이미 충분한 길이
        assertEquals("1122", hex.rpad(1).toString()) // 지정된 길이보다 이미 큰 경우

        // 다른 패딩 바이트 사용
        assertEquals("1122FF", hex.rpad(3, 0xFF.toByte()).toString())
        assertEquals("1122AAAAAA", hex.rpad(5, 0xAA.toByte()).toString())

        // Edge cases
        assertEquals("", Hex.empty().rpad(0).toString())
        assertEquals("00", Hex.empty().rpad(1).toString())
    }

    @Test
    fun `lpad should add padding bytes to the left`() {
        // Given
        val hex = Hex.from("1122")

        // When & Then
        assertEquals("001122", hex.lpad(3).toString())
        assertEquals("00001122", hex.lpad(4).toString())
        assertEquals("1122", hex.lpad(2).toString()) // 이미 충분한 길이
        assertEquals("1122", hex.lpad(1).toString()) // 지정된 길이보다 이미 큰 경우

        // 다른 패딩 바이트 사용
        assertEquals("FF1122", hex.lpad(3, 0xFF.toByte()).toString())
        assertEquals("AAAAAAAA1122", hex.lpad(6, 0xAA.toByte()).toString())

        // Edge cases
        assertEquals("", Hex.empty().lpad(0).toString())
        assertEquals("00", Hex.empty().lpad(1).toString())

        val h = Hex.from("A1B2")
        val h1 = h.lpad(4)

        assertEquals("0000A1B2", h1.toString())
        assertEquals(4, h1.size)
        assertEquals(0x00.toByte(), h1[0])
        assertEquals(0x00.toByte(), h1[1])
        assertEquals(0xA1.toByte(), h1[2])
        assertEquals(0xB2.toByte(), h1[3])

        val h2 = h.lpad(6, 0xff.toByte())
        assertEquals("FFFFFFFFA1B2", h2.toString())
        assertEquals(6, h2.size)
        assertEquals(0xff.toByte(), h2[0])
        assertEquals(0xff.toByte(), h2[1])
        assertEquals(0xff.toByte(), h2[2])
        assertEquals(0xff.toByte(), h2[3])
        assertEquals(0xA1.toByte(), h2[4])
        assertEquals(0xB2.toByte(), h2[5])

        val h3 = h.rpad(4)
        assertEquals("A1B20000", h3.toString())
        assertEquals(4, h3.size)
        assertEquals(0xA1.toByte(), h3[0])
        assertEquals(0xB2.toByte(), h3[1])
        assertEquals(0x00.toByte(), h3[2])
        assertEquals(0x00.toByte(), h3[3])

        val h4 = h.rpad(6, 0xff.toByte())
        assertEquals("A1B2FFFFFFFF", h4.toString())
        assertEquals(6, h4.size)
        assertEquals(0xA1.toByte(), h4[0])
        assertEquals(0xB2.toByte(), h4[1])
        assertEquals(0xff.toByte(), h4[2])
        assertEquals(0xff.toByte(), h4[3])
        assertEquals(0xff.toByte(), h4[4])
        assertEquals(0xff.toByte(), h4[5])
    }

    @Test
    fun `practical examples of padding functions`() {
        // 1. BER-TLV Length 필드 인코딩
        val length = 127 // 0x7F
        val lengthHex = Hex.from(byteArrayOf(length.toByte()))
        assertEquals("7F", lengthHex.toString())

        // 2. 8바이트 고정 길이 필드로 만들기
        val pan = Hex.from("1234567890123456")
        val paddedPan = pan.rpad(8, 0xFF.toByte())
        assertEquals("1234567890123456", paddedPan.toString()) // 이미 8바이트

        // 3. 6바이트 고정 필드로 만들기 (작은 값)
        val amount = Hex.from("0000123456")
        val fixedAmount = amount.lpad(6)
        assertEquals("000000123456", fixedAmount.toString())

        // 4. 이진 데이터를 8바이트 블록으로 패딩 (암호화 전)
        val data = Hex.from("123456789A")
        val paddedForEncryption = data.rpad((data.size + 7) / 8 * 8, 0x00)
        assertEquals("123456789A000000", paddedForEncryption.toString())

        // 5. AID(Application Identifier) 패딩
        val aid = Hex.from("A0000000031010")
        val paddedAid = aid.rpad(16, 0xFF.toByte())
        assertEquals("A0000000031010FFFFFFFFFFFFFFFFFF", paddedAid.toString())
    }
}