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

package com.algorand.common.account.info.data.repository

import com.algorand.common.account.info.data.database.dao.AccountInformationDao
import com.algorand.common.account.info.data.database.dao.AssetHoldingDao
import com.algorand.common.account.info.data.mapper.AccountInformationMapper
import com.algorand.common.account.info.data.mapper.AssetHoldingMapper
import com.algorand.common.account.info.domain.model.AccountInformation
import com.algorand.common.account.info.domain.repository.AccountInformationRepository
import com.algorand.common.foundation.PeraResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

internal class AccountInformationRepositoryImpl(
    private val accountInformationMapper: AccountInformationMapper,
    private val accountInformationDao: AccountInformationDao,
    private val assetHoldingDao: AssetHoldingDao,
    private val assetHoldingMapper: AssetHoldingMapper,
    private val accountInformationCacheHelper: AccountInformationCacheHelper,
    private val accountInformationFetchHelper: AccountInformationFetchHelper
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

    override suspend fun getAllAccountInformation(): Map<String, AccountInformation?> {
        val accountInformationMap = mutableMapOf<String, AccountInformation?>()
        accountInformationDao.getAll().forEach {
            val assetEntities = assetHoldingDao.getAssetsByAddress(it.algoAddress)
            val assetHoldings = assetHoldingMapper(assetEntities)
            accountInformationMap[it.algoAddress] = accountInformationMapper(it, assetHoldings)
        }
        return accountInformationMap
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

    override suspend fun deleteAccountInformation(address: String) {
        accountInformationDao.delete(address)
        assetHoldingDao.deleteByAddress(address)
    }

    companion object {
        private const val DEFAULT_EARLIEST_LAST_FETCHED_ROUND = 0L
    }
}
