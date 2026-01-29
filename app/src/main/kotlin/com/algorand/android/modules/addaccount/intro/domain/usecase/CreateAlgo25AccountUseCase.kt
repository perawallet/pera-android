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
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CreateAlgo25AccountUseCase @Inject constructor(
    private val algoAccountSdk: AlgoAccountSdk,
    private val aesPlatformManager: AESPlatformManager
) : CreateAlgo25Account {

    override suspend fun invoke(): Result<AccountCreation> = withContext(Dispatchers.Default) {
        val account = algoAccountSdk.createAlgo25Account()
            ?: return@withContext Result.Error(AccountCreationException("Failed to generate Algo25 account"))

        Result.Success(
            AccountCreation(
                address = account.address,
                customName = null,
                isBackedUp = false,
                type = AccountCreation.Type.Algo25(
                    aesPlatformManager.encryptByteArray(account.secretKey)
                ),
                creationType = CreationType.CREATE
            )
        )
    }
}
