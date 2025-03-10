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

package com.algorand.wallet.account.info.domain.repository

import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.foundation.PeraResult
import kotlinx.coroutines.flow.Flow

internal interface AccountInformationRepository {

    suspend fun fetchAccountInformation(address: String): PeraResult<AccountInformation>

    suspend fun getAccountInformation(address: String): AccountInformation?

    fun getCachedAccountInformationCountFlow(): Flow<Int>

    suspend fun getAllSuccessfullyCachedAccountAddresses(): List<String>

    suspend fun fetchAndCacheAccountInformation(addresses: List<String>): Map<String, AccountInformation?>

    suspend fun getEarliestLastFetchedRound(): Long

    suspend fun clearCache()

    suspend fun deleteAccountInformation(address: String)

    suspend fun getAllAssetHoldingIds(addresses: List<String>): List<Long>

    fun getAllAccountInformationFlow(): Flow<Map<String, AccountInformation?>>

    fun getAccountInformationFlow(address: String): Flow<AccountInformation?>

    suspend fun fetchRekeyedAccounts(address: String): PeraResult<List<AccountInformation>>

    suspend fun setAssetStatus(address: String, assetId: Long, status: AssetStatus)

    suspend fun addAssetHoldingAsPending(address: String, assetId: Long)

    fun getAssetHoldingsFlow(address: String): Flow<List<AssetHolding>>

    suspend fun getFailedAccountInformation(): List<String>

    suspend fun getRekeyAuthAddress(address: String): String?
    
    suspend fun getFilteredRekeyedAccountCount(authAddress: String, algoAddresses: List<String>): Int
}
