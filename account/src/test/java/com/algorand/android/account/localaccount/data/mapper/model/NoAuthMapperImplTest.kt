package com.algorand.android.account.localaccount.data.mapper.model

import com.algorand.android.account.localaccount.data.database.model.NoAuthEntity
import com.algorand.android.account.localaccount.data.mapper.model.noauth.NoAuthMapperImpl
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class NoAuthMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()

    private val sut = NoAuthMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapped model`() {
        whenever(encryptionManager.decrypt("encrypted_address")).thenReturn("decrypted_address")

        val result = sut(NO_AUTH_ENTITY)

        val expected = LocalAccount.NoAuth("decrypted_address")
        assertEquals(expected, result)
    }

    companion object {
        private val NO_AUTH_ENTITY = fixtureOf<NoAuthEntity>().copy(encryptedAddress = "encrypted_address")
    }
}
