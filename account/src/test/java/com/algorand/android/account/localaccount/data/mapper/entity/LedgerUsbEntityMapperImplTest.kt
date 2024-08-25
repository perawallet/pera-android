package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.LedgerUsbEntity
import com.algorand.android.account.localaccount.data.mapper.entity.LedgerUsbEntityMapperImpl
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

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
