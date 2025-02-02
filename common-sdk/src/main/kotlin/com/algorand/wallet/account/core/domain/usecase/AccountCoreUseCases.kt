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

package com.algorand.wallet.account.core.domain.usecase

import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.foundation.PeraResult
import java.math.BigInteger
import kotlinx.coroutines.flow.Flow

fun interface AddAlgo25Account {
    suspend operator fun invoke(address: String, secretKey: ByteArray, isBackedUp: Boolean, customName: String?)
}

fun interface AddHdKeyAccount {
    suspend operator fun invoke(
        address: String,
        publicKey: ByteArray,
        privateKey: ByteArray,
        seedId: Int,
        account: Int,
        change: Int,
        keyIndex: Int,
        derivationType: Int,
        isBackedUp: Boolean,
        customName: String?
    )
}

fun interface AddLedgerBleAccount {
    suspend operator fun invoke(
        address: String,
        deviceMacAddress: String,
        indexInLedger: Int,
        customName: String?,
        bluetoothName: String?
    )
}

fun interface AddNoAuthAccount {
    suspend operator fun invoke(
        address: String,
        customName: String?
    )
}

fun interface DeleteAccount {
    suspend operator fun invoke(address: String)
}

fun interface GetAccountDetailFlow {
    operator fun invoke(address: String): Flow<AccountDetail?>
}

fun interface GetAccountsDetailsFlow {
    operator fun invoke(): Flow<List<AccountDetail>>
}

fun interface CacheAccountDetail {
    suspend operator fun invoke(address: String): PeraResult<AccountInformation>
}

fun interface FetchAccountInformationAndCacheAssets {
    suspend operator fun invoke(address: String): PeraResult<AccountInformation>
}

interface GetAccountMinBalance {
    suspend operator fun invoke(accountAddress: String): BigInteger
    suspend operator fun invoke(accountInformation: AccountInformation): BigInteger
}
