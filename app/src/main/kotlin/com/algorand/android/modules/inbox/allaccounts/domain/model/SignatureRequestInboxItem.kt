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

package com.algorand.android.modules.inbox.allaccounts.domain.model

import android.os.Parcelable
import com.algorand.android.models.RecyclerListItem
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignatureRequestInboxItem(
    val signRequestId: String,
    val jointAccountAddress: String,
    val jointAccountAddressShortened: String,
    val accountIconDrawablePreview: AccountIconDrawablePreview,
    val description: String,
    val timeAgo: String,
    val signedCount: Int,
    val totalCount: Int,
    val timeLeft: String?,
    val isRead: Boolean = true,
    val statusLineText: String,
    val statusLineIsError: Boolean = false,
    val failReasonDisplay: String? = null,
    val canUserSign: Boolean = true
) : Parcelable, RecyclerListItem {
    override fun areItemsTheSame(other: RecyclerListItem): Boolean {
        return other is SignatureRequestInboxItem && signRequestId == other.signRequestId
    }

    override fun areContentsTheSame(other: RecyclerListItem): Boolean {
        return other is SignatureRequestInboxItem && this == other
    }
}
