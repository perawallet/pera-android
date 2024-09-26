package com.algorand.android.module.account.local.data.repository

import com.algorand.android.module.account.local.data.database.dao.LedgerUsbDao
import com.algorand.android.module.account.local.data.database.model.LedgerUsbEntity
import com.algorand.android.module.account.local.data.mapper.entity.LedgerUsbEntityMapper
import com.algorand.android.module.account.local.data.mapper.model.ledgerusb.LedgerUsbMapper
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

class LedgerUsbAccountRepositoryImplTest {

    private val ledgerUsbDao: LedgerUsbDao = mock()
    private val ledgerUsbEntityMapper: LedgerUsbEntityMapper = mock()
    private val ledgerUsbMapper: LedgerUsbMapper = mock()
    private val encryptionManager: EncryptionManager = mock()
    private val sut = LedgerUsbAccountRepositoryImpl(
        ledgerUsbDao,
        ledgerUsbEntityMapper,
        ledgerUsbMapper,
        encryptionManager
    )

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(LEDGER_USB_1_ENTITY, LEDGER_USB_2_ENTITY)
        whenever(ledgerUsbDao.getAll()).thenReturn(entities)
        whenever(ledgerUsbMapper(LEDGER_USB_1_ENTITY)).thenReturn(LEDGER_USB_1)
        whenever(ledgerUsbMapper(LEDGER_USB_2_ENTITY)).thenReturn(LEDGER_USB_2)

        val localAccounts = sut.getAll()

        val expectedReturnedList = listOf(LEDGER_USB_1, LEDGER_USB_2)
        verify(ledgerUsbDao, times(1)).getAll()
        assertEquals(expectedReturnedList, localAccounts)
    }

    @Test
    fun `EXPECT account WHEN account was registered before`() = runTest {
        whenever(ledgerUsbMapper(LEDGER_USB_1_ENTITY)).thenReturn(LEDGER_USB_1)
        whenever(ledgerUsbDao.get(LEDGER_USB_1_ENTITY.encryptedAddress)).thenReturn(LEDGER_USB_1_ENTITY)
        whenever(encryptionManager.encrypt(LEDGER_USB_1.address)).thenReturn(LEDGER_USB_1_ENTITY.encryptedAddress)

        val localAccount = sut.getAccount(LEDGER_USB_1.address)

        verify(ledgerUsbDao, times(1)).get(LEDGER_USB_1_ENTITY.encryptedAddress)
        assertEquals(LEDGER_USB_1, localAccount)
    }

    @Test
    fun `EXPECT account to be added to database  WHEN addAccount is invoked`() = runTest {
        whenever(ledgerUsbEntityMapper(LEDGER_USB_1)).thenReturn(LEDGER_USB_1_ENTITY)

        sut.addAccount(LEDGER_USB_1)

        verify(ledgerUsbDao).insert(LEDGER_USB_1_ENTITY)
    }

    @Test
    fun `EXPECT account to be deleted WHEN deleteAccount is invoked`() = runTest {
        whenever(encryptionManager.encrypt("address")).thenReturn("encryptedAddress")

        sut.deleteAccount("address")

        verify(ledgerUsbDao).delete("encryptedAddress")
    }

    @Test
    fun `EXPECT all accounts to be deleted WHEN deleteAllAccounts is invoked`() = runTest {
        sut.deleteAllAccounts()

        verify(ledgerUsbDao).clearAll()
    }

    @Test
    fun `EXPECT all accounts WHEN getAllAsFlow is invoked`() = runTest {
        val entities = listOf(LEDGER_USB_1_ENTITY, LEDGER_USB_2_ENTITY)
        whenever(ledgerUsbDao.getAllAsFlow()).thenReturn(flowOf(entities))
        whenever(ledgerUsbMapper(LEDGER_USB_1_ENTITY)).thenReturn(LEDGER_USB_1)
        whenever(ledgerUsbMapper(LEDGER_USB_2_ENTITY)).thenReturn(LEDGER_USB_2)

        val localAccounts = sut.getAllAsFlow().toList().first()

        val expectedReturnedList = listOf(LEDGER_USB_1, LEDGER_USB_2)
        verify(ledgerUsbDao, times(1)).getAllAsFlow()
        assertEquals(expectedReturnedList, localAccounts)
    }

    companion object {
        private val LEDGER_USB_1 = fixtureOf<LocalAccount.LedgerUsb>()
        private val LEDGER_USB_1_ENTITY = fixtureOf<LedgerUsbEntity>()
        private val LEDGER_USB_2 = fixtureOf<LocalAccount.LedgerUsb>()
        private val LEDGER_USB_2_ENTITY = fixtureOf<LedgerUsbEntity>()
    }
}
