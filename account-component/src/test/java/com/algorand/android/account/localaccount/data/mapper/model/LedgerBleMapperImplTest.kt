package com.algorand.android.account.localaccount.data.mapper.model

import com.algorand.android.account.localaccount.data.database.model.LedgerBleEntity
import com.algorand.android.account.localaccount.data.mapper.model.ledgerble.LedgerBleMapperImpl
import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class LedgerBleMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()

    private val sut = LedgerBleMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapper model`() {
        whenever(encryptionManager.decrypt("encrypted_address")).thenReturn("decrypted_address")

        val result = sut(LEDGER_BLE_ENTITY)

        val expected = LocalAccount.LedgerBle(
            address = "decrypted_address",
            deviceMacAddress = LEDGER_BLE_ENTITY.deviceMacAddress,
            indexInLedger = LEDGER_BLE_ENTITY.accountIndexInLedger
        )
        assertEquals(expected, result)
    }

    companion object {
        private val LEDGER_BLE_ENTITY = fixtureOf<LedgerBleEntity>().copy(encryptedAddress = "encrypted_address")
    }
}
