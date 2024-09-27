/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.core.component.domain.model.AccountTotalValue
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.module.account.info.domain.model.AccountInformation
import java.math.BigInteger
import kotlinx.coroutines.flow.Flow

interface AddAccount {

    suspend fun addAlgo25(
        address: String,
        secretKey: ByteArray,
        isBackedUp: Boolean,
        customName: String?
    )

    suspend fun addLedgerBle(
        address: String,
        deviceMacAddress: String,
        indexInLedger: Int,
        customName: String?
    )

    suspend fun addNoAuth(
        address: String,
        customName: String?
    )
}

interface DeleteAccount {
    suspend operator fun invoke(address: String)
}

interface GetAccountCollectibleDataFlow {
    operator fun invoke(address: String): Flow<List<BaseOwnedCollectibleData>>
}

interface GetAccountCollectiblesData {
    suspend operator fun invoke(address: String): List<BaseOwnedCollectibleData>
    suspend operator fun invoke(accountInformation: AccountInformation): List<BaseOwnedCollectibleData>
}

interface GetAccountMinBalance {
    suspend operator fun invoke(accountAddress: String): BigInteger
    suspend operator fun invoke(accountInformation: AccountInformation): BigInteger
}

interface GetAccountTotalValue {
    suspend operator fun invoke(address: String, includeAlgo: Boolean): AccountTotalValue
}

interface GetAccountTotalValueFlow {
    suspend operator fun invoke(address: String, includeAlgo: Boolean): Flow<AccountTotalValue>
}

interface GetNotBackedUpAccounts {
    suspend operator fun invoke(): List<String>
}

interface UpdateAccountName {
    suspend operator fun invoke(address: String, name: String?)
}

interface HasAccountAnyRekeyedAccount {
    suspend operator fun invoke(address: String): Boolean
}

interface GetRekeyedAccountAddresses {
    suspend operator fun invoke(address: String): List<String>
}
