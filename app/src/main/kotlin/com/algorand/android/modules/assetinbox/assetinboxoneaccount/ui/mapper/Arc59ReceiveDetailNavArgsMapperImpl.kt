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

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper

import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AsaPreview
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailNavArgs
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailNavArgs.SenderDetail
import javax.inject.Inject

class Arc59ReceiveDetailNavArgsMapperImpl @Inject constructor() : Arc59ReceiveDetailNavArgsMapper {

    override fun invoke(asaPreview: AsaPreview): Arc59ReceiveDetailNavArgs {
        return Arc59ReceiveDetailNavArgs(
            receiverAddress = asaPreview.receiverAddress,
            assetDetail = mapAssetDetail(asaPreview),
            gainOnClaim = asaPreview.gainOnClaim,
            gainOnReject = asaPreview.gainOnReject,
            inboxAccountAddress = asaPreview.inboxAccountAddress,
            creatorAccountAddress = asaPreview.creatorAddress,
            senderDetails = asaPreview.senderAccounts.map {
                SenderDetail(
                    address = it.address,
                    name = it.name.orEmpty(),
                    amount = it.amount
                )
            },
            shouldUseFundsBeforeClaiming = asaPreview.shouldUseFundsBeforeClaiming,
            shouldUseFundsBeforeRejecting = asaPreview.shouldUseFundsBeforeRejecting,
            insufficientAlgoForClaiming = asaPreview.insufficientAlgoForClaiming,
            insufficientAlgoForRejecting = asaPreview.insufficientAlgoForRejecting,
        )
    }

    private fun mapAssetDetail(asaPreview: AsaPreview): Arc59ReceiveDetailNavArgs.BaseAssetDetail {
        return when (asaPreview) {
            is AsaPreview.AssetPreview -> Arc59ReceiveDetailNavArgs.BaseAssetDetail.AssetDetail(
                id = asaPreview.id,
                amount = asaPreview.amount,
                decimals = asaPreview.decimals,
                name = asaPreview.assetName,
                shortName = asaPreview.shortName,
                usdValue = asaPreview.usdValue,
                imageUrl = asaPreview.logo.orEmpty(),
                verificationTier = asaPreview.verificationTier,
                verificationTierConfiguration = asaPreview.verificationTierConfiguration
            )

            is AsaPreview.CollectiblePreview -> Arc59ReceiveDetailNavArgs.BaseAssetDetail.CollectibleDetail(
                id = asaPreview.id,
                name = asaPreview.assetName,
                shortName = asaPreview.shortName,
                imageUrl = asaPreview.logo.orEmpty(),
                verificationTier = asaPreview.verificationTier,
                verificationTierConfiguration = asaPreview.verificationTierConfiguration,
                decimals = asaPreview.decimals
            )
        }
    }
}
