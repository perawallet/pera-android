package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.LedgerBleEntity
import com.algorand.android.module.account.local.data.mapper.entity.LedgerBleEntityMapperImpl
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class LedgerBleEntityMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()

    private val sut = LedgerBleEntityMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapper entity`() {
        whenever(encryptionManager.encrypt("unencrypted_address")).thenReturn("encrypted_address")
        val result = sut(
            LocalAccount.LedgerBle(
                address = "unencrypted_address",
                deviceMacAddress = "mac_address",
                indexInLedger = 1
            )
        )

        val expected = LedgerBleEntity(
            encryptedAddress = "encrypted_address",
            deviceMacAddress = "mac_address",
            accountIndexInLedger = 1
        )
        assertEquals(expected, result)
    }
}
