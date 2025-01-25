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

import com.algorand.wallet.account.custom.domain.model.CustomInfo
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomInfo
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.SaveAlgo25Account
import javax.inject.Inject

internal class AddAlgo25AccountUseCase @Inject constructor(
    private val saveAlgo25Account: SaveAlgo25Account,
    private val setCustomInfo: SetAccountCustomInfo
) : AddAlgo25Account {

    override suspend fun invoke(address: String, secretKey: ByteArray, isBackedUp: Boolean, customName: String?) {
        val account = LocalAccount.Algo25(address)
        saveAlgo25Account(account, secretKey)
        setCustomInfo(CustomInfo(address, customName, Int.MAX_VALUE, isBackedUp))
    }
}
