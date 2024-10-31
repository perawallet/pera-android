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

import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.AssetInboxOneAccountPaginated
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.AssetInboxOneAccountResult
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AsaPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.SenderPreview
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.formatAsAssetAmount
import com.algorand.android.utils.formatAsCurrency
import com.algorand.android.utils.multiplyOrZero
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject

class AssetInboxOneAccountPreviewMapperImpl @Inject constructor(
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider
) : AssetInboxOneAccountPreviewMapper {

    override fun invoke(
        assetInboxOneAccountPaginated: AssetInboxOneAccountPaginated,
        isLoading: Boolean,
        isEmptyStateVisible: Boolean,
        showError: Event<ErrorResource>?,
        onNavBack: Event<Unit>?,
    ): AssetInboxOneAccountPreview {
        return AssetInboxOneAccountPreview(
            asaPreviewList = getAsaPreviewList(assetInboxOneAccountPaginated),
            isLoading = isLoading,
            isEmptyStateVisible = isEmptyStateVisible,
            showError = showError,
            onNavBack = onNavBack,
        )
    }

    private fun getAsaPreviewList(
        assetInboxOneAccountPaginated: AssetInboxOneAccountPaginated
    ): List<AsaPreview> {
        return assetInboxOneAccountPaginated.results.map { result ->
            val assetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(
                result.asset.assetId,
                result.asset.name,
                result.asset.logo
            )
            if (result.asset.collectible != null) {
                createCollectiblePreview(
                    assetInboxOneAccountPaginated,
                    result,
                    assetDrawableProvider
                )
            } else {
                createAssetPreview(assetInboxOneAccountPaginated, result, assetDrawableProvider)
            }
        }
    }

    private fun createCollectiblePreview(
        assetInboxOneAccountPaginated: AssetInboxOneAccountPaginated,
        result: AssetInboxOneAccountResult,
        assetDrawableProvider: BaseAssetDrawableProvider
    ): AsaPreview {
        return AsaPreview.CollectiblePreview(
            id = result.asset.assetId,
            receiverAddress = assetInboxOneAccountPaginated.receiverAddress,
            assetName = result.asset.name,
            shortName = result.asset.unitName,
            usdValue = result.asset.usdValue,
            amount = result.senders.results.sumOf { it.amount },
            logo = result.asset.logo,
            senderAccounts = result.senders.results.map {
                SenderPreview(
                    it.sender.name ?: it.sender.address.toShortenedAddress(),
                    it.sender.address,
                    it.sender.name,
                    it.amount
                )
            },
            decimals = result.asset.decimals,
            verificationTier = result.asset.verificationTier,
            verificationTierConfiguration = verificationTierConfigurationDecider.decideVerificationTierConfiguration(
                result.asset.verificationTier
            ),
            assetDrawableProvider = assetDrawableProvider,
            gainOnReject = result.algoGainOnReject,
            gainOnClaim = result.algoGainOnClaim,
            inboxAccountAddress = assetInboxOneAccountPaginated.inboxAddress,
            total = result.asset.total,
            creatorAddress = result.asset.creator.address,
            firstSender = result.senders.results.firstOrNull()?.let {
                it.sender.name ?: it.sender.address.toShortenedAddress()
            }.orEmpty(),
            otherSendersCount = result.senders.results.size - 1,
            shouldUseFundsBeforeClaiming = result.shouldUseFundsBeforeClaiming,
            shouldUseFundsBeforeRejecting = result.shouldUseFundsBeforeRejecting,
            insufficientAlgoForClaiming = result.insufficientAlgoForClaiming,
            insufficientAlgoForRejecting = result.insufficientAlgoForRejecting
        )
    }

    private fun createAssetPreview(
        assetInboxOneAccountPaginated: AssetInboxOneAccountPaginated,
        result: AssetInboxOneAccountResult,
        assetDrawableProvider: BaseAssetDrawableProvider
    ): AsaPreview {

        return AsaPreview.AssetPreview(
            id = result.asset.assetId,
            receiverAddress = assetInboxOneAccountPaginated.receiverAddress,
            assetName = result.asset.name,
            shortName = result.asset.unitName,
            usdValue = getFormattedUsdValue(result),
            amount = result.totalAmount,
            logo = result.asset.collectible?.primaryImage,
            senderAccounts = result.senders.results.map {
                SenderPreview(
                    it.sender.name ?: it.sender.address.toShortenedAddress(),
                    it.sender.address,
                    it.sender.name,
                    it.amount
                )
            },
            decimals = result.asset.decimals,
            verificationTier = result.asset.verificationTier,
            verificationTierConfiguration = verificationTierConfigurationDecider.decideVerificationTierConfiguration(
                result.asset.verificationTier
            ),
            assetDrawableProvider = assetDrawableProvider,
            gainOnReject = result.algoGainOnReject,
            gainOnClaim = result.algoGainOnClaim,
            inboxAccountAddress = assetInboxOneAccountPaginated.inboxAddress,
            total = result.asset.total,
            creatorAddress = result.asset.creator.address,
            firstSender = result.senders.results.firstOrNull()?.let {
                it.sender.name ?: it.sender.address.toShortenedAddress()
            }.orEmpty(),
            otherSendersCount = result.senders.results.size - 1,
            formattedAssetAmount = getFormattedAssetAmount(result),
            shouldUseFundsBeforeClaiming = result.shouldUseFundsBeforeClaiming,
            shouldUseFundsBeforeRejecting = result.shouldUseFundsBeforeRejecting,
            insufficientAlgoForClaiming = result.insufficientAlgoForClaiming,
            insufficientAlgoForRejecting = result.insufficientAlgoForRejecting
        )
    }

    override fun getInitialPreview(): AssetInboxOneAccountPreview {
        return AssetInboxOneAccountPreview(
            asaPreviewList = emptyList(),
            isLoading = true,
            isEmptyStateVisible = false,
            showError = null,
            onNavBack = null,
        )
    }

    private fun getFormattedAssetAmount(result: AssetInboxOneAccountResult): String {
        return result.totalAmount
            .formatAmount(result.asset.decimals)
            .formatAsAssetAmount(result.asset.unitName)
    }

    private fun getFormattedUsdValue(result: AssetInboxOneAccountResult): String {
        return result.totalAmount
            .toBigDecimal()
            .movePointLeft(result.asset.decimals)
            .multiplyOrZero(result.asset.usdValue.toBigDecimalOrNull())
            .formatAsCurrency(Currency.USD.symbol, isCompact = true)
    }
}
