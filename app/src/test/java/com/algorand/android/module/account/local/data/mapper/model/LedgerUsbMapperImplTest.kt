package com.algorand.android.module.account.local.data.mapper.model

import com.algorand.android.module.account.local.data.database.model.LedgerUsbEntity
import com.algorand.android.module.account.local.data.mapper.model.ledgerusb.LedgerUsbMapperImpl
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class LedgerUsbMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()

    private val sut = LedgerUsbMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapped model`() {
        whenever(encryptionManager.decrypt("encrypted_address")).thenReturn("decrypted_address")

        val result = sut(LEDGER_USB_ENTITY)

        val expected = LocalAccount.LedgerUsb(
            address = "decrypted_address",
            deviceId = LEDGER_USB_ENTITY.ledgerUsbId,
            indexInLedger = LEDGER_USB_ENTITY.accountIndexInLedger
        )
        Assert.assertEquals(expected, result)
    }

    companion object {
        private val LEDGER_USB_ENTITY = fixtureOf<LedgerUsbEntity>().copy(encryptedAddress = "encrypted_address")
    }
}
