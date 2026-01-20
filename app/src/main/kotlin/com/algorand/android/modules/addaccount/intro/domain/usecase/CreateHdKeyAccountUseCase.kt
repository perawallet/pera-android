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

package com.algorand.android.modules.addaccount.intro.domain.usecase

import com.algorand.android.models.AccountCreation
import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.intro.domain.exception.AccountCreationException
import com.algorand.android.ui.onboarding.creation.mapper.AccountCreationHdKeyTypeMapper
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import javax.inject.Inject

internal class CreateHdKeyAccountUseCase @Inject constructor(
    private val bip39WalletProvider: Bip39WalletProvider,
    private val accountCreationHdKeyTypeMapper: AccountCreationHdKeyTypeMapper
) : CreateHdKeyAccount {

    override fun invoke(): Result<AccountCreation> {
        return try {
            val wallet = bip39WalletProvider.createBip39Wallet()
            val hdKeyAddress = wallet.generateAddress(HdKeyAddressIndex())
            val hdKeyType = accountCreationHdKeyTypeMapper(
                wallet.getEntropy().value,
                hdKeyAddress,
                seedId = null
            )
            Result.Success(
                AccountCreation(
                    address = hdKeyAddress.address,
                    customName = null,
                    isBackedUp = false,
                    type = hdKeyType,
                    creationType = CreationType.CREATE
                )
            )
        } catch (e: Exception) {
            Result.Error(AccountCreationException("Failed to generate HD key account: ${e.message}"))
        }
    }
}
