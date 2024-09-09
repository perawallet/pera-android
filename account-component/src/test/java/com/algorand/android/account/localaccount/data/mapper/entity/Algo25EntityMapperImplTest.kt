package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.Algo25Entity
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.Base64Manager
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class Algo25EntityMapperImplTest {

    private val tinkEncryptionManager: EncryptionManager = mock()
    private val deterministicEncryptionManager: EncryptionManager = mock()
    private val base64Manager: Base64Manager = mock()

    private val sut = Algo25EntityMapperImpl(
        deterministicEncryptionManager = deterministicEncryptionManager,
        nondeterministicEncryptionManager = tinkEncryptionManager,
        base64Manager = base64Manager
    )

    @Test
    fun `EXPECT mapped entity`() {
        whenever(deterministicEncryptionManager.encrypt("unencrypted_address")).thenReturn("encrypted_address")
        whenever(base64Manager.encode(byteArrayOf(1, 2, 3))).thenReturn("base_64_encrypted_sk")
        whenever(tinkEncryptionManager.encrypt("base_64_encrypted_sk")).thenReturn("encrypted_secret_key")

        val result = sut(LocalAccount.Algo25(address = "unencrypted_address", secretKey = byteArrayOf(1, 2, 3)))

        val expected = Algo25Entity(encryptedAddress = "encrypted_address", "encrypted_secret_key")
        assertEquals(expected, result)
    }
}
