package com.algorand.android.module.account.info.domain.repository

import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.foundation.PeraResult
import kotlinx.coroutines.flow.Flow

internal interface AccountInformationRepository {

    suspend fun fetchAccountInformation(address: String): Result<AccountInformation>

    suspend fun getAccountInformation(address: String): AccountInformation?

    fun getAccountInformationFlow(address: String): Flow<AccountInformation?>

    fun getCachedAccountInformationCountFlow(): Flow<Int>

    suspend fun getAllAccountInformation(): Map<String, AccountInformation?>

    suspend fun fetchAndCacheAccountInformation(addresses: List<String>): Map<String, AccountInformation?>

    suspend fun getEarliestLastFetchedRound(): Long

    suspend fun clearCache()

    suspend fun deleteAccountInformation(address: String)

    suspend fun fetchRekeyedAccounts(address: String): PeraResult<List<AccountInformation>>

    suspend fun getAllAssetHoldingIds(addresses: List<String>): List<Long>

    fun getAllAccountInformationFlow(): Flow<Map<String, AccountInformation?>>

    suspend fun addAssetAdditionToAccountAssetHoldings(address: String, assetId: Long)

    suspend fun addAssetRemovalToAccountAssetHoldings(address: String, assetId: Long)
}
