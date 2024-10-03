package com.algorand.android.module.account.local.data.repository

import com.algorand.android.module.account.local.data.database.dao.LedgerBleDao
import com.algorand.android.module.account.local.data.database.model.LedgerBleEntity
import com.algorand.android.module.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.android.module.account.local.data.mapper.model.ledgerble.LedgerBleMapper
import com.algorand.android.module.account.local.domain.model.LocalAccount.LedgerBle
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LedgerBleAccountRepositoryImplTest {

    private val ledgerBleDao: LedgerBleDao = mock()
    private val ledgerBleEntityMapper: LedgerBleEntityMapper = mock()
    private val ledgerBleMapper: LedgerBleMapper = mock()
    private val encryptionManager: EncryptionManager = mock()
    private val sut = LedgerBleAccountRepositoryImpl(
        ledgerBleDao,
        ledgerBleEntityMapper,
        ledgerBleMapper,
        encryptionManager
    )

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(LEDGER_BLE_1_ENTITY, LEDGER_BLE_2_ENTITY)
        whenever(ledgerBleDao.getAll()).thenReturn(entities)
        whenever(ledgerBleMapper(LEDGER_BLE_1_ENTITY)).thenReturn(LEDGER_BLE_1)
        whenever(ledgerBleMapper(LEDGER_BLE_2_ENTITY)).thenReturn(LEDGER_BLE_2)

        val localAccounts = sut.getAll()

        val expectedReturnedList = listOf(LEDGER_BLE_1, LEDGER_BLE_2)
        verify(ledgerBleDao, times(1)).getAll()
        assertEquals(expectedReturnedList, localAccounts)
    }

    @Test
    fun `EXPECT account WHEN account was registered before`() = runTest {
        whenever(ledgerBleMapper(LEDGER_BLE_1_ENTITY)).thenReturn(LEDGER_BLE_1)
        whenever(ledgerBleDao.get(LEDGER_BLE_1_ENTITY.encryptedAddress)).thenReturn(LEDGER_BLE_1_ENTITY)
        whenever(encryptionManager.encrypt(LEDGER_BLE_1.address)).thenReturn(LEDGER_BLE_1_ENTITY.encryptedAddress)

        val localAccount = sut.getAccount(LEDGER_BLE_1.address)

        verify(ledgerBleDao, times(1)).get(LEDGER_BLE_1_ENTITY.encryptedAddress)
        assertEquals(LEDGER_BLE_1, localAccount)
    }

    @Test
    fun `EXPECT account to be added to database  WHEN addAccount is invoked`() = runTest {
        whenever(ledgerBleEntityMapper(LEDGER_BLE_1)).thenReturn(LEDGER_BLE_1_ENTITY)

        sut.addAccount(LEDGER_BLE_1)

        verify(ledgerBleDao).insert(LEDGER_BLE_1_ENTITY)
    }

    @Test
    fun `EXPECT account to be deleted WHEN deleteAccount is invoked`() = runTest {
        whenever(encryptionManager.encrypt("address")).thenReturn("encryptedAddress")

        sut.deleteAccount("address")

        verify(ledgerBleDao).delete("encryptedAddress")
    }

    @Test
    fun `EXPECT all accounts to be deleted WHEN deleteAllAccounts is invoked`() = runTest {
        sut.deleteAllAccounts()

        verify(ledgerBleDao).clearAll()
    }

    @Test
    fun `EXPECT all accounts WHEN getAllAsFlow is invoked`() = runTest {
        val entities = listOf(LEDGER_BLE_1_ENTITY, LEDGER_BLE_2_ENTITY)
        whenever(ledgerBleDao.getAllAsFlow()).thenReturn(flowOf(entities))
        whenever(ledgerBleMapper(LEDGER_BLE_1_ENTITY)).thenReturn(LEDGER_BLE_1)
        whenever(ledgerBleMapper(LEDGER_BLE_2_ENTITY)).thenReturn(LEDGER_BLE_2)

        val localAccounts = sut.getAllAsFlow().toList().first()

        val expectedReturnedList = listOf(LEDGER_BLE_1, LEDGER_BLE_2)
        verify(ledgerBleDao, times(1)).getAllAsFlow()
        assertEquals(expectedReturnedList, localAccounts)
    }

    companion object {
        private val LEDGER_BLE_1 = fixtureOf<LedgerBle>()
        private val LEDGER_BLE_1_ENTITY = fixtureOf<LedgerBleEntity>()
        private val LEDGER_BLE_2 = fixtureOf<LedgerBle>()
        private val LEDGER_BLE_2_ENTITY = fixtureOf<LedgerBleEntity>()
    }
}
