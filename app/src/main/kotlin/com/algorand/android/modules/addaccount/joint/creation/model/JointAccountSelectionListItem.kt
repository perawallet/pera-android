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

package com.algorand.android.modules.addaccount.joint.creation.model

import android.net.Uri
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview

sealed class JointAccountSelectionListItem {

    abstract val address: String

    data class AccountItem(
        override val address: String,
        val displayName: String,
        val secondaryDisplayName: String?,
        val iconDrawablePreview: AccountIconDrawablePreview,
        val formattedAmount: String,
        val formattedCurrencyValue: String
    ) : JointAccountSelectionListItem()

    data class ContactItem(
        override val address: String,
        val displayName: String,
        val imageUri: Uri?
    ) : JointAccountSelectionListItem()

    data class NfdItem(
        override val address: String,
        val domainName: String,
        val serviceLogoUrl: String?
    ) : JointAccountSelectionListItem()

    data class ExternalAddressItem(
        override val address: String,
        val shortenedAddress: String,
        val iconDrawablePreview: AccountIconDrawablePreview
    ) : JointAccountSelectionListItem()
}
