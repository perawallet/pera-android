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

package com.algorand.wallet.account.core.domain.usecase

import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomInfo
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.SaveJointAccount
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class AddJointAccountUseCase @Inject constructor(
    private val saveJointAccount: SaveJointAccount,
    private val setAccountCustomInfo: SetAccountCustomInfo
) : AddJointAccount {

    override suspend fun invoke(
        address: String,
        participantAddresses: List<String>,
        threshold: Int,
        version: Int,
        customName: String?,
        orderIndex: Int
    ): PeraResult<Unit> {
        return try {
            val account = LocalAccount.Joint(
                algoAddress = address,
                participantAddresses = participantAddresses,
                threshold = threshold,
                version = version
            )
            saveJointAccount(account)
            setAccountCustomInfo(CustomAccountInfo(address, customName, orderIndex, isBackedUp = true))
            PeraResult.Success(Unit)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }
}
