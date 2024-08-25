package com.algorand.android.account.localaccount.data.mapper.model

import com.algorand.android.account.localaccount.data.database.model.Algo25Entity
import com.algorand.android.account.localaccount.data.mapper.model.algo25.Algo25MapperImpl
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class Algo25MapperImplTest {

    private val tinkEncryptionManager: EncryptionManager = mock()
    private val deterministicEncryptionManager: EncryptionManager = mock()

    private val sut = Algo25MapperImpl(
        deterministicEncryptionManager = deterministicEncryptionManager,
        nondeterministicEncryptionManager = tinkEncryptionManager
    )

    @Test
    fun `EXPECT mapped model`() {
        whenever(deterministicEncryptionManager.decrypt("encrypted_address")).thenReturn("decrypted_address")
        whenever(tinkEncryptionManager.decrypt("encrypted_secret_key")).thenReturn("decrypted_secret_key")

        val entity = Algo25Entity("encrypted_address", "encrypted_secret_key")
        val result = sut(entity)

        val expected = LocalAccount.Algo25("decrypted_address", "decrypted_secret_key".toByteArray())
        assertEquals(expected, result)
    }
}
