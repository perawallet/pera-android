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

package com.algorand.wallet.account.local.domain.usecase

import com.algorand.wallet.account.local.domain.model.AccountMnemonic
import com.algorand.wallet.account.local.domain.model.AccountMnemonic.AccountType
import com.algorand.wallet.account.local.domain.model.AccountMnemonic.AccountType.Algo25
import com.algorand.wallet.account.local.domain.model.AccountMnemonic.AccountType.HdKey
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class GetAccountMnemonicUseCase @Inject constructor(
    private val getLocalAccount: GetLocalAccount,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdEntropy: GetHdEntropy,
    private val algoAccountSdk: AlgoAccountSdk,
    private val bip39Sdk: PeraBip39Sdk
) : GetAccountMnemonic {

    override suspend fun invoke(address: String): PeraResult<AccountMnemonic> {
        val localAccount = getLocalAccount(address)
        return when (localAccount) {
            is LocalAccount.Algo25 -> getAlgo25Mnemonic(address)
            is LocalAccount.HdKey -> getHdKeyMnemonic(address)
            else -> PeraResult.Error(IllegalArgumentException())
        }
    }

    private suspend fun getAlgo25Mnemonic(address: String): PeraResult<AccountMnemonic> {
        val secretKey = getAlgo25SecretKey(address) ?: return PeraResult.Error(IllegalArgumentException())
        val mnemonic = algoAccountSdk.getMnemonicFromAlgo25SecretKey(secretKey)
        return getAccountMnemonic(mnemonic, Algo25)
    }

    private suspend fun getHdKeyMnemonic(address: String): PeraResult<AccountMnemonic> {
        val localAccount = getLocalAccount(address)
        if (localAccount !is LocalAccount.HdKey) {
            return PeraResult.Error(IllegalArgumentException("Account is not an HD key account."))
        }

        val entropy = getHdEntropy(localAccount.seedId) ?:
            return PeraResult.Error(IllegalArgumentException("HD entropy not found for seed"))

        val mnemonic = bip39Sdk.getMnemonicFromEntropy(entropy)
        return getAccountMnemonic(mnemonic, HdKey)
    }

    private fun getAccountMnemonic(mnemonic: String?, type: AccountType): PeraResult<AccountMnemonic> {
        return if (mnemonic.isNullOrBlank()) {
            PeraResult.Error(IllegalArgumentException())
        } else {
            val mnemonicWords = splitMnemonicIntoWords(mnemonic)
            PeraResult.Success(AccountMnemonic(mnemonicWords, type))
        }
    }

    private fun splitMnemonicIntoWords(mnemonic: String): List<String> = mnemonic.split(" ")
}
