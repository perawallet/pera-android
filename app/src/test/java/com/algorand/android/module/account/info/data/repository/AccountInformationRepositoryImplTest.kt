package com.algorand.android.module.account.info.data.repository

import com.algorand.android.module.account.info.data.helper.fetch.AccountInformationFetchHelper
import com.algorand.android.module.account.info.data.mapper.AccountInformationResponseMapper
import com.algorand.android.module.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.android.module.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.android.module.account.info.data.model.AccountInformationResponse
import com.algorand.android.module.account.info.data.model.AccountInformationResponsePayloadResponse
import com.algorand.android.module.account.info.data.model.AssetHoldingResponse
import com.algorand.android.module.account.info.data.service.IndexerApi
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.model.AssetHolding
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.shareddb.accountinformation.dao.AccountInformationDao
import com.algorand.android.module.shareddb.accountinformation.dao.AssetHoldingDao
import com.algorand.android.module.shareddb.accountinformation.model.AccountInformationEntity
import com.algorand.android.module.shareddb.accountinformation.model.AssetHoldingEntity
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AccountInformationRepositoryImplTest {

    private val indexerApi: IndexerApi = mock()
    private val accountInformationMapper: AccountInformationMapper = mock()
    private val accountInformationDao: AccountInformationDao = mock()
    private val assetHoldingDao: AssetHoldingDao = mock()
    private val assetHoldingMapper: AssetHoldingMapper = mock()
    private val accountInformationResponseMapper: AccountInformationResponseMapper = mock()
    private val accountInformationCacheHelper: AccountInformationCacheHelper = mock()
    private val accountInformationFetchHelper: AccountInformationFetchHelper = mock()
    private val encryptionManager: EncryptionManager = mock()

    private val sut = AccountInformationRepositoryImpl(
        indexerApi,
        accountInformationMapper,
        accountInformationDao,
        assetHoldingDao,
        assetHoldingMapper,
        encryptionManager,
        accountInformationResponseMapper,
        accountInformationCacheHelper,
        accountInformationFetchHelper
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
        whenever(accountInformationFetchHelper.fetchAccount(ADDRESS))
            .thenReturn(PeraResult.Success(ACCOUNT_INFORMATION_RESPONSE))
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(ACCOUNT_INFORMATION)

        val result = sut.fetchAccountInformation(ADDRESS)

        assertEquals(Result.success(ACCOUNT_INFORMATION), result)
    }

    @Test
    fun `EXPECT failure result WHEN fetching is successful but mapping returns null`() = runTest {
        whenever(accountInformationFetchHelper.fetchAccount(ADDRESS))
            .thenReturn(PeraResult.Success(ACCOUNT_INFORMATION_RESPONSE))
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(null)

        val result = sut.fetchAccountInformation(ADDRESS)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test(timeout = 200L)
    fun `EXPECT fetch information call to be async`() = runTest {
        whenever(accountInformationMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(ACCOUNT_INFORMATION)
        whenever(accountInformationFetchHelper.fetchAccount(any())).doAnswer {
            Thread.sleep(100)
            PeraResult.Success(ACCOUNT_INFORMATION_RESPONSE)
        }

        val accountAddresses = listOf("address1", "address2", "address3", "address4", "address5", "address6")
        sut.fetchAndCacheAccountInformation(accountAddresses)
    }

    @Test
    fun `EXPECT fetched information to be cached and returned WHEN response and mapping successful`() = runTest {
        whenever(accountInformationFetchHelper.fetchAccount(ADDRESS))
            .thenReturn(PeraResult.Success(ACCOUNT_INFORMATION_RESPONSE))
        whenever(accountInformationFetchHelper.fetchAccount("address2"))
            .thenReturn(PeraResult.Error(Exception()))
        whenever(accountInformationCacheHelper.cacheAccountInformation(ADDRESS, ACCOUNT_INFORMATION_RESPONSE))
            .thenReturn(ACCOUNT_INFORMATION)
        val result = sut.fetchAndCacheAccountInformation(listOf("address1", "address2"))

        val expected = mapOf(
            ADDRESS to ACCOUNT_INFORMATION,
            "address2" to null
        )
        assertEquals(expected, result)
        verify(accountInformationCacheHelper).cacheAccountInformation(ADDRESS, ACCOUNT_INFORMATION_RESPONSE)
        verify(accountInformationCacheHelper, times(1)).cacheAccountInformation(any(), any())
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
//
//    @Test
//    fun `EXPECT error account information to be cached WHEN fetch and cache is called and entity is null`() = runTest {
//        whenever(indexerApi.getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION_RESPONSE)
//        whenever(accountInformationEntityMapper(ACCOUNT_INFORMATION_RESPONSE)).thenReturn(null)
//        whenever(accountInformationErrorEntityMapper(ADDRESS)).thenReturn(ACCOUNT_INFORMATION_ENTITY)
//
//        sut.fetchAndCacheAccountInformation(listOf(ADDRESS))
//
//        verify(accountInformationErrorEntityMapper).invoke(ADDRESS)
//        verify(accountInformationDao).insert(ACCOUNT_INFORMATION_ENTITY)
//    }

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
        private const val EXCLUDES = "exclude"
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
