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

package com.algorand.common.account.core.domain.usecase

import com.algorand.common.account.custom.domain.model.CustomInfo
import com.algorand.common.account.custom.domain.usecase.SetAccountCustomInfo
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.account.local.domain.usecase.SaveNoAuthAccount

internal class AddNoAuthAccountUseCase(
    private val saveNoAuthAccount: SaveNoAuthAccount,
    private val setAccountCustomInfo: SetAccountCustomInfo
) : AddNoAuthAccount {

    override suspend fun invoke(address: String, customName: String?) {
        val account = LocalAccount.NoAuth(address)
        saveNoAuthAccount(account)
        setAccountCustomInfo(CustomInfo(address, customName, Int.MAX_VALUE, isBackedUp = true))
    }
}
