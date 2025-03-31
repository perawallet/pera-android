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

package com.algorand.android.usecase

import com.algorand.android.models.BaseAccountAddress
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import javax.inject.Inject

class AccountAddressUseCase @Inject constructor(
    private val getAccountCustomName: GetAccountCustomName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) {
    suspend fun getAccountAddress(address: String): BaseAccountAddress.AccountAddress {
        return BaseAccountAddress.AccountAddress(
            publicKey = address,
            accountIconDrawablePreview = getAccountIconDrawablePreview(address),
            displayName = getAccountCustomName(address)
        )
    }
}
