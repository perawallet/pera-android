package com.algorand.android.encryption

import com.google.crypto.tink.DeterministicAead
import org.junit.*
import org.mockito.kotlin.*

internal class DeterministicEncryptionManagerTest {

    private val deterministicAead: DeterministicAead = mock()
    private val base64Manager: Base64Manager = mock()

    private val sut = DeterministicEncryptionManager(deterministicAead, base64Manager)

    @Test
    fun `EXPECT base64 encoded and encrypted value WHEN encrypt is invoked`() {
        whenever(base64Manager.encode(byteArrayOf(1, 2, 3))).thenReturn(UNENCODED_ADDRESS)
        whenever(deterministicAead.encryptDeterministically("123".toByteArray(Charsets.UTF_8), null))
            .thenReturn(byteArrayOf(1, 2, 3))

        val result = sut.encrypt("123")

        Assert.assertEquals(UNENCODED_ADDRESS, result)
    }

    @Test
    fun `EXPECT decrypted value WHEN value is base64 encoded and encrypted`() {
        whenever(base64Manager.decode(ENCODED_ADDRESS)).thenReturn(byteArrayOf(1, 2, 3))
        whenever(deterministicAead.decryptDeterministically(byteArrayOf(1, 2, 3), null)).thenReturn(ADDESS_BYTE_ARRAY)

        val result = sut.decrypt(ENCODED_ADDRESS)

        val expected = "address"
        Assert.assertEquals(expected, result)
    }

    companion object {
        private const val UNENCODED_ADDRESS = "address"
        private var ADDESS_BYTE_ARRAY = byteArrayOf(97, 100, 100, 114, 101, 115, 115)
        private const val ENCODED_ADDRESS = "123"
    }
}
