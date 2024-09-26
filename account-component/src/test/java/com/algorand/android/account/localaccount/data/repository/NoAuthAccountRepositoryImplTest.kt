package com.algorand.android.module.account.local.data.repository

import com.algorand.android.module.account.local.data.database.dao.NoAuthDao
import com.algorand.android.module.account.local.data.database.model.NoAuthEntity
import com.algorand.android.module.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.android.module.account.local.data.mapper.model.noauth.NoAuthMapper
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.encryption.EncryptionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

class NoAuthAccountRepositoryImplTest {

    private val noAuthDao: NoAuthDao = mock()
    private val noAuthEntityMapper: NoAuthEntityMapper = mock()
    private val noAuthMapper: NoAuthMapper = mock()
    private val encryptionManager: EncryptionManager = mock()
    private val sut = NoAuthAccountRepositoryImpl(
        noAuthDao,
        noAuthEntityMapper,
        noAuthMapper,
        encryptionManager
    )

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(NoAuthEntity("address1"), NoAuthEntity("address2"))
        whenever(noAuthDao.getAll()).thenReturn(entities)
        whenever(noAuthMapper(entities[0])).thenReturn(LocalAccount.NoAuth("address1"))
        whenever(noAuthMapper(entities[1])).thenReturn(LocalAccount.NoAuth("address2"))

        val localAccounts = sut.getAll()

        val expectedReturnedList = listOf(LocalAccount.NoAuth("address1"), LocalAccount.NoAuth("address2"))
        verify(noAuthDao, times(1)).getAll()
        assertEquals(expectedReturnedList, localAccounts)
    }

    @Test
    fun `EXPECT account WHEN account was registered before`() = runTest {
        whenever(noAuthMapper(NoAuthEntity("encrypted_address"))).thenReturn(LocalAccount.NoAuth("address"))
        whenever(noAuthDao.get("encrypted_address")).thenReturn(NoAuthEntity("encrypted_address"))
        whenever(encryptionManager.encrypt("address")).thenReturn("encrypted_address")

        val localAccount = sut.getAccount("address")

        val expectedAccount = LocalAccount.NoAuth("address")
        verify(noAuthDao, times(1)).get("encrypted_address")
        assertEquals(expectedAccount, localAccount)
    }

    @Test
    fun `EXPECT account to be added to database WHEN addAccount is invoked`() = runTest {
        val account = LocalAccount.NoAuth("address")
        val ledgerUsbEntity = NoAuthEntity("encryptedAddress")
        whenever(noAuthEntityMapper(account)).thenReturn(ledgerUsbEntity)

        sut.addAccount(account)

        verify(noAuthDao).insert(ledgerUsbEntity)
    }

    @Test
    fun `EXPECT account to be deleted WHEN deleteAccount is invoked`() = runTest {
        whenever(encryptionManager.encrypt("address")).thenReturn("encryptedAddress")

        sut.deleteAccount("address")

        verify(noAuthDao).delete("encryptedAddress")
    }

    @Test
    fun `EXPECT all accounts to be deleted WHEN deleteAllAccounts is invoked`() = runTest {
        sut.deleteAllAccounts()

        verify(noAuthDao).clearAll()
    }

    @Test
    fun `EXPECT all accounts WHEN getAllAsFlow is invoked`() = runTest {
        val entities = listOf(NoAuthEntity("address1"), NoAuthEntity("address2"))
        whenever(noAuthDao.getAllAsFlow()).thenReturn(flowOf(entities))
        whenever(noAuthMapper(entities[0])).thenReturn(LocalAccount.NoAuth("address1"))
        whenever(noAuthMapper(entities[1])).thenReturn(LocalAccount.NoAuth("address2"))

        val localAccounts = sut.getAllAsFlow().toList().first()

        val expectedReturnedList = listOf(LocalAccount.NoAuth("address1"), LocalAccount.NoAuth("address2"))
        verify(noAuthDao, times(1)).getAllAsFlow()
        assertEquals(expectedReturnedList, localAccounts)
    }
}
