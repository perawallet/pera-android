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

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model

import com.algorand.android.assetsearch.domain.model.VerificationTier
import com.algorand.android.utils.AssetName
import java.math.BigInteger

data class AssetInboxOneAccountPaginated(
    val next: String?,
    val previous: String?,
    val receiverAddress: String,
    val inboxAddress: String,
    val count: Int,
    val results: List<AssetInboxOneAccountResult>
)

data class AssetInboxOneAccountResult(
    val totalAmount: Int,
    val asset: Asset,
    val algoGainOnClaim: BigInteger,
    val algoGainOnReject: BigInteger,
    val senders: Senders,
    val insufficientAlgoForClaiming: Boolean,
    val shouldUseFundsBeforeClaiming: Boolean,
    val insufficientAlgoForRejecting: Boolean,
    val shouldUseFundsBeforeRejecting: Boolean
)

data class Asset(
    val assetId: Long,
    val name: AssetName,
    val logo: String?,
    val unitName: String,
    val decimals: Int,
    val total: String,
    val usdValue: String,
    val isVerified: Boolean,
    val isDeleted: Boolean,
    val verificationTier: VerificationTier,
    val explorerUrl: String,
    val collectible: Collectible?,
    val creator: Creator,
    val type: String
)

data class Senders(
    val count: Int,
    val results: List<SenderResult>
)

data class SenderResult(
    val sender: Sender,
    val amount: BigInteger
)

data class Sender(
    val address: String,
    val name: String?
)

data class Collectible(
    val title: String,
    val standard: String,
    val primaryImage: String,
    val mediaType: String,
    val explorerUrl: String,
    val collection: SimpleCollection?
)

data class SimpleCollection(
    val collectionId: Long,
    val collectionName: String,
    val collectionDescription: String
)

data class Creator(
    val id: BigInteger,
    val address: String,
    val isVerifiedAssetCreator: Boolean
)
