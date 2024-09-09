package com.algorand.android.account.accountinformation.data.repository

import com.algorand.android.account.accountinformation.data.mapper.entity.*
import com.algorand.android.account.accountinformation.data.mapper.model.*
import com.algorand.android.account.accountinformation.data.model.*
import com.algorand.android.account.accountinformation.data.service.IndexerApi
import com.algorand.android.account.accountinformation.domain.model.*
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.accountinformation.dao.*
import com.algorand.android.shared_db.accountinformation.model.*
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

class AccountInformationRepositoryImplTest {

    private val indexerApi: IndexerApi = mock()
    private val accountInformationMapper: AccountInformationMapper = mock()
    private val accountInformationEntityMapper: com.algorand.android.accountinfo.component.data.mapper.entity.AccountInformationEntityMapper =
        mock()
    private val accountInformationErrorEntityMapper: AccountInformationErrorEntityMapper = mock()
    private val assetHoldingEntityMapper: AssetHoldingEntityMapper = mock()
    private val accountInformationDao: AccountInformationDao = mock()
    private val assetHoldingDao: AssetHoldingDao = mock()
    private val assetHoldingMapper: AssetHoldingMapper = mock()
    private val encryptionManager: EncryptionManager = mock()

    private val sut = AccountInformationRepositoryImpl(
        indexerApi,
        accountInformationEntityMapper,
        accountInformationErrorEntityMapper,
        accountInformationMapper,
        accountInformationDao,
        assetHoldingDao,
        assetHoldingEntityMapper,
        assetHoldingMapper,
        encryptionManager
    )

    @Test
    fun `EXPECT account information WHEN getAccountInformation is called and account was cached before`() = runTest {
        whenever(encryptionManager.encrypt(ADDRESS)).thenReturn(ENCRYPTED_ADDRESS)
        whenever(accountInformationDao.get(ENCRYPTED_ADDRESS)).thenReturn(ACCOUNT_INFORMATION_ENTITY)
        whenever(assetHoldingDao.getAssetsByAddress(ENCRYPTED_ADDRESS)).thenReturn(ASSET_HOLDING_ENTITY_LIST)
        whenever(assetHoldingMapper(ASSET_HOLDING_ENTITY_LIST)).thenReturn(ASSET_HOLDING_LIST)
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_ENTITY, ASSET_HOLDING_LIST))
            .thenReturn(ACCOUNT_INFORMATION)

        val result = sut.getAccountInformation(ADDRESS)

        assertEquals(ACCOUNT_INFORMATION, result)
    }

    @Test
    fun `EXPECT null WHEN getAccountInformation is called and account was not cached before`() = runTest {
        whenever(encryptionManager.encrypt(ADDRESS)).thenReturn(ENCRYPTED_ADDRESS)
        whenever(accountInformationDao.get(ENCRYPTED_ADDRESS)).thenReturn(null)

        val result = sut.getAccountInformation(ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT account information and not being cached WHEN fetch account information is called`() = runTest {
        whenever(indexerApi.getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION_RESPONSE)
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(ACCOUNT_INFORMATION)

        val result = sut.fetchAccountInformation(ADDRESS)

        assertEquals(Result.success(ACCOUNT_INFORMATION), result)
    }

    @Test
    fun `EXPECT failure result WHEN fetching is successful but mapping returns null`() = runTest {
        whenever(indexerApi.getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION_RESPONSE)
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(null)

        val result = sut.fetchAccountInformation(ADDRESS)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test
    fun `EXPECT failure result WHEN fetchAccountInformation api call throws exception`() = runTest {
        whenever(indexerApi.getAccountInformation(ADDRESS)).doAnswer { throw Exception() }

        val result = sut.fetchAccountInformation(ADDRESS)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test(timeout = 200L)
    fun `EXPECT fetch information call to be async`() = runTest {
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(ACCOUNT_INFORMATION)
        whenever(indexerApi.getAccountInformation(any(), eq(false))).doAnswer {
            Thread.sleep(100)
            ACCOUNT_INFORMATION_RESPONSE
        }

        val accountAddresses = listOf("address1", "address2", "address3", "address4", "address5", "address6")
        sut.fetchAndCacheAccountInformation(accountAddresses)
    }

    @Test
    fun `EXPECT fetched information to be cached and returned WHEN response and mapping successful`() = runTest {
        whenever(indexerApi.getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION_RESPONSE)
        whenever(indexerApi.getAccountInformation("address2")).thenReturn(ACCOUNT_INFORMATION_RESPONSE_2)
        whenever(indexerApi.getAccountInformation("address3")).doAnswer { throw Exception() }
        whenever(accountInformationEntityMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(ACCOUNT_INFORMATION_ENTITY)
        whenever(accountInformationEntityMapper(ACCOUNT_INFORMATION_RESPONSE_2)).thenReturn(null)
        whenever(
            assetHoldingEntityMapper(
                ADDRESS,
                ASSET_HOLDING_LIST_RESPONSE[0]
            )
        ).thenReturn(ASSET_HOLDING_ENTITY_LIST[0])
        whenever(assetHoldingMapper(listOf(ASSET_HOLDING_ENTITY_LIST[0]))).thenReturn(ASSET_HOLDING_LIST)
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_ENTITY, ASSET_HOLDING_LIST))
            .thenReturn(ACCOUNT_INFORMATION)

        val result = sut.fetchAndCacheAccountInformation(listOf("address1", "address2", "address3"))

        val expected = mapOf(
            ADDRESS to ACCOUNT_INFORMATION,
            "address2" to null,
            "address3" to null
        )
        assertEquals(expected, result)
        verify(accountInformationDao).insert(ACCOUNT_INFORMATION_ENTITY)
        verify(accountInformationDao, times(1)).insert(any())
    }

    @Test
    fun `EXPECT account and asset holding tables to be cleared WHEN clearCache is called`() = runTest {
        sut.clearCache()

        verify(accountInformationDao).clearAll()
        verify(assetHoldingDao).clearAll()
    }

    @Test
    fun `EXPECT last fetched round WHEN getEarliestLastFetchedRound is called`() = runTest {
        whenever(accountInformationDao.getEarliestLastFetchedRound()).thenReturn(768L)

        val result = sut.getEarliestLastFetchedRound()

        assertEquals(768L, result)
    }

    @Test
    fun `EXPECT default value WHEN getEarliestLastFetchedRound is called and there is no value in db`() = runTest {
        whenever(accountInformationDao.getEarliestLastFetchedRound()).thenReturn(null)

        val result = sut.getEarliestLastFetchedRound()

        assertEquals(0L, result)
    }

    @Test
    fun `EXPECT account and asset holdings to be deleted WHEN delete account information is called`() = runTest {
        whenever(encryptionManager.encrypt(ADDRESS)).thenReturn(ENCRYPTED_ADDRESS)

        sut.deleteAccountInformation(ADDRESS)

        verify(accountInformationDao).delete(ENCRYPTED_ADDRESS)
        verify(assetHoldingDao).deleteByAddress(ENCRYPTED_ADDRESS)
    }

    @Test
    fun `EXPECT error account information to be cached WHEN fetch and cache is called and entity is null`() = runTest {
        whenever(indexerApi.getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION_RESPONSE)
        whenever(accountInformationEntityMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(null)
        whenever(accountInformationErrorEntityMapper(ADDRESS)).thenReturn(ACCOUNT_INFORMATION_ENTITY)

        sut.fetchAndCacheAccountInformation(listOf(ADDRESS))

        verify(accountInformationErrorEntityMapper).invoke(ADDRESS)
        verify(accountInformationDao).insert(ACCOUNT_INFORMATION_ENTITY)
    }

    @Test
    fun `EXPECT getAllAccountInformation to return all account information`() = runTest {
        whenever(encryptionManager.decrypt("encrypted_error_address")).thenReturn("decrypted_error_address")
        whenever(encryptionManager.decrypt(ACCOUNT_INFORMATION_ENTITY.encryptedAddress))
            .thenReturn(ACCOUNT_INFORMATION.address)
        whenever(accountInformationDao.getAll())
            .thenReturn(
                listOf(
                    ACCOUNT_INFORMATION_ENTITY,
                    ACCOUNT_INFORMATION_ERROR_ENTITY.copy(encryptedAddress = "encrypted_error_address")
                )
            )
        whenever(assetHoldingDao.getAssetsByAddress(ACCOUNT_INFORMATION_ENTITY.encryptedAddress))
            .thenReturn(ASSET_HOLDING_ENTITY_LIST)
        whenever(assetHoldingDao.getAssetsByAddress(ACCOUNT_INFORMATION_ERROR_ENTITY.encryptedAddress))
            .thenReturn(emptyList())
        whenever(assetHoldingMapper(ASSET_HOLDING_ENTITY_LIST))
            .thenReturn(ASSET_HOLDING_LIST)
        whenever(assetHoldingMapper(emptyList())).thenReturn(emptyList())
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_ENTITY, ASSET_HOLDING_LIST))
            .thenReturn(ACCOUNT_INFORMATION)
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_ERROR_ENTITY, emptyList()))
            .thenReturn(null)

        val result = sut.getAllAccountInformation()

        val expected = mapOf<String, AccountInformation?>(
            ACCOUNT_INFORMATION.address to ACCOUNT_INFORMATION,
            "decrypted_error_address" to null
        )
        assertEquals(expected, result)
    }

    companion object {
        private const val ADDRESS = "address1"
        private const val ENCRYPTED_ADDRESS = "encrypted_address"
        private val ASSET_HOLDING_LIST_RESPONSE = fixtureOf<List<AssetHoldingResponse>>()
        private val ACCOUNT_INFORMATION_PAYLOAD = fixtureOf<AccountInformationResponsePayloadResponse>().copy(
            address = ADDRESS,
            allAssetHoldingList = ASSET_HOLDING_LIST_RESPONSE
        )
        private val ACCOUNT_INFORMATION_RESPONSE = fixtureOf<AccountInformationResponse>().copy(
            accountInformation = ACCOUNT_INFORMATION_PAYLOAD
        )
        private val ACCOUNT_INFORMATION_RESPONSE_2 = fixtureOf<AccountInformationResponse>()
        private val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>()
        private val ACCOUNT_INFORMATION_ENTITY = fixtureOf<AccountInformationEntity>()
        private val ACCOUNT_INFORMATION_ERROR_ENTITY = fixtureOf<AccountInformationEntity>()
        private val ASSET_HOLDING_ENTITY_LIST = fixtureOf<List<AssetHoldingEntity>>()
        private val ASSET_HOLDING_LIST = fixtureOf<List<AssetHolding>>()
    }
}
