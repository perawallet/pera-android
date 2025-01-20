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

package com.algorand.wallet.account.custom.domain.repository

import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.model.CustomInfo

internal interface CustomInfoRepository {

    suspend fun getCustomInfo(address: String): CustomInfo

    suspend fun getCustomInfoOrNull(address: String): CustomInfo?

    suspend fun setCustomInfo(customInfo: CustomInfo)

    suspend fun setCustomName(address: String, name: String)

    suspend fun getCustomName(address: String): String?

    suspend fun setOrderIndex(address: String, orderIndex: Int)

    suspend fun deleteCustomInfo(address: String)

    suspend fun getNotBackedUpAccounts(): Set<String>

    suspend fun getBackedUpAccounts(): Set<String>

    suspend fun isAccountBackedUp(accountAddress: String): Boolean

    suspend fun getAllAccountOrderIndexes(): List<AccountOrderIndex>
}
