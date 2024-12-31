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

package com.algorand.common.account.local.domain.usecase

import com.algorand.common.account.local.domain.model.LocalAccount
import kotlinx.coroutines.flow.Flow

internal fun interface SaveBip39Account {
    suspend operator fun invoke(account: LocalAccount.Bip39)
}

internal fun interface SaveAlgo25Account {
    suspend operator fun invoke(account: LocalAccount.Algo25)
}

internal fun interface SaveLedgerBleAccount {
    suspend operator fun invoke(account: LocalAccount.LedgerBle)
}

internal fun interface SaveNoAuthAccount {
    suspend operator fun invoke(account: LocalAccount.NoAuth)
}

fun interface DeleteLocalAccount {
    suspend operator fun invoke(address: String)
}

fun interface GetAllLocalAccountAddressesAsFlow {
    operator fun invoke(): Flow<List<String>>
}

fun interface GetLedgerBleAccount {
    suspend operator fun invoke(address: String): LocalAccount.LedgerBle?
}

fun interface GetLocalAccountCountFlow {
    operator fun invoke(): Flow<Int>
}

fun interface GetLocalAccounts {
    suspend operator fun invoke(): List<LocalAccount>
}

fun interface GetSecretKey {
    suspend operator fun invoke(address: String): ByteArray?
}

fun interface IsThereAnyAccountWithAddress {
    suspend operator fun invoke(address: String): Boolean
}

fun interface IsThereAnyNoAuthAccountWithAddress {
    suspend operator fun invoke(address: String): Boolean
}

fun interface IsThereAnyLocalAccount {
    suspend operator fun invoke(): Boolean
}

fun interface UpdateNoAuthAccountToAlgo25 {
    suspend operator fun invoke(address: String, secretKey: ByteArray)
}

fun interface UpdateNoAuthAccountToBip39 {
    suspend operator fun invoke(address: String, secretKey: ByteArray)
}

fun interface UpdateNoAuthAccountToLedgerBle {
    suspend operator fun invoke(address: String, deviceMacAddress: String, bluetoothName: String, indexInLedger: Int)
}
