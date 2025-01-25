/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model

import com.algorand.android.assetsearch.domain.model.VerificationTier
import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.models.RecyclerListItem
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider
import java.math.BigInteger

data class AssetInboxOneAccountPreview(
    val asaPreviewList: List<AsaPreview>,
    val isLoading: Boolean,
    val isEmptyStateVisible: Boolean,
    val showError: Event<ErrorResource>?,
    val onNavBack: Event<Unit>?
)

enum class ItemType {
    ASSET,
    COLLECTIBLE,
}

sealed class AsaPreview : RecyclerListItem {
    abstract val id: Long
    abstract val itemType: ItemType
    abstract val receiverAddress: String
    abstract val assetName: AssetName
    abstract val shortName: String
    abstract val usdValue: String
    abstract val total: String
    abstract val amount: BigInteger
    abstract val logo: String?
    abstract val senderAccounts: List<SenderPreview>
    abstract val decimals: Int
    abstract val verificationTier: VerificationTier
    abstract val verificationTierConfiguration: VerificationTierConfiguration
    abstract val assetDrawableProvider: BaseAssetDrawableProvider?
    abstract val gainOnClaim: BigInteger
    abstract val gainOnReject: BigInteger
    abstract val inboxAccountAddress: String
    abstract val creatorAddress: String
    abstract val firstSender: String
    abstract val otherSendersCount: Int
    abstract val shouldUseFundsBeforeClaiming: Boolean
    abstract val shouldUseFundsBeforeRejecting: Boolean
    abstract val insufficientAlgoForClaiming: Boolean
    abstract val insufficientAlgoForRejecting: Boolean

    data class AssetPreview(
        override val id: Long,
        override val receiverAddress: String,
        override val assetName: AssetName,
        override val shortName: String,
        override val usdValue: String,
        override val total: String,
        override val amount: BigInteger,
        override val logo: String?,
        override val senderAccounts: List<SenderPreview>,
        override val decimals: Int,
        override val verificationTier: VerificationTier,
        override val verificationTierConfiguration: VerificationTierConfiguration,
        override val assetDrawableProvider: BaseAssetDrawableProvider?,
        override val gainOnClaim: BigInteger,
        override val gainOnReject: BigInteger,
        override val inboxAccountAddress: String,
        override val creatorAddress: String,
        override val firstSender: String,
        override val otherSendersCount: Int,
        override val shouldUseFundsBeforeClaiming: Boolean,
        override val shouldUseFundsBeforeRejecting: Boolean,
        override val insufficientAlgoForClaiming: Boolean,
        override val insufficientAlgoForRejecting: Boolean,
        val formattedAssetAmount: String
    ) : AsaPreview() {
        override val itemType = ItemType.ASSET

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is AssetPreview && id == other.id
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is AssetPreview && this == other
        }
    }

    data class CollectiblePreview(
        override val id: Long,
        override val receiverAddress: String,
        override val assetName: AssetName,
        override val shortName: String,
        override val usdValue: String,
        override val amount: BigInteger,
        override val total: String,
        override val logo: String?,
        override val senderAccounts: List<SenderPreview>,
        override val decimals: Int,
        override val verificationTier: VerificationTier,
        override val verificationTierConfiguration: VerificationTierConfiguration,
        override val assetDrawableProvider: BaseAssetDrawableProvider?,
        override val gainOnClaim: BigInteger,
        override val gainOnReject: BigInteger,
        override val inboxAccountAddress: String,
        override val creatorAddress: String,
        override val firstSender: String,
        override val otherSendersCount: Int,
        override val shouldUseFundsBeforeClaiming: Boolean,
        override val shouldUseFundsBeforeRejecting: Boolean,
        override val insufficientAlgoForClaiming: Boolean,
        override val insufficientAlgoForRejecting: Boolean
    ) : AsaPreview() {
        override val itemType = ItemType.COLLECTIBLE

        override fun areItemsTheSame(other: RecyclerListItem): Boolean {
            return other is CollectiblePreview && id == other.id
        }

        override fun areContentsTheSame(other: RecyclerListItem): Boolean {
            return other is CollectiblePreview && this == other
        }
    }
}

data class SenderPreview(
    val nameOrAddress: String,
    val address: String,
    val name: String?,
    val amount: BigInteger,
)
