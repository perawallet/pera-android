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

package com.algorand.wallet.account.custom.domain.usecase

import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import com.algorand.wallet.account.custom.domain.model.CustomHdSeedInfo
import com.algorand.wallet.account.custom.domain.model.HdSeedOrderIndex
import kotlinx.coroutines.flow.Flow

fun interface SetAccountCustomName {
    suspend operator fun invoke(address: String, name: String)
}

fun interface GetAccountCustomName {
    suspend operator fun invoke(address: String): String?
}

fun interface GetAccountsCustomInfoFlow {
    operator fun invoke(addresses: List<String>): Flow<Map<String, CustomAccountInfo?>>
}

fun interface GetAccountsCustomInfo {
    suspend operator fun invoke(addresses: List<String>): Map<String, CustomAccountInfo?>
}

fun interface SetAccountCustomInfo {
    suspend operator fun invoke(customInfo: CustomAccountInfo)
}

fun interface GetAccountCustomInfoOrNull {
    suspend operator fun invoke(address: String): CustomAccountInfo?
}

fun interface DeleteAccountCustomInfo {
    suspend operator fun invoke(address: String)
}

fun interface GetAccountCustomInfo {
    suspend operator fun invoke(address: String): CustomAccountInfo
}

fun interface SetAccountOrderIndex {
    suspend operator fun invoke(address: String, orderIndex: Int)
}

fun interface GetBackedUpAccounts {
    suspend operator fun invoke(): Set<String>
}

fun interface GetNotBackedUpAccounts {
    suspend operator fun invoke(): Set<String>
}

fun interface GetAccountBackUpStatus {
    suspend operator fun invoke(accountAddress: String): Boolean
}

fun interface SetAddressesBackedUp {
    suspend operator fun invoke(accountAddresses: Set<String>)
}

fun interface GetAllAccountOrderIndexes {
    suspend operator fun invoke(): List<AccountOrderIndex>
}

fun interface ClearAllCustomInformation {
    suspend operator fun invoke()
}

// custom_hd_seed_info

fun interface SetHdSeedCustomName {
    suspend operator fun invoke(seedId: Int, name: String)
}

fun interface GetHdSeedCustomName {
    suspend operator fun invoke(seedId: Int): String?
}

fun interface SetHdSeedCustomInfo {
    suspend operator fun invoke(customInfo: CustomHdSeedInfo)
}

fun interface GetHdSeedCustomInfoOrNull {
    suspend operator fun invoke(seedId: Int): CustomHdSeedInfo?
}

fun interface DeleteHdSeedCustomInfo {
    suspend operator fun invoke(aseedId: Int)
}

fun interface GetHdSeedCustomInfo {
    suspend operator fun invoke(seedId: Int): CustomHdSeedInfo
}

fun interface SetHdSeedOrderIndex {
    suspend operator fun invoke(seedId: Int, orderIndex: Int)
}

fun interface GetBackedUpHdSeeds {
    suspend operator fun invoke(): Set<String>
}

fun interface GetNotBackedUpHdSeeds {
    suspend operator fun invoke(): Set<String>
}

fun interface GetHdSeedAsbBackUpStatus {
    suspend operator fun invoke(seedId: Int): Boolean
}

fun interface GetAllHdSeedOrderIndexes {
    suspend operator fun invoke(): List<HdSeedOrderIndex>
}
