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

package com.algorand.android.modules.addaccount.joint.creation.usecase

import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class GetDefaultJointAccountNameUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts
) : GetDefaultJointAccountName {

    override suspend operator fun invoke(): String {
        val localAccounts = getLocalAccounts()
        val jointAccountCount = localAccounts.count { it is LocalAccount.Joint }
        return "Joint Account #${jointAccountCount + 1}"
    }
}
