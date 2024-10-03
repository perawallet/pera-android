package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.LedgerUsbEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class LedgerUsbEntityMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()

    private val sut = LedgerUsbEntityMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapped entity`() {
        whenever(encryptionManager.encrypt("unencrypted_address")).thenReturn("encrypted_address")

        val result = sut(
            LocalAccount.LedgerUsb(
                address = "unencrypted_address",
                deviceId = 123,
                indexInLedger = 1
            )
        )

        val expected = LedgerUsbEntity(
            encryptedAddress = "encrypted_address",
            ledgerUsbId = 123,
            accountIndexInLedger = 1
        )
        assertEquals(expected, result)
    }
}
