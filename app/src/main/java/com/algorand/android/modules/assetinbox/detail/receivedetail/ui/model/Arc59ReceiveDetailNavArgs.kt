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

package com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model

import android.os.Parcelable
import com.algorand.android.assetsearch.domain.model.VerificationTier
import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.utils.AssetName
import java.math.BigInteger
import kotlinx.parcelize.Parcelize

@Parcelize
data class Arc59ReceiveDetailNavArgs(
    val receiverAddress: String,
    val assetDetail: BaseAssetDetail,
    val senderDetails: List<SenderDetail>,
    val gainOnClaim: BigInteger,
    val gainOnReject: BigInteger,
    val inboxAccountAddress: String,
    val creatorAccountAddress: String,
    val shouldUseFundsBeforeClaiming: Boolean,
    val shouldUseFundsBeforeRejecting: Boolean,
    val insufficientAlgoForClaiming: Boolean,
    val insufficientAlgoForRejecting: Boolean
) : Parcelable {

    sealed interface BaseAssetDetail : Parcelable {

        val name: AssetName
        val shortName: String
        val id: Long
        val decimals: Int
        val verificationTier: VerificationTier
        val verificationTierConfiguration: VerificationTierConfiguration
        val imageUrl: String

        @Parcelize
        data class AssetDetail(
            override val name: AssetName,
            override val id: Long,
            override val shortName: String,
            override val decimals: Int,
            override val verificationTier: VerificationTier,
            override val verificationTierConfiguration: VerificationTierConfiguration,
            override val imageUrl: String,
            val amount: BigInteger,
            val usdValue: String
        ) : BaseAssetDetail

        @Parcelize
        data class CollectibleDetail(
            override val id: Long,
            override val name: AssetName,
            override val shortName: String,
            override val decimals: Int,
            override val verificationTier: VerificationTier,
            override val verificationTierConfiguration: VerificationTierConfiguration,
            override val imageUrl: String
        ) : BaseAssetDetail
    }

    @Parcelize
    data class SenderDetail(
        val address: String,
        val name: String,
        val amount: BigInteger
    ) : Parcelable
}
