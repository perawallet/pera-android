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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.model.AccountCacheStatus
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.foundation.PeraResult
import java.math.BigInteger
import kotlinx.coroutines.flow.Flow

fun interface ClearAccountInformationCache {
    suspend operator fun invoke()
}

fun interface FetchAndCacheAccountInformation {
    suspend operator fun invoke(addresses: List<String>): Map<String, AccountInformation?>
}

fun interface FetchAccountInformationWithoutAssets {
    suspend operator fun invoke(address: String, includeDeletedAccount: Boolean): PeraResult<AccountInformation>
}

fun interface GetAllSuccessfullyCachedAccountAddresses {
    suspend operator fun invoke(): List<String>
}

fun interface GetAllFailedCachedAccountAddresses {
    suspend operator fun invoke(): List<String>
}

fun interface GetAllAssetHoldingIds {
    suspend operator fun invoke(accountAddresses: List<String>): List<Long>
}

fun interface GetCachedAccountInformationCountFlow {
    operator fun invoke(): Flow<Int>
}

fun interface GetEarliestLastFetchedRound {
    suspend operator fun invoke(): Long
}

fun interface GetAccountDetailCacheStatusFlow {
    operator fun invoke(): Flow<AccountCacheStatus>
}

fun interface GetAllAccountInformationFlow {
    operator fun invoke(): Flow<Map<String, AccountInformation?>>
}

fun interface GetAccountInformation {
    suspend operator fun invoke(address: String): AccountInformation?
}

fun interface GetAccountInformationFlow {
    operator fun invoke(address: String): Flow<AccountInformation?>
}

interface IsThereAnyCachedErrorAccount {
    suspend operator fun invoke(excludeNoAuthAccounts: Boolean): Boolean
    suspend operator fun invoke(localAccounts: List<LocalAccount>, excludeNoAuthAccounts: Boolean): Boolean
}

interface IsThereAnyCachedSuccessAccount {
    suspend operator fun invoke(excludeNoAuthAccounts: Boolean): Boolean
    suspend operator fun invoke(localAccounts: List<LocalAccount>, excludeNoAuthAccounts: Boolean): Boolean
}

interface IsAssetOwnedByAccount {
    suspend operator fun invoke(address: String, assetId: Long): Boolean
    suspend operator fun invoke(accountInfo: AccountInformation, assetId: Long): Boolean
}

fun interface IsAssetOptedInByAnyLocalAccount {
    suspend operator fun invoke(assetId: Long): Boolean
}

fun interface IsAssetOptedInByAccount {
    suspend operator fun invoke(address: String, assetId: Long): Boolean
}

fun interface DeleteAccountInformation {
    suspend operator fun invoke(address: String)
}

fun interface FetchAccountInformation {
    suspend operator fun invoke(address: String, includeDeletedAccount: Boolean): PeraResult<AccountInformation>
}

fun interface FetchRekeyedAccounts {
    suspend operator fun invoke(address: String): PeraResult<List<AccountInformation>>
}

fun interface SetAccountAssetStatus {
    suspend operator fun invoke(address: String, assetId: Long, status: AssetStatus)
}

fun interface AddAssetHoldingToAccountAsPending {
    suspend operator fun invoke(address: String, assetId: Long)
}

fun interface GetAccountAssetHoldingsFlow {
    operator fun invoke(address: String): Flow<List<AssetHolding>>
}

fun interface GetAccountAssetHoldingFlow {
    operator fun invoke(address: String, assetId: Long): Flow<AssetHolding?>
}

fun interface GetAccountAssetHolding {
    suspend operator fun invoke(address: String, assetId: Long): AssetHolding?
}

fun interface GetAccountAssetHoldings {
    suspend operator fun invoke(address: String): List<AssetHolding>
}

fun interface IsAccountCachedSuccessfully {
    suspend operator fun invoke(address: String): Boolean
}

fun interface GetAccountRekeyAdminAddress {
    suspend operator fun invoke(address: String): String?
}

fun interface GetAccountFastLookup {
    suspend operator fun invoke(address: String): PeraResult<AccountFastLookup>
}

fun interface GetAccountAlgoBalance {
    suspend operator fun invoke(address: String): BigInteger?
}

fun interface GetRegisteredHdKeys {
    suspend operator fun invoke(entropy: ByteArray): List<RegisteredHdKey>
}

fun interface GetAccountAssetHoldingAmount {
    suspend operator fun invoke(address: String, assetId: Long): BigInteger?
}

fun interface IsAccountOptedInToAnyAsset {
    suspend operator fun invoke(address: String): Boolean
}

fun interface IsAccountOptedInToAnyApp {
    suspend operator fun invoke(address: String): Boolean
}
