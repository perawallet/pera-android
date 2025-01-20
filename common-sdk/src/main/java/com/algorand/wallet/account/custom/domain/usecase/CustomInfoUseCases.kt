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

package com.algorand.wallet.account.custom.domain.usecase

import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.model.CustomInfo

fun interface SetAccountCustomName {
    suspend operator fun invoke(address: String, name: String)
}

fun interface GetAccountCustomName {
    suspend operator fun invoke(address: String): String?
}

fun interface SetAccountCustomInfo {
    suspend operator fun invoke(customInfo: CustomInfo)
}

fun interface GetAccountCustomInfoOrNull {
    suspend operator fun invoke(address: String): CustomInfo?
}

fun interface DeleteAccountCustomInfo {
    suspend operator fun invoke(address: String)
}

fun interface GetAccountCustomInfo {
    suspend operator fun invoke(address: String): CustomInfo
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

fun interface GetAccountAsbBackUpStatus {
    suspend operator fun invoke(accountAddress: String): Boolean
}

fun interface GetAllAccountOrderIndexes {
    suspend operator fun invoke(): List<AccountOrderIndex>
}
