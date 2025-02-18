/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.info.data.repository

import com.algorand.wallet.account.info.data.cache.AccountInformationErrorCache
import com.algorand.wallet.account.info.data.database.dao.AccountInformationDao
import com.algorand.wallet.account.info.data.database.dao.AssetHoldingDao
import com.algorand.wallet.account.info.data.mapper.AccountInformationMapper
import com.algorand.wallet.account.info.data.mapper.AssetHoldingEntityMapper
import com.algorand.wallet.account.info.data.mapper.AssetHoldingMapper
import com.algorand.wallet.account.info.data.mapper.AssetStatusEntityMapper
import com.algorand.wallet.account.info.data.service.AccountInformationApiService
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.utils.request
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

internal class AccountInformationRepositoryImpl @Inject constructor(
    private val indexerApi: AccountInformationApiService,
    private val accountInformationMapper: AccountInformationMapper,
    private val accountInformationDao: AccountInformationDao,
    private val assetHoldingDao: AssetHoldingDao,
    private val assetHoldingMapper: AssetHoldingMapper,
    private val accountInformationCacheHelper: AccountInformationCacheHelper,
    private val accountInformationFetchHelper: AccountInformationFetchHelper,
    private val assetStatusEntityMapper: AssetStatusEntityMapper,
    private val assetHoldingEntityMapper: AssetHoldingEntityMapper,
    private val accountInformationErrorCache: AccountInformationErrorCache
) : AccountInformationRepository {

    override suspend fun fetchAccountInformation(address: String): PeraResult<AccountInformation> {
        return accountInformationFetchHelper.fetchAccount(address).use(
            onSuccess = { response ->
                val accountInformation = accountInformationMapper(response)
                if (accountInformation == null) {
                    PeraResult.Error(Exception())
                } else {
                    PeraResult.Success(accountInformation)
                }
            },
            onFailed = { exception, _ ->
                PeraResult.Error(exception)
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
        return withContext(Dispatchers.IO) {
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

    override suspend fun getAllSuccessfullyCachedAccountAddresses(): List<String> {
        return accountInformationDao.getAllAddresses()
    }

    override fun getAllAccountInformationFlow(): Flow<Map<String, AccountInformation?>> {
        return combine(
            accountInformationDao.getAllAsFlow(),
            assetHoldingDao.getAllAsFlow()
        ) { accountInformationEntities, _ ->
            accountInformationEntities.associate {
                val assetEntities = assetHoldingDao.getAssetsByAddress(it.algoAddress)
                val assetHoldings = assetHoldingMapper(assetEntities)
                it.algoAddress to accountInformationMapper(it, assetHoldings)
            }
        }.distinctUntilChanged()
    }

    override suspend fun getEarliestLastFetchedRound(): Long {
        return accountInformationDao.getEarliestLastFetchedRound() ?: DEFAULT_EARLIEST_LAST_FETCHED_ROUND
    }

    override suspend fun clearCache() {
        accountInformationDao.clearAll()
        assetHoldingDao.clearAll()
    }

    override suspend fun getAccountInformation(address: String): AccountInformation? {
        val accountInformationEntity = accountInformationDao.get(address) ?: return null

        val assetEntities = assetHoldingDao.getAssetsByAddress(address)
        val assetHoldings = assetHoldingMapper(assetEntities)

        return accountInformationMapper(accountInformationEntity, assetHoldings)
    }

    override fun getAccountInformationFlow(address: String): Flow<AccountInformation?> {
        return combine(
            accountInformationDao.getAsFlow(address),
            assetHoldingDao.getAssetsByAddressAsFlow(address)
        ) { accountInformation, assetHoldingEntities ->
            if (accountInformation == null) return@combine null
            val assetHoldings = assetHoldingMapper(assetHoldingEntities)
            accountInformationMapper(accountInformation, assetHoldings)
        }.distinctUntilChanged()
    }

    override suspend fun fetchRekeyedAccounts(address: String): PeraResult<List<AccountInformation>> {
        return request { indexerApi.getRekeyedAccounts(address) }.map {
            accountInformationMapper(it)
        }
    }

    override suspend fun deleteAccountInformation(address: String) {
        accountInformationDao.delete(address)
        assetHoldingDao.deleteByAddress(address)
    }

    override suspend fun setAssetStatus(address: String, assetId: Long, status: AssetStatus) {
        val statusEntity = assetStatusEntityMapper(status)
        assetHoldingDao.updateStatus(address, assetId, statusEntity)
    }

    override suspend fun addAssetHoldingAsPending(address: String, assetId: Long) {
        val entity = assetHoldingEntityMapper(address, assetId, AssetStatus.PENDING_FOR_ADDITION)
        assetHoldingDao.insert(entity)
    }

    override suspend fun getFailedAccountInformation(): List<String> {
        return accountInformationErrorCache.getAll()
    }

    override suspend fun getRekeyAuthAddress(address: String): String? {
        return accountInformationDao.getRekeyAuthAddress(address)
    }

    companion object {
        private const val DEFAULT_EARLIEST_LAST_FETCHED_ROUND = 0L
    }
}
