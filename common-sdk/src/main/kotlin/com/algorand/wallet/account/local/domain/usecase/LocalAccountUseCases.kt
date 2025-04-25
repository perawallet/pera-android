/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.local.domain.usecase

import com.algorand.wallet.account.local.domain.model.AccountMnemonic
import com.algorand.wallet.account.local.domain.model.HdSeed
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.foundation.PeraResult
import kotlinx.coroutines.flow.Flow

internal fun interface SaveHdKeyAccount {
    suspend operator fun invoke(account: LocalAccount.HdKey, privateKey: ByteArray)
}

internal fun interface SaveAlgo25Account {
    suspend operator fun invoke(account: LocalAccount.Algo25, privateKey: ByteArray)
}

internal fun interface SaveLedgerBleAccount {
    suspend operator fun invoke(account: LocalAccount.LedgerBle)
}

internal fun interface SaveNoAuthAccount {
    suspend operator fun invoke(account: LocalAccount.NoAuth)
}

fun interface GetAlgoAddressFromHdPublicKey {
    suspend operator fun invoke(publicKey: ByteArray): String
}

fun interface CreateHdKeyAccount {
    suspend operator fun invoke(
        algoAddress: String,
        publicKey: ByteArray,
        privateKey: ByteArray,
        seedId: Int,
        account: Int,
        change: Int,
        keyIndex: Int,
        derivationType: Int
    )
}

fun interface CreateAlgo25Account {
    suspend operator fun invoke(address: String, secretKey: ByteArray)
}

fun interface CreateLedgerBleAccount {
    suspend operator fun invoke(address: String, deviceMacAddress: String, indexInLedger: Int)
}

fun interface CreateNoAuthAccount {
    suspend operator fun invoke(address: String)
}

fun interface DeleteLocalAccount {
    suspend operator fun invoke(address: String)
}

fun interface DeleteAllLocalAccounts {
    suspend operator fun invoke()
}

fun interface GetAllLocalAccountAddressesAsFlow {
    operator fun invoke(): Flow<List<String>>
}

fun interface GetLedgerBleAccount {
    suspend operator fun invoke(address: String): LocalAccount.LedgerBle?
}

fun interface GetHdPublicKeyFromAlgoAddress {
    suspend operator fun invoke(address: String): ByteArray
}

fun interface GetLocalAccountsFlow {
    operator fun invoke(): Flow<List<LocalAccount>>
}

fun interface GetLocalAccountCountFlow {
    operator fun invoke(): Flow<Int>
}

fun interface GetLocalAccountCount {
    suspend operator fun invoke(): Int
}

fun interface GetLocalAccounts {
    suspend operator fun invoke(): List<LocalAccount>
}

fun interface GetLocalAccountsAddresses {
    suspend operator fun invoke(): List<String>
}

fun interface GetLocalAccount {
    suspend operator fun invoke(address: String): LocalAccount?
}

fun interface GetAlgo25SecretKey {
    suspend operator fun invoke(address: String): ByteArray?
}

fun interface GetHdKeyPrivateKey {
    suspend operator fun invoke(address: String): ByteArray?
}

fun interface GetHdEntropy {
    suspend operator fun invoke(seedId: Int): ByteArray?
}

fun interface GetHdSeed {
    suspend operator fun invoke(seedId: Int): ByteArray?
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

fun interface UpdateNoAuthAccountToHdKey {
    suspend operator fun invoke(
        address: String,
        publicKey: ByteArray,
        privateKey: ByteArray,
        seedId: Int,
        account: Int,
        change: Int,
        keyIndex: Int,
        derivationType: Int
    )
}

fun interface GetAccountMnemonic {
    suspend operator fun invoke(address: String): PeraResult<AccountMnemonic>
}

fun interface UpdateNoAuthAccountToLedgerBle {
    suspend operator fun invoke(
        address: String, deviceMacAddress: String, bluetoothName: String, indexInLedger: Int
    )
}

fun interface GetMaxHdSeedId {
    suspend operator fun invoke(): Int?
}

fun interface GetHasAnyHdSeedId {
    suspend operator fun invoke(): Boolean
}

fun interface GetSeedIdIfExistingEntropy {
    suspend operator fun invoke(entropy: ByteArray): Int?
}

fun interface GetAllHdSeeds {
    suspend operator fun invoke(): List<HdSeed>
}
