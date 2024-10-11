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

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.mapper

import com.algorand.android.assetsearch.data.mapper.VerificationTierDTODecider
import com.algorand.android.assetsearch.domain.mapper.VerificationTierDecider
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.mapper.AssetInboxOneAccountMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse.AssetInboxOneAccountResultResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse.AssetResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse.CollectibleResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse.CollectionResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse.CreatorResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse.SenderResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse.SenderResultResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse.SendersResponse
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.Asset
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.AssetInboxOneAccountPaginated
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.AssetInboxOneAccountResult
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.Collectible
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.Creator
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.Sender
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.SenderResult
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.Senders
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.SimpleCollection
import com.algorand.android.utils.AssetName
import java.math.BigInteger
import javax.inject.Inject

class AssetInboxOneAccountMapperImpl @Inject constructor(
    private val verificationTierDTODecider: VerificationTierDTODecider,
    private val verificationTierDecider: VerificationTierDecider
) : AssetInboxOneAccountMapper {

    override fun invoke(response: AssetInboxOneAccountPaginatedResponse?): AssetInboxOneAccountPaginated? {
        if (response == null || !response.isValid()) return null

        return AssetInboxOneAccountPaginated(
            next = response.next,
            previous = response.previous,
            receiverAddress = response.address.orEmpty(),
            inboxAddress = response.inboxAddress.orEmpty(),
            count = response.count ?: 0,
            results = response.results?.map { mapToAssetInboxOneAccountResult(it) } ?: emptyList()
        )
    }

    // TODO Update is valid parameters
    private fun AssetInboxOneAccountPaginatedResponse.isValid(): Boolean {
        return true
    }

    private fun mapToAssetInboxOneAccountResult(response: AssetInboxOneAccountResultResponse?):
            AssetInboxOneAccountResult {
        return AssetInboxOneAccountResult(
            totalAmount = response?.totalAmount ?: 0,
            asset = mapToAsset(response?.asset),
            algoGainOnClaim = response?.algoGainOnClaim ?: BigInteger.ZERO,
            algoGainOnReject = response?.algoGainOnReject ?: BigInteger.ZERO,
            senders = mapToSenders(response?.senders),
            insufficientAlgoForClaiming = response?.insufficientAlgoForClaiming ?: false,
            shouldUseFundsBeforeClaiming = response?.shouldUseFundsBeforeClaiming ?: false,
            insufficientAlgoForRejecting = response?.insufficientAlgoForRejecting ?: false,
            shouldUseFundsBeforeRejecting = response?.shouldUseFundsBeforeRejecting ?: false
        )
    }

    private fun mapToAsset(response: AssetResponse?): Asset {
        val verificationTier = verificationTierDecider.decideVerificationTier(
            verificationTierDTODecider.decideVerificationTierDTO(
                response?.verificationTier
            )
        )
        return Asset(
            assetId = response?.assetId ?: 0L,
            name = AssetName.create(response?.name),
            logo = response?.logo ?: response?.collectible?.primaryImage,
            unitName = response?.unitName.orEmpty(),
            decimals = response?.fractionDecimals ?: 0,
            total = response?.total.orEmpty(),
            usdValue = response?.usdValue.orEmpty(),
            isVerified = response?.isVerified ?: false,
            isDeleted = response?.isDeleted ?: false,
            verificationTier = verificationTier,
            explorerUrl = response?.explorerUrl.orEmpty(),
            collectible = response?.collectible.let { mapToCollectible(it) },
            creator = mapToCreator(response?.creator),
            type = response?.type.orEmpty()
        )
    }

    private fun mapToSenders(response: SendersResponse?): Senders {
        return Senders(
            count = response?.count ?: 0,
            results = response?.results?.map { mapToSenderResult(it) } ?: emptyList()
        )
    }

    private fun mapToSenderResult(response: SenderResultResponse): SenderResult {
        return SenderResult(
            sender = mapToSender(response.sender),
            amount = response.amount ?: BigInteger.ZERO
        )
    }

    private fun mapToSender(response: SenderResponse?): Sender {
        return Sender(
            address = response?.address.orEmpty(),
            name = response?.name
        )
    }

    private fun mapToCollectible(response: CollectibleResponse?): Collectible? {
        if (response == null) return null

        return Collectible(
            title = response.title.orEmpty(),
            standard = response.standard.orEmpty(),
            primaryImage = response.primaryImage.orEmpty(),
            mediaType = response.mediaType.orEmpty(),
            explorerUrl = response.explorerUrl.orEmpty(),
            collection = response.collection?.let { mapToSimpleCollection(it) }
        )
    }

    private fun mapToSimpleCollection(response: CollectionResponse): SimpleCollection {
        return SimpleCollection(
            collectionId = response.collectionId ?: 0L,
            collectionName = response.collectionName.orEmpty(),
            collectionDescription = response.collectionDescription.orEmpty()
        )
    }

    private fun mapToCreator(response: CreatorResponse?): Creator {
        return Creator(
            id = response?.id ?: BigInteger.ZERO,
            address = response?.address.orEmpty(),
            isVerifiedAssetCreator = response?.isVerifiedAssetCreator ?: false
        )
    }
}
