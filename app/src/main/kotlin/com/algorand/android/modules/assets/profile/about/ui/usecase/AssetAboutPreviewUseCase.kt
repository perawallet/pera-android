/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.assets.profile.about.ui.usecase

import androidx.annotation.StringRes
import com.algorand.android.R
import com.algorand.android.modules.assets.profile.about.domain.usecase.GetSelectedAssetExchangeValueUseCase
import com.algorand.android.modules.assets.profile.about.ui.mapper.AssetAboutPreviewMapper
import com.algorand.android.modules.assets.profile.about.ui.mapper.BaseAssetAboutListItemMapper
import com.algorand.android.modules.assets.profile.about.ui.model.AssetAboutPreview
import com.algorand.android.modules.assets.profile.about.ui.model.BaseAssetAboutListItem
import com.algorand.android.modules.assets.profile.asaprofile.ui.usecase.AsaProfilePreviewUseCase.Companion.MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT
import com.algorand.android.utils.AssetName
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
import com.algorand.wallet.asset.domain.usecase.CacheSingleAssetDetail
import com.algorand.wallet.asset.domain.usecase.ClearSingleAssetCache
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.asset.domain.usecase.GetSingleAssetDetailFlow
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class AssetAboutPreviewUseCase @Inject constructor(
    private val cacheSingleAssetDetail: CacheSingleAssetDetail,
    private val assetAboutPreviewMapper: AssetAboutPreviewMapper,
    private val baseAssetAboutListItemMapper: BaseAssetAboutListItemMapper,
    private val getSelectedAssetExchangeValueUseCase: GetSelectedAssetExchangeValueUseCase,
    private val clearSingleAssetCache: ClearSingleAssetCache,
    private val getSingleAssetDetailFlow: GetSingleAssetDetailFlow,
    private val getAsset: GetAsset
) {

    suspend fun clearAsaProfileLocalCache() {
        clearSingleAssetCache()
    }

    suspend fun cacheAssetDetailToAsaProfileLocalCache(assetId: Long) {
        cacheSingleAssetDetail(assetId)
    }

    fun getAssetAboutPreview(assetId: Long) = flow {
        emit(assetAboutPreviewMapper.mapToAssetAboutPreviewInitialState())
        if (assetId == ALGO_ID) {
            val algoAssetDetail = getAsset(ALGO_ID) ?: return@flow
            val algoAboutPreview = createAlgoAboutPreview(algoAssetDetail)
            emit(algoAboutPreview)
        } else {
            getSingleAssetDetailFlow().collect { assetDetail ->
                emit(createAssetAboutPreview(assetDetail))
            }
        }
    }

    private fun createAlgoAboutPreview(assetDetail: Asset): AssetAboutPreview {
        val algorandAboutList = mutableListOf<BaseAssetAboutListItem>().apply {
            with(assetDetail) {
                add(createStatisticsItem(this))
                add(BaseAssetAboutListItem.DividerItem)
                add(
                    createAlgoAboutAssetItem(
                        fullName = fullName,
                        asaUrl = assetInfo?.url
                    )
                )
                add(BaseAssetAboutListItem.DividerItem)
                add(createAlgoDescriptionItem(R.string.the_algo_is_the_official_cryptocurrency))
                createSocialMediaItem(assetInfo?.social)?.run {
                    add(BaseAssetAboutListItem.DividerItem)
                    add(this)
                }
                addVerificationTierDescriptionIfNeed(this@apply, verificationTier)
            }
        }
        return assetAboutPreviewMapper.mapToAssetAboutPreview(assetAboutListItems = algorandAboutList)
    }

    private fun createAssetAboutPreview(assetDetail: Asset): AssetAboutPreview {
        val assetAboutList = mutableListOf<BaseAssetAboutListItem>().apply {
            with(assetDetail) {

                add(createStatisticsItem(this))
                add(BaseAssetAboutListItem.DividerItem)

                add(createAboutAssetItem(assetDetail.id, assetInfo))

                createAssetDescriptionItem(assetInfo?.description)?.run {
                    add(BaseAssetAboutListItem.DividerItem)
                    add(this)
                }

                createSocialMediaItem(assetInfo?.social)?.run {
                    add(BaseAssetAboutListItem.DividerItem)
                    add(this)
                }
                addReportItemIfNeed(this@apply, verificationTier, assetDetail.id, shortName)
                addVerificationTierDescriptionIfNeed(this@apply, verificationTier)
            }
        }
        return assetAboutPreviewMapper.mapToAssetAboutPreview(assetAboutListItems = assetAboutList)
    }

    private fun addVerificationTierDescriptionIfNeed(
        assetAboutList: MutableList<BaseAssetAboutListItem>,
        verificationTier: VerificationTier
    ) {
        val position = when (verificationTier) {
            TRUSTED, VERIFIED -> assetAboutList.indexOfFirst { it is BaseAssetAboutListItem.AboutAssetItem } + 1
            SUSPICIOUS -> assetAboutList.indexOfFirst { it is BaseAssetAboutListItem.StatisticsItem }
            UNVERIFIED, UNKNOWN -> null
        }
        val item = when (verificationTier) {
            VERIFIED -> BaseAssetAboutListItem.BadgeDescriptionItem.VerifiedBadgeItem
            TRUSTED -> BaseAssetAboutListItem.BadgeDescriptionItem.TrustedBadgeItem
            SUSPICIOUS -> BaseAssetAboutListItem.BadgeDescriptionItem.SuspiciousBadgeItem
            UNVERIFIED, UNKNOWN -> null
        }
        if (item != null && position != null) {
            assetAboutList.add(position, item)
        }
    }

    private fun addReportItemIfNeed(
        mutableList: MutableList<BaseAssetAboutListItem>,
        verificationTier: VerificationTier,
        assetId: Long,
        shortName: String?
    ) {
        if (verificationTier != TRUSTED) {
            mutableList.add(BaseAssetAboutListItem.DividerItem)
            mutableList.add(createReportItem(assetId, shortName))
        }
    }

    private fun createStatisticsItem(assetDetail: Asset): BaseAssetAboutListItem.StatisticsItem {
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
            return baseAssetAboutListItemMapper.mapToStatisticsItem(
                formattedPriceText = formattedAssetPrice,
                formattedCompactTotalSupplyText = formattedTotalSupply
            )
        }
    }

    private fun createAlgoAboutAssetItem(
        fullName: String?,
        asaUrl: String?
    ): BaseAssetAboutListItem.AboutAssetItem {
        return baseAssetAboutListItemMapper.mapToAboutAssetItem(
            assetName = AssetName.create(fullName),
            assetId = ALGO_ID,
            assetCreatorAddress = null,
            asaUrl = asaUrl.addProtocolIfNeed(),
            displayAsaUrl = asaUrl.removeProtocolIfNeed(),
            peraExplorerUrl = null,
            projectWebsiteUrl = null
        )
    }

    private fun createAboutAssetItem(
        assetId: Long?,
        assetInfo: Asset.AssetInfo?
    ): BaseAssetAboutListItem.AboutAssetItem {
        return baseAssetAboutListItemMapper.mapToAboutAssetItem(
            assetName = AssetName.create(assetInfo?.name?.fullName),
            assetId = assetId,
            assetCreatorAddress = assetInfo?.creator?.publicKey,
            asaUrl = assetInfo?.url?.addProtocolIfNeed(),
            displayAsaUrl = assetInfo?.url.removeProtocolIfNeed(),
            peraExplorerUrl = assetInfo?.explorerUrl,
            projectWebsiteUrl = assetInfo?.project?.url
        )
    }

    private fun createAssetDescriptionItem(
        assetDescription: String?
    ): BaseAssetAboutListItem.BaseAssetDescriptionItem.AssetDescriptionItem? {
        if (assetDescription.isNullOrBlank()) return null
        return baseAssetAboutListItemMapper.mapToAssetDescriptionItem(descriptionText = assetDescription)
    }

    private fun createAlgoDescriptionItem(
        @StringRes descriptionTextResId: Int
    ): BaseAssetAboutListItem.BaseAssetDescriptionItem.AlgoDescriptionItem {
        return baseAssetAboutListItemMapper.mapToAlgoDescriptionItem(descriptionTextResId = descriptionTextResId)
    }

    private fun createSocialMediaItem(social: Asset.Social?): BaseAssetAboutListItem.SocialMediaItem? {
        return social?.run {
            if (discordUrl.isNullOrBlank() && telegramUrl.isNullOrBlank() && twitterUsername.isNullOrBlank()) {
                return null
            }
            return baseAssetAboutListItemMapper.mapToSocialMediaItem(
                discordUrl = discordUrl,
                telegramUrl = telegramUrl,
                twitterUsername = twitterUsername
            )
        }
    }

    private fun createReportItem(assetId: Long, shortName: String?): BaseAssetAboutListItem.ReportItem {
        return baseAssetAboutListItemMapper.mapToReportItem(
            assetName = AssetName.createShortName(shortName),
            assetId = assetId
        )
    }
}
