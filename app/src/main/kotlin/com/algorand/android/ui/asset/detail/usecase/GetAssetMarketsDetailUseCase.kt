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

package com.algorand.android.ui.asset.detail.usecase

import com.algorand.android.R
import com.algorand.android.modules.assets.profile.about.domain.usecase.GetSelectedAssetExchangeValueUseCase
import com.algorand.android.modules.assets.profile.asaprofile.ui.usecase.AsaProfilePreviewUseCase.Companion.MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT
import com.algorand.android.ui.asset.detail.mapper.AssetMarketsDetailBadgeDescriptionMapper
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.SimpleFormattedAmount
import com.algorand.android.utils.DEFAULT_ASSET_DECIMAL
import com.algorand.android.utils.browser.addProtocolIfNeed
import com.algorand.android.utils.browser.removeProtocolIfNeed
import com.algorand.android.utils.formatAmount
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.domain.model.VerificationTier.SUSPICIOUS
import com.algorand.wallet.asset.domain.model.VerificationTier.TRUSTED
import com.algorand.wallet.asset.domain.model.VerificationTier.UNKNOWN
import com.algorand.wallet.asset.domain.model.VerificationTier.UNVERIFIED
import com.algorand.wallet.asset.domain.model.VerificationTier.VERIFIED
import java.math.BigDecimal
import javax.inject.Inject

internal class GetAssetMarketsDetailUseCase @Inject constructor(
    private val getSelectedAssetExchangeValueUseCase: GetSelectedAssetExchangeValueUseCase,
    private val assetMarketsDetailBadgeDescriptionMapper: AssetMarketsDetailBadgeDescriptionMapper
) : GetAssetMarketsDetail {

    override fun invoke(asset: Asset): List<AssetMarketsDetail> {
        return if (asset.isAlgo) {
            createAlgoMarketsDetail(asset)
        } else {
            createAssetAboutPreview(asset)
        }
    }

    private fun createAlgoMarketsDetail(assetDetail: Asset): List<AssetMarketsDetail> {
        return mutableListOf<AssetMarketsDetail>().apply {
            with(assetDetail) {
                add(createStatisticsItem(this))
                add(createAlgoAboutAssetItem(fullName, assetInfo?.url))
                add(createAlgoDescriptionItem())
                createSocialMediaItem(assetInfo?.social)?.run { add(this) }
                addVerificationTierDescriptionIfNeed(this@apply, verificationTier)
            }
        }
    }

    private fun createAssetAboutPreview(assetDetail: Asset): List<AssetMarketsDetail> {
        return mutableListOf<AssetMarketsDetail>().apply {
            with(assetDetail) {
                add(createStatisticsItem(this))
                add(createAboutAssetItem(assetDetail.id, assetInfo))
                createAssetDescriptionItem(assetInfo?.description)?.run { add(this) }
                createSocialMediaItem(assetInfo?.social)?.run { add(this) }
                addReportItemIfNeed(this@apply, verificationTier, assetDetail.id, shortName)
                addVerificationTierDescriptionIfNeed(this@apply, verificationTier)
            }
        }
    }

    private fun addVerificationTierDescriptionIfNeed(
        detailsList: MutableList<AssetMarketsDetail>,
        verificationTier: VerificationTier
    ) {
        val position = when (verificationTier) {
            TRUSTED, VERIFIED -> detailsList.indexOfFirst { it is AssetMarketsDetail.About } + 1
            SUSPICIOUS -> detailsList.indexOfFirst { it is AssetMarketsDetail.Statistics }
            UNVERIFIED, UNKNOWN -> null
        }
        val description = assetMarketsDetailBadgeDescriptionMapper(verificationTier)
        if (description != null && position != null) {
            detailsList.add(position, description)
        }
    }

    private fun addReportItemIfNeed(
        mutableList: MutableList<AssetMarketsDetail>,
        verificationTier: VerificationTier,
        assetId: Long,
        shortName: String?
    ) {
        if (verificationTier != TRUSTED) {
            mutableList.add(createReportItem(assetId, shortName))
        }
    }

    private fun createStatisticsItem(assetDetail: Asset): AssetMarketsDetail {
        with(assetDetail) {
            val minAmountToDisplay = BigDecimal.valueOf(MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT)
            val formattedAssetPrice = getSelectedAssetExchangeValueUseCase
                .getSelectedAssetExchangeValue(assetDetail = this)
                ?.getFormattedValue(minValueToDisplayExactAmount = minAmountToDisplay)
            val formattedTotalSupply = assetDetail.assetInfo?.supply?.total?.formatAmount(
                decimals = assetDetail.assetInfo?.decimals ?: DEFAULT_ASSET_DECIMAL,
                isCompact = true,
                isDecimalFixed = false
            )
            return AssetMarketsDetail.Statistics(
                AmountRenderer(SimpleFormattedAmount(formattedAssetPrice.orEmpty()), AmountRenderer.RenderType.Plain),
                totalSupply = formattedTotalSupply
            )
        }
    }

    private fun createAlgoAboutAssetItem(fullName: String?, asaUrl: String?): AssetMarketsDetail.About {
        return AssetMarketsDetail.About(
            assetName = fullName,
            assetId = null,
            assetCreatorAddress = null,
            asaUrl = asaUrl.addProtocolIfNeed(),
            displayAsaUrl = asaUrl.removeProtocolIfNeed(),
            peraExplorerUrl = null,
            projectWebsiteUrl = null
        )
    }

    private fun createAboutAssetItem(assetId: Long?, assetInfo: Asset.AssetInfo?): AssetMarketsDetail.About {
        return AssetMarketsDetail.About(
            assetName = assetInfo?.name?.fullName,
            assetId = assetId,
            assetCreatorAddress = assetInfo?.creator?.publicKey,
            asaUrl = assetInfo?.url?.addProtocolIfNeed(),
            displayAsaUrl = assetInfo?.url.removeProtocolIfNeed(),
            peraExplorerUrl = assetInfo?.explorerUrl,
            projectWebsiteUrl = assetInfo?.project?.url
        )
    }

    private fun createAssetDescriptionItem(assetDescription: String?): AssetMarketsDetail.AssetDescription.Text? {
        if (assetDescription.isNullOrBlank()) return null
        return AssetMarketsDetail.AssetDescription.Text(assetDescription)
    }

    private fun createAlgoDescriptionItem(): AssetMarketsDetail.AssetDescription.TextResource {
        return AssetMarketsDetail.AssetDescription.TextResource(R.string.the_algo_is_the_official_cryptocurrency)
    }

    private fun createSocialMediaItem(social: Asset.Social?): AssetMarketsDetail.SocialMedia? {
        return social?.run {
            if (discordUrl.isNullOrBlank() && telegramUrl.isNullOrBlank() && twitterUsername.isNullOrBlank()) {
                return null
            }
            return AssetMarketsDetail.SocialMedia(discordUrl, telegramUrl, twitterUsername)
        }
    }

    private fun createReportItem(assetId: Long, shortName: String?): AssetMarketsDetail.Report {
        return AssetMarketsDetail.Report(shortName, assetId)
    }
}
