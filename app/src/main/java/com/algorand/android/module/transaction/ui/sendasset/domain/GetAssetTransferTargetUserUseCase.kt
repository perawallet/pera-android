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

package com.algorand.android.module.transaction.ui.sendasset.domain

import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.contacts.domain.usecase.GetContactByAddress
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser.Account
import javax.inject.Inject

internal class GetAssetTransferTargetUserUseCase @Inject constructor(
    private val getContactByAddress: GetContactByAddress,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDetail: GetAccountDetail
) : GetAssetTransferTargetUser {

    override suspend fun invoke(
        receiverAddress: String,
        nfDomainAddress: String?,
        nfDomainServiceLogoUrl: String?
    ): AssetTransferTargetUser {
        val contact = getContactByAddress(receiverAddress)
        if (contact != null) {
            return AssetTransferTargetUser.ContactUser(contact)
        }

        if (!nfDomainAddress.isNullOrBlank()) {
            return AssetTransferTargetUser.NfDomainUser(nfDomainAddress, nfDomainServiceLogoUrl)
        }

        val accountDetail = getAccountDetail(receiverAddress)
        if (accountDetail.accountRegistrationType != null) {
            return Account(getAccountDisplayName(accountDetail), getAccountIconDrawablePreview(accountDetail))
        }

        return AssetTransferTargetUser.Address(receiverAddress)
    }
}
