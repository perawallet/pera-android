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

import com.algorand.android.R
import com.algorand.android.core.AccountManager
import com.algorand.android.mapper.AccountAddressMapper
import com.algorand.android.models.AccountIconResource
import com.algorand.android.models.BaseAccountAddress
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import javax.inject.Inject

class AccountAddressUseCase @Inject constructor(
    private val accountManager: AccountManager,
    private val accountAddressMapper: AccountAddressMapper,
    private val getAccountCustomName: GetAccountCustomName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) {

    @Deprecated("Use getAccountAddress instead.")
    fun createAccountAddress(publicKey: String): BaseAccountAddress.AccountAddress {
        val account = accountManager.getAccount(publicKey)
        return if (account == null) {
            accountAddressMapper.createAccountAddress(
                publicKey = publicKey,
                accountIconDrawablePreview = getDefaultDrawablePreview()
            )
        } else {
            accountAddressMapper.createAccountAddress(
                account = account,
                accountIconDrawablePreview = getDefaultDrawablePreview()
            )
        }
    }

    // TODO: This method should be removed after all usages are replaced with getAccountAddress.
    private fun getDefaultDrawablePreview(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = R.color.layer_gray_lighter,
            iconResId = AccountIconResource.STANDARD.iconResId,
            iconTintResId = R.color.text_gray
        )
    }

    suspend fun getAccountAddress(address: String): BaseAccountAddress.AccountAddress {
        return BaseAccountAddress.AccountAddress(
            publicKey = address,
            accountIconDrawablePreview = getAccountIconDrawablePreview(address),
            displayName = getAccountCustomName(address)
        )
    }
}
