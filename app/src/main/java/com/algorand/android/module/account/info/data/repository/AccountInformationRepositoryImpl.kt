package com.algorand.android.module.account.info.data.repository

import com.algorand.android.module.account.info.data.helper.fetch.AccountInformationFetchHelper
import com.algorand.android.module.account.info.data.mapper.AccountInformationResponseMapper
import com.algorand.android.module.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.android.module.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.android.module.account.info.data.service.IndexerApi
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.repository.AccountInformationRepository
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.foundation.PeraResult
import com.algorand.android.module.network.request
import com.algorand.android.shared_db.accountinformation.dao.AccountInformationDao
import com.algorand.android.shared_db.accountinformation.dao.AssetHoldingDao
import com.algorand.android.shared_db.accountinformation.model.AssetHoldingEntity
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity.PENDING_FOR_ADDITION
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity.PENDING_FOR_REMOVAL
import java.math.BigInteger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

internal class AccountInformationRepositoryImpl(
    private val indexerApi: IndexerApi,
    private val accountInformationMapper: AccountInformationMapper,
    private val accountInformationDao: AccountInformationDao,
    private val assetHoldingDao: AssetHoldingDao,
    private val assetHoldingMapper: AssetHoldingMapper,
    private val encryptionManager: EncryptionManager,
    private val accountInformationResponseMapper: AccountInformationResponseMapper,
    private val accountInformationCacheHelper: AccountInformationCacheHelper,
    private val accountInformationFetchHelper: AccountInformationFetchHelper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AccountInformationRepository {

    override suspend fun fetchAccountInformation(address: String): Result<AccountInformation> {
        return accountInformationFetchHelper.fetchAccount(address).use(
            onSuccess = { response ->
                val accountInformation = accountInformationMapper(response)
                if (accountInformation == null) Result.failure(Exception()) else Result.success(accountInformation)
            },
            onFailed = { exception, _ ->
                Result.failure(exception)
            }
        )
    }

    override fun getCachedAccountInformationCountFlow(): Flow<Int> {
        return accountInformationDao.getTableSizeAsFlow()
    }

    override suspend fun getAllAssetHoldingIds(addresses: List<String>): List<Long> {
        return assetHoldingDao.getAssetIdsByAddresses(addresses).toSet().toList()
    }

    override suspend fun fetchAndCacheAccountInformation(
        addresses: List<String>
    ): Map<String, AccountInformation?> {
        return withContext(coroutineDispatcher) {
            val result = mutableMapOf<String, AccountInformation?>()
            addresses.map { address ->
                async {
                    result[address] = accountInformationFetchHelper.fetchAccount(address).use(
                        onSuccess = { response ->
                            accountInformationCacheHelper.cacheAccountInformation(address, response)
                        },
                        onFailed = { _, _ ->
                            null
                        }
                    )
                }
            }.awaitAll()
            result
        }
    }

    override suspend fun getAllAccountInformation(): Map<String, AccountInformation?> {
        val accountInformationMap = mutableMapOf<String, AccountInformation?>()
        accountInformationDao.getAll().forEach {
            val assetEntities = assetHoldingDao.getAssetsByAddress(it.encryptedAddress)
            val assetHoldings = assetHoldingMapper(assetEntities)
            val decryptedAddress = encryptionManager.decrypt(it.encryptedAddress)
            accountInformationMap[decryptedAddress] = accountInformationMapper(it, assetHoldings)
        }
        return accountInformationMap
    }

    override fun getAllAccountInformationFlow(): Flow<Map<String, AccountInformation?>> {
        return combine(
            accountInformationDao.getAllAsFlow(),
            assetHoldingDao.getAllAsFlow()
        ) { accountInformationEntities, _ ->
            accountInformationEntities.associate {
                val assetEntities = assetHoldingDao.getAssetsByAddress(it.encryptedAddress)
                val assetHoldings = assetHoldingMapper(assetEntities)
                val decryptedAddress = encryptionManager.decrypt(it.encryptedAddress)
                decryptedAddress to accountInformationMapper(it, assetHoldings)
            }
        }.distinctUntilChanged()
    }

    override suspend fun addAssetAdditionToAccountAssetHoldings(address: String, assetId: Long) {
        val encryptedAddress = encryptionManager.encrypt(address)
        val entity = AssetHoldingEntity(
            encryptedAddress = encryptedAddress,
            assetId = assetId,
            amount = BigInteger.ZERO,
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = null,
            optedOutAtRound = null,
            assetStatusEntity = PENDING_FOR_ADDITION
        )
        assetHoldingDao.insert(entity)
    }

    override suspend fun addAssetRemovalToAccountAssetHoldings(address: String, assetId: Long) {
        val encryptedAddress = encryptionManager.encrypt(address)
        assetHoldingDao.updateStatus(encryptedAddress, assetId, PENDING_FOR_REMOVAL)
    }

    override suspend fun getEarliestLastFetchedRound(): Long {
        return accountInformationDao.getEarliestLastFetchedRound() ?: DEFAULT_EARLIEST_LAST_FETCHED_ROUND
    }

    override suspend fun clearCache() {
        accountInformationDao.clearAll()
        assetHoldingDao.clearAll()
    }

    override suspend fun getAccountInformation(address: String): AccountInformation? {
        val encryptedAddress = encryptionManager.encrypt(address)
        val accountInformationEntity = accountInformationDao.get(encryptedAddress) ?: return null

        val assetEntities = assetHoldingDao.getAssetsByAddress(encryptedAddress)
        val assetHoldings = assetHoldingMapper(assetEntities)

        return accountInformationMapper(accountInformationEntity, assetHoldings)
    }

    override fun getAccountInformationFlow(address: String): Flow<AccountInformation?> {
        val encryptedAddress = encryptionManager.encrypt(address)
        return combine(
            accountInformationDao.getAsFlow(encryptedAddress),
            assetHoldingDao.getAssetsByAddressAsFlow(encryptedAddress)
        ) { accountInformation, assetHoldingEntities ->
            if (accountInformation == null) return@combine null
            val assetHoldings = assetHoldingMapper(assetHoldingEntities)
            accountInformationMapper(accountInformation, assetHoldings)
        }.distinctUntilChanged()
    }

    override suspend fun deleteAccountInformation(address: String) {
        val encryptedAddress = encryptionManager.encrypt(address)
        accountInformationDao.delete(encryptedAddress)
        assetHoldingDao.deleteByAddress(encryptedAddress)
    }

    override suspend fun fetchRekeyedAccounts(address: String): PeraResult<List<AccountInformation>> {
        return request { indexerApi.getRekeyedAccounts(address) }.use(
            onSuccess = { response ->
                val accountInformationList = accountInformationMapper(response)
                PeraResult.Success(accountInformationList)
            },
            onFailed = { exception, code ->
                if (code == 404) {
                    val emptyAccountResponse = accountInformationResponseMapper.createEmptyAccount(address)
                    val emptyAccountInfo = accountInformationMapper(emptyAccountResponse)
                    if (emptyAccountInfo == null) {
                        PeraResult.Error(exception)
                    } else {
                        PeraResult.Success(listOf(emptyAccountInfo))
                    }
                } else {
                    PeraResult.Error(exception)
                }
            }
        )
    }

    companion object {
        private const val DEFAULT_EARLIEST_LAST_FETCHED_ROUND = 0L
    }
}
