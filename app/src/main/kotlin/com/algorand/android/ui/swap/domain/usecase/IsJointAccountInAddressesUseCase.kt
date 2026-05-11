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

package com.algorand.android.ui.swap.domain.usecase

import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import javax.inject.Inject

internal class IsJointAccountInAddressesUseCase @Inject constructor(
    private val getAccountRegistrationType: GetAccountRegistrationType
) : IsJointAccountInAddresses {

    override suspend fun invoke(addresses: List<String>): Boolean {
        return addresses.any { address ->
            getAccountRegistrationType(address) == AccountRegistrationType.Joint
        }
    }
}
