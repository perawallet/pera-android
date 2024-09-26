package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.NoAuthEntity
import com.algorand.android.module.account.local.data.mapper.entity.NoAuthEntityMapperImpl
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class NoAuthEntityMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()

    private val sut = NoAuthEntityMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapped entity`() {
        whenever(encryptionManager.encrypt("unencrypted_address")).thenReturn("encrypted_address")

        val result = sut(LocalAccount.NoAuth(address = "unencrypted_address"))

        val expected = NoAuthEntity(encryptedAddress = "encrypted_address")
        assertEquals(expected, result)
    }
}
