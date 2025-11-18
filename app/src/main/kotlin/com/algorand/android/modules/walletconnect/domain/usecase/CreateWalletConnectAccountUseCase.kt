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

package com.algorand.android.modules.walletconnect.domain.usecase

import com.algorand.android.models.WalletConnectAccount
import com.algorand.android.models.WalletConnectAddress
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import javax.inject.Inject

internal class CreateWalletConnectAccountUseCase @Inject constructor(
    private val getAccountCustomName: GetAccountCustomName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : CreateWalletConnectAccount {

    override suspend fun invoke(address: WalletConnectAddress): WalletConnectAccount? {
        val decodedAddress = address.decodedAddress
        if (decodedAddress.isNullOrBlank()) return null
        return WalletConnectAccount(
            address = address.decodedAddress,
            name = getAccountCustomName(decodedAddress).orEmpty(),
            accountIconDrawablePreview = getAccountIconDrawablePreview(decodedAddress)
        )
    }
}
