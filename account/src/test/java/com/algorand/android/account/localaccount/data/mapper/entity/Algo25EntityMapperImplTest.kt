package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.Algo25Entity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class Algo25EntityMapperImplTest {

    private val tinkEncryptionManager: EncryptionManager = mock()
    private val deterministicEncryptionManager: EncryptionManager = mock()

    private val sut = Algo25EntityMapperImpl(
        deterministicEncryptionManager = deterministicEncryptionManager,
        nondeterministicEncryptionManager = tinkEncryptionManager
    )

    @Test
    fun `EXPECT mapped entity`() {
        whenever(deterministicEncryptionManager.encrypt("unencrypted_address")).thenReturn("encrypted_address")
        whenever(tinkEncryptionManager.encrypt(String(byteArrayOf(1, 2, 3)))).thenReturn("encrypted_secret_key")

        val result = sut(LocalAccount.Algo25(address = "unencrypted_address", secretKey = byteArrayOf(1, 2, 3)))

        val expected = Algo25Entity(encryptedAddress = "encrypted_address", "encrypted_secret_key")
        assertEquals(expected, result)
    }
}
