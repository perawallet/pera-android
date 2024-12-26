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

package com.algorand.common.account.info.domain.usecase

import com.algorand.common.account.info.domain.model.AccountCacheStatus
import com.algorand.common.account.info.domain.model.AccountInformation
import kotlinx.coroutines.flow.Flow

fun interface ClearAccountInformationCache {
    suspend operator fun invoke()
}

fun interface FetchAndCacheAccountInformation {
    suspend operator fun invoke(addresses: List<String>): Map<String, AccountInformation?>
}

fun interface GetAllAccountInformation {
    suspend operator fun invoke(): Map<String, AccountInformation?>
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
