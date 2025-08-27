package io.github.coolbong.hex

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HexPadTest {
    @Test
    fun `lpad should pad when length is larger`() {
        val hex = Hex3.from("1234")
        val paddedHex = hex.lpad(8)

        assertEquals("0000000000001234", paddedHex.toString())
        assertEquals(8, paddedHex.size)
    }

    @Test
    fun `lpad should return self when length is less or equal`() {
        val hex = Hex3.from("1234")

        val paddedSame = hex.lpad(4)
        assertEquals("00001234", paddedSame.toString())
        assertEquals(4, paddedSame.size)

        val paddedSmaller = hex.lpad(2)
        assertEquals("1234", paddedSmaller.toString())
        assertEquals(2, paddedSmaller.size)
    }

    @Test
    fun `rpad should pad when length is larger`() {
        val hex = Hex3.from("1234")
        val paddedHex = hex.rpad(8)

        assertEquals("1234000000000000", paddedHex.toString())
        assertEquals(8, paddedHex.size)
    }

    @Test
    fun `rpad should return self when length is less or equal`() {
        val hex = Hex3.from("1234")

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
        val hex = Hex3.from("1122")

        // When & Then
        assertEquals("112200", hex.rpad(3).toString())
        assertEquals("11220000", hex.rpad(4).toString())
        assertEquals("1122", hex.rpad(2).toString()) // 이미 충분한 길이
        assertEquals("1122", hex.rpad(1).toString()) // 지정된 길이보다 이미 큰 경우

        // 다른 패딩 바이트 사용
        assertEquals("1122FF", hex.rpad(3, 0xFF.toByte()).toString())
        assertEquals("1122AAAAAA", hex.rpad(5, 0xAA.toByte()).toString())

        // Edge cases
        assertEquals("", Hex3.empty().rpad(0).toString())
        assertEquals("00", Hex3.empty().rpad(1).toString())
    }

    @Test
    fun `lpad should add padding bytes to the left`() {
        // Given
        val hex = Hex3.from("1122")

        // When & Then
        assertEquals("001122", hex.lpad(3).toString())
        assertEquals("00001122", hex.lpad(4).toString())
        assertEquals("1122", hex.lpad(2).toString()) // 이미 충분한 길이
        assertEquals("1122", hex.lpad(1).toString()) // 지정된 길이보다 이미 큰 경우

        // 다른 패딩 바이트 사용
        assertEquals("FF1122", hex.lpad(3, 0xFF.toByte()).toString())
        assertEquals("AAAAAAAA1122", hex.lpad(6, 0xAA.toByte()).toString())

        // Edge cases
        assertEquals("", Hex3.empty().lpad(0).toString())
        assertEquals("00", Hex3.empty().lpad(1).toString())
    }

    @Test
    fun `practical examples of padding functions`() {
        // 1. BER-TLV Length 필드 인코딩
        val length = 127 // 0x7F
        val lengthHex = Hex3.from(byteArrayOf(length.toByte()))
        assertEquals("7F", lengthHex.toString())

        // 2. 8바이트 고정 길이 필드로 만들기
        val pan = Hex3.from("1234567890123456")
        val paddedPan = pan.rpad(8, 0xFF.toByte())
        assertEquals("1234567890123456", paddedPan.toString()) // 이미 8바이트

        // 3. 6바이트 고정 필드로 만들기 (작은 값)
        val amount = Hex3.from("0000123456")
        val fixedAmount = amount.lpad(6)
        assertEquals("000000123456", fixedAmount.toString())

        // 4. 이진 데이터를 8바이트 블록으로 패딩 (암호화 전)
        val data = Hex3.from("123456789A")
        val paddedForEncryption = data.rpad((data.size + 7) / 8 * 8, 0x00)
        assertEquals("123456789A000000", paddedForEncryption.toString())

        // 5. AID(Application Identifier) 패딩
        val aid = Hex3.from("A0000000031010")
        val paddedAid = aid.rpad(16, 0xFF.toByte())
        assertEquals("A0000000031010FFFFFFFFFFFFFFFFFF", paddedAid.toString())
    }


}