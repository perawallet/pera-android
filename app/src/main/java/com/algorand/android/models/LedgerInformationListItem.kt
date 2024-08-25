/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.models

import androidx.annotation.StringRes
import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration
import com.algorand.android.drawableui.asset.BaseAssetDrawableProvider

sealed class LedgerInformationListItem {

    abstract infix fun areItemsTheSame(other: LedgerInformationListItem): Boolean
    abstract infix fun areContentsTheSame(other: LedgerInformationListItem): Boolean

    data class AccountItem(
        val accountDisplayName: AccountDisplayName,
        val address: String,
        val accountIconDrawablePreview: AccountIconDrawablePreview,
        val portfolioValue: String
    ) : LedgerInformationListItem() {
        override fun areItemsTheSame(other: LedgerInformationListItem): Boolean {
            return other is AccountItem && address == other.address
        }

        override fun areContentsTheSame(other: LedgerInformationListItem): Boolean {
            return other is AccountItem && this == other
        }
    }

    data class AssetInformationItem(
        val id: Long,
        val name: AssetName,
        val shortName: AssetName,
        val isAmountInDisplayedCurrencyVisible: Boolean,
        val verificationTierConfiguration: VerificationTierConfiguration,
        val baseAssetDrawableProvider: BaseAssetDrawableProvider,
        val formattedDisplayedCurrencyValue: String,
        val formattedAmount: String
    ) : LedgerInformationListItem() {
        override fun areItemsTheSame(other: LedgerInformationListItem): Boolean {
            return other is AssetInformationItem && id == other.id
        }

        override fun areContentsTheSame(other: LedgerInformationListItem): Boolean {
            return other is AssetInformationItem && this == other
        }
    }

    data class TitleItem(@StringRes val titleRes: Int) : LedgerInformationListItem() {
        override fun areItemsTheSame(other: LedgerInformationListItem): Boolean {
            return other is TitleItem && titleRes == other.titleRes
        }

        override fun areContentsTheSame(other: LedgerInformationListItem): Boolean {
            return other is TitleItem && titleRes == other.titleRes
        }
    }

    data class CanSignedByItem(
        val accountIconDrawablePreview: AccountIconDrawablePreview,
        val accountPublicKey: String
    ) : LedgerInformationListItem() {

        override fun areItemsTheSame(other: LedgerInformationListItem): Boolean {
            return other is CanSignedByItem && accountPublicKey == other.accountPublicKey
        }

        override fun areContentsTheSame(other: LedgerInformationListItem): Boolean {
            return other is CanSignedByItem && this == other
        }
    }
}
