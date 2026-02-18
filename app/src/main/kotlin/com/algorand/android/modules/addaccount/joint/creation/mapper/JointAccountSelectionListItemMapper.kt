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

package com.algorand.android.modules.addaccount.joint.creation.mapper

import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.models.BaseAccountSelectionListItem
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.creation.model.JointAccountSelectionListItem
import javax.inject.Inject

class JointAccountSelectionListItemMapper @Inject constructor() {

    fun mapToAccountItem(
        accountItem: BaseAccountSelectionListItem.BaseAccountItem.AccountItem
    ): JointAccountSelectionListItem.AccountItem {
        val configuration = accountItem.accountListItem.itemConfiguration
        return JointAccountSelectionListItem.AccountItem(
            address = configuration.accountAddress,
            displayName = configuration.accountDisplayName?.primaryDisplayName.orEmpty(),
            secondaryDisplayName = configuration.accountDisplayName?.secondaryDisplayName,
            iconDrawablePreview = configuration.accountIconDrawablePreview ?: getDefaultIconDrawablePreview(),
            formattedAmount = configuration.primaryValueText.orEmpty(),
            formattedCurrencyValue = configuration.secondaryValueText.orEmpty()
        )
    }

    fun mapToContactItem(
        contactItem: BaseAccountSelectionListItem.BaseAccountItem.ContactItem
    ): JointAccountSelectionListItem.ContactItem {
        return JointAccountSelectionListItem.ContactItem(
            address = contactItem.address,
            displayName = contactItem.displayName,
            imageUri = contactItem.imageUri
        )
    }

    fun mapToNfdItem(
        nfdItem: BaseAccountSelectionListItem.BaseAccountItem.NftDomainAccountItem
    ): JointAccountSelectionListItem.NfdItem {
        return JointAccountSelectionListItem.NfdItem(
            address = nfdItem.address,
            domainName = nfdItem.displayName,
            serviceLogoUrl = nfdItem.serviceLogoUrl
        )
    }

    fun mapToExternalAddressItem(
        address: String,
        shortenedAddress: String
    ): JointAccountSelectionListItem.ExternalAddressItem {
        return JointAccountSelectionListItem.ExternalAddressItem(
            address = address,
            shortenedAddress = shortenedAddress,
            iconDrawablePreview = getExternalAddressIconDrawablePreview()
        )
    }

    private fun getDefaultIconDrawablePreview(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = R.color.layer_gray_lighter,
            iconTintResId = R.color.text_gray,
            iconResId = R.drawable.ic_wallet
        )
    }

    private fun getExternalAddressIconDrawablePreview(): AccountIconDrawablePreview {
        return AccountIconDrawablePreview(
            backgroundColorResId = AccountIconResource.JOINT.backgroundColorResId,
            iconTintResId = AccountIconResource.JOINT.iconTintResId,
            iconResId = AccountIconResource.JOINT.iconResId
        )
    }
}
