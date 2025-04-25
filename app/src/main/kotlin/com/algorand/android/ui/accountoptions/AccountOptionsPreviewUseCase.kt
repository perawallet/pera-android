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

package com.algorand.android.ui.accountoptions

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.ui.accountoptions.model.AccountOptionsPreview
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType.Algo25
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType.HdKey
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import javax.inject.Inject

class AccountOptionsPreviewUseCase @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountLite: GetAccountLite
) {

    suspend fun getPreview(address: String): AccountOptionsPreview? {
        return getAccountLite(address)?.run {
            val canSignTransaction = cachedInfo?.type?.canSignTransaction() == true
            val isRekeyed = cachedInfo?.isRekeyed == true
            AccountOptionsPreview(
                accountAddress = address,
                authAddress = cachedInfo?.rekeyAuthAddress,
                accountDisplayName = getAccountDisplayName(address),
                authAccountDisplayName = getAuthAccountDisplayName(this),
                isAuthAddressButtonVisible = isRekeyed,
                isPassphraseButtonVisible = registrationType == Algo25 || registrationType == HdKey,
                isUndoRekeyButtonVisible = isRekeyed && canSignTransaction,
                canSignTransaction = canSignTransaction
            )
        }
    }

    private suspend fun getAuthAccountDisplayName(accountLite: AccountLite): AccountDisplayName? {
        return if (accountLite.cachedInfo?.isRekeyed == true) {
            getAccountDisplayName(accountLite.cachedInfo.rekeyAuthAddress.orEmpty())
        } else {
            null
        }
    }
}
