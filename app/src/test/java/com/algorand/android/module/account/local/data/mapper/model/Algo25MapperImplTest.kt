package com.algorand.android.module.account.local.data.mapper.model

import com.algorand.android.module.account.local.data.database.model.Algo25Entity
import com.algorand.android.module.account.local.data.mapper.model.algo25.Algo25MapperImpl
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.encryption.Base64Manager
import com.algorand.android.module.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class Algo25MapperImplTest {

    private val tinkEncryptionManager: EncryptionManager = mock()
    private val deterministicEncryptionManager: EncryptionManager = mock()
    private val base64Manager: Base64Manager = mock()

    private val sut = Algo25MapperImpl(
        deterministicEncryptionManager = deterministicEncryptionManager,
        nondeterministicEncryptionManager = tinkEncryptionManager,
        base64Manager = base64Manager
    )

    @Test
    fun `EXPECT mapped model`() {
        whenever(deterministicEncryptionManager.decrypt("encrypted_address")).thenReturn("decrypted_address")
        whenever(tinkEncryptionManager.decrypt("encrypted_secret_key")).thenReturn("decrypted_secret_key")
        whenever(base64Manager.decode("decrypted_secret_key")).thenReturn(byteArrayOf(1, 2, 3))

        val entity = Algo25Entity("encrypted_address", "encrypted_secret_key")
        val result = sut(entity)

        val expected = LocalAccount.Algo25("decrypted_address", byteArrayOf(1, 2, 3))
        assertEquals(expected, result)
    }
}
