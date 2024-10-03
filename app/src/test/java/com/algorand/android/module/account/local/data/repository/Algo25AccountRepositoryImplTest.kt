package com.algorand.android.module.account.local.data.repository

import com.algorand.android.module.account.local.data.database.dao.Algo25Dao
import com.algorand.android.module.account.local.data.database.model.Algo25Entity
import com.algorand.android.module.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.android.module.account.local.data.mapper.model.algo25.Algo25Mapper
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.encryption.EncryptionManager
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class Algo25AccountRepositoryImplTest {

    private val algo25Dao: Algo25Dao = mock()
    private val algo25EntityMapper: Algo25EntityMapper = mock()
    private val algo25Mapper: Algo25Mapper = mock()
    private val encryptionManager: EncryptionManager = mock()
    private val sut = Algo25AccountRepositoryImpl(
        algo25Dao,
        algo25EntityMapper,
        algo25Mapper,
        encryptionManager
    )

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(
            Algo25Entity("encryptedAddress1", "encryptedSecretKey1"),
            Algo25Entity("encryptedAddress2", "encryptedSecretKey2")
        )
        whenever(algo25Dao.getAll()).thenReturn(entities)
        whenever(algo25Mapper(entities[0])).thenReturn(LocalAccount.Algo25("address1", byteArrayOf(1, 2, 3)))
        whenever(algo25Mapper(entities[1])).thenReturn(LocalAccount.Algo25("address2", byteArrayOf(4, 5, 6)))

        val localAccounts = sut.getAll()

        val expectedReturnedList = listOf(
            LocalAccount.Algo25("address1", byteArrayOf(1, 2, 3)),
            LocalAccount.Algo25("address2", byteArrayOf(4, 5, 6))
        )
        verify(algo25Dao).getAll()
        assertEquals(expectedReturnedList, localAccounts)
    }

    @Test
    fun `EXPECT account WHEN account was registered before`() = runTest {
        whenever(algo25Mapper(Algo25Entity("encryptedAddress", "encryptedSecretKey")))
            .thenReturn(LocalAccount.Algo25("address", byteArrayOf(1, 2, 3)))
        whenever(algo25Dao.get("encryptedAddress")).thenReturn(Algo25Entity("encryptedAddress", "encryptedSecretKey"))
        whenever(encryptionManager.encrypt("address")).thenReturn("encryptedAddress")

        val localAccount = sut.getAccount("address")

        val expectedAccount = LocalAccount.Algo25("address", byteArrayOf(1, 2, 3))
        verify(algo25Dao, times(1)).get("encryptedAddress")
        assertEquals(expectedAccount, localAccount)
    }

    @Test
    fun `EXPECT account to be added to database WHEN addAccount is invoked`() = runTest {
        val account = LocalAccount.Algo25("address", byteArrayOf(1, 2, 3))
        val ledgerUsbEntity = Algo25Entity("encryptedAddress", "encryptedSecretKey")
        whenever(algo25EntityMapper(account)).thenReturn(ledgerUsbEntity)

        sut.addAccount(account)

        verify(algo25Dao).insert(ledgerUsbEntity)
    }

    @Test
    fun `EXPECT account to be deleted from database  WHEN deleteAccount is invoked`() = runTest {
        val address = "address"
        val encryptedAddress = "encryptedAddress"
        whenever(encryptionManager.encrypt(address)).thenReturn(encryptedAddress)

        sut.deleteAccount(address)

        verify(algo25Dao).delete(encryptedAddress)
    }

    @Test
    fun `EXPECT all accounts to be deleted from database WHEN deleteAllAccounts is invoked`() = runTest {
        sut.deleteAllAccounts()

        verify(algo25Dao).clearAll()
    }

    @Test
    fun `EXPECT all accounts as flow WHEN getAllAsFlow is invoked`() {
        val entities = listOf(
            Algo25Entity("encryptedAddress1", "encryptedSecretKey1"),
            Algo25Entity("encryptedAddress2", "encryptedSecretKey2")
        )
        val localAccounts = listOf(
            LocalAccount.Algo25("address1", byteArrayOf(1, 2, 3)),
            LocalAccount.Algo25("address2", byteArrayOf(4, 5, 6))
        )
        whenever(algo25Dao.getAllAsFlow()).thenReturn(flowOf(entities))
        whenever(algo25Mapper(entities[0])).thenReturn(localAccounts[0])
        whenever(algo25Mapper(entities[1])).thenReturn(localAccounts[1])

        val flow = sut.getAllAsFlow()

        runTest {
            val returnedList = flow.toList()
            assertEquals(listOf(localAccounts), returnedList)
        }
    }
}
