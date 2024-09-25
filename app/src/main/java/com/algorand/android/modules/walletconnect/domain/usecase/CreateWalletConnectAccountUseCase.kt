/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.modules.walletconnect.domain.usecase

import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.models.WalletConnectAccount
import javax.inject.Inject

internal class CreateWalletConnectAccountUseCase @Inject constructor(
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDetail: GetAccountDetail
) : CreateWalletConnectAccount {

    override suspend fun invoke(address: String?): WalletConnectAccount? {
        if (address == null) return null
        val accountDetail = getAccountDetail(address)
        return WalletConnectAccount(
            address = address,
            accountIconDrawablePreview = getAccountIconDrawablePreview(address),
            name = accountDetail.customInfo.customName.orEmpty()
        )
    }
}
