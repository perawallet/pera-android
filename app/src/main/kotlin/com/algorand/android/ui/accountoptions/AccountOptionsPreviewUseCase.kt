/*
 * Copyright 2025 Pera Wallet, LDA
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

import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.ui.accountoptions.model.AccountOptionsPreview
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import javax.inject.Inject

class AccountOptionsPreviewUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountDetail: GetAccountDetail
) {

    suspend fun getPreview(address: String): AccountOptionsPreview? {
        val accountDetail = getAccountDetail(address)
        val canSignTransaction = accountDetail.accountType?.canSignTransaction() == true
        return getAccountInformation(address)?.run {
            AccountOptionsPreview(
                accountAddress = address,
                authAddress = rekeyAdminAddress,
                accountDisplayName = getAccountDisplayName(address),
                authAccountDisplayName = if (isRekeyed()) getAccountDisplayName(rekeyAdminAddress.orEmpty()) else null,
                isAuthAddressButtonVisible = isRekeyed(),
                isPassphraseButtonVisible = accountDetail.accountRegistrationType == AccountRegistrationType.Algo25 ||
                        accountDetail.accountRegistrationType == AccountRegistrationType.HdKey,
                isUndoRekeyButtonVisible = isRekeyed() && canSignTransaction,
                canSignTransaction = canSignTransaction
            )
        }
    }
}
