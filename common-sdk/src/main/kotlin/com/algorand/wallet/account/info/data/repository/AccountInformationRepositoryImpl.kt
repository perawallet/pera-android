/*
 * Copyright 2022-2025 Pera Wallet, LDA
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
import com.algorand.wallet.account.info.data.mapper.entity.AssetHoldingEntityMapper
import com.algorand.wallet.account.info.data.mapper.entity.AssetStatusEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountAssetAndAppsCountMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.wallet.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.wallet.account.info.data.model.AccountInformationResponse
import com.algorand.wallet.account.info.data.service.AccountInformationApiService
import com.algorand.wallet.account.info.domain.model.AccountAssetAndAppsCount
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.account.lite.domain.model.AccountLiteInformation
import com.algorand.wallet.account.lite.domain.model.AssetHoldingLite
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.utils.request
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
    private val accountInformationErrorCache: AccountInformationErrorCache,
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses,
    private val accountAssetAndAppsCountMapper: AccountAssetAndAppsCountMapper
) : AccountInformationRepository {

    override suspend fun fetchAccountInformation(
        address: String,
        includeClosedAccount: Boolean
    ): PeraResult<AccountInformation> {
        return accountInformationFetchHelper.fetchAccount(address, includeClosedAccount).mapToAccountInfo()
    }

    override suspend fun fetchAccountInformationWithoutAssets(
        address: String,
        includeClosedAccount: Boolean
    ): PeraResult<AccountInformation> {
        return accountInformationFetchHelper.fetchAccountWithoutAssets(address, includeClosedAccount).mapToAccountInfo()
    }

    override fun getCachedAccountInformationCountFlow(): Flow<Int> {
        return combine(
            accountInformationDao.getTableSizeAsFlow(),
            accountInformationErrorCache.getAsFlow()
        ) { cachedAccounts, errorAccounts ->
            cachedAccounts + errorAccounts.size
        }
    }

    override suspend fun getAllAssetHoldingIds(addresses: List<String>): List<Long> {
        return assetHoldingDao.getAssetIdsByAddresses(addresses).toSet().toList()
    }

    override suspend fun isAssetOptedInByAnyLocalAccount(assetId: Long): Boolean {
        val localAccountAddresses = getLocalAccountsAddresses()
        return assetHoldingDao.isAssetOptedInByAnyLocalAccount(localAccountAddresses, assetId)
    }

    override suspend fun fetchAndCacheAccountInformation(
        addresses: List<String>
    ): Map<String, AccountInformation?> {
        return withContext(Dispatchers.IO) {
            val result = mutableMapOf<String, AccountInformation?>()
            addresses.map { address ->
                async {
                    result[address] = accountInformationFetchHelper.fetchAccount(
                        address,
                        includeClosedAccount = false
                    ).use(
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

    override fun getAssetHoldingsFlow(address: String): Flow<List<AssetHolding>> {
        return assetHoldingDao.getAssetsByAddressAsFlow(address).map { assetHoldingMapper(it) }
    }

    override fun getAssetHoldingFlow(address: String, assetId: Long): Flow<AssetHolding?> {
        return assetHoldingDao.getAssetHoldingAsFlow(address, assetId).map {
            if (it == null) return@map null
            assetHoldingMapper(it)
        }.distinctUntilChanged()
    }

    override suspend fun getFailedAccountInformation(): List<String> {
        return accountInformationErrorCache.getAll()
    }

    override suspend fun getRekeyAuthAddress(address: String): String? {
        return accountInformationDao.getRekeyAuthAddress(address)
    }

    override suspend fun getFilteredRekeyedAccountCount(authAddress: String, algoAddresses: List<String>): Int {
        return accountInformationDao.getAuthAccountCountFilteredByAddress(authAddress, algoAddresses)
    }

    override suspend fun getAccountAlgoBalance(address: String): BigInteger? {
        return accountInformationDao.getAccountAlgoBalance(address)
    }

    override fun getAccountsLiteInformationFlow(addresses: List<String>): Flow<Map<String, AccountLiteInformation?>> {
        return accountInformationDao.getAccountLiteInformationFlow(addresses).map { accountLiteInformationList ->
            accountLiteInformationList.associate { accountLiteInformation ->
                accountLiteInformation.address to AccountLiteInformation(
                    address = accountLiteInformation.address,
                    rekeyAuthAddress = accountLiteInformation.rekeyAuthAddress,
                    algoBalance = accountLiteInformation.algoBalance,
                    minRequiredBalance = accountLiteInformation.minRequiredBalance
                )
            }
        }
    }

    override fun getAssetHoldingsLiteFlow(addresses: List<String>): Flow<Map<String, AssetHoldingLite>> {
        return assetHoldingDao.getAssetHoldingsLiteInformationFlow(addresses).map { assetHoldingLiteList ->
            val assetHoldingMap = mutableMapOf<String, AssetHoldingLite>()
            assetHoldingLiteList.forEach { assetHoldingLite ->
                val assetHolding = assetHoldingMap[assetHoldingLite.address]
                assetHoldingMap[assetHoldingLite.address] = if (assetHolding == null) {
                    AssetHoldingLite(
                        assetHoldingLite.address,
                        mapOf(assetHoldingLite.assetId to assetHoldingLite.amount)
                    )
                } else {
                    assetHolding.copy(
                        assetHoldingAmounts = assetHolding.assetHoldingAmounts + (assetHoldingLite.assetId to assetHoldingLite.amount)
                    )
                }
            }
            assetHoldingMap
        }
    }

    override suspend fun getAccountAssetHoldingAmount(address: String, assetId: Long): BigInteger? {
        return assetHoldingDao.getAssetHoldingAmount(address, assetId)
    }

    override suspend fun getCachedAccountMinRequiredBalance(address: String): BigInteger? {
        return accountInformationDao.getMinRequiredBalance(address)
    }

    override suspend fun isAssetOptedInByAccount(address: String, assetId: Long): Boolean {
        return assetHoldingDao.isAssetOptedInByAccount(address, assetId)
    }

    override suspend fun getAccountAssetsAndAppsCount(address: String): AccountAssetAndAppsCount? {
        return accountInformationDao.getAssetsAndAppsCount(address)?.let {
            accountAssetAndAppsCountMapper.map(it)
        }
    }

    override suspend fun getAssetHolding(address: String, assetId: Long): AssetHolding? {
        return assetHoldingDao.getAssetHolding(address, assetId)?.let { assetHoldingMapper(it) }
    }

    override suspend fun getAssetHoldings(address: String): List<AssetHolding> {
        return assetHoldingDao.getAssetsByAddress(address).map { assetHoldingMapper(it) }
    }

    private suspend fun PeraResult<AccountInformationResponse>.mapToAccountInfo(): PeraResult<AccountInformation> {
        return use(
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

    companion object {
        private const val DEFAULT_EARLIEST_LAST_FETCHED_ROUND = 0L
    }
}
