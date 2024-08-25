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

package com.algorand.android.modules.assets.profile.about.ui.usecase

import androidx.annotation.StringRes
import com.algorand.android.R
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.assetdetail.component.AssetConstants.MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier.SUSPICIOUS
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier.TRUSTED
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier.UNKNOWN
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier.UNVERIFIED
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier.VERIFIED
import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.assetdetail.component.assetabout.domain.usecase.CacheAssetDetailToAsaProfile
import com.algorand.android.assetdetail.component.assetabout.domain.usecase.ClearAsaProfileCache
import com.algorand.android.assetdetail.component.assetabout.domain.usecase.GetAssetFlowFromAsaProfileCache
import com.algorand.android.modules.assets.profile.about.ui.mapper.AssetAboutPreviewMapper
import com.algorand.android.modules.assets.profile.about.ui.mapper.BaseAssetAboutListItemMapper
import com.algorand.android.modules.assets.profile.about.ui.model.AssetAboutPreview
import com.algorand.android.modules.assets.profile.about.ui.model.BaseAssetAboutListItem
import com.algorand.android.parity.domain.usecase.GetAssetExchangeParityValue
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.browser.addProtocolIfNeed
import com.algorand.android.utils.browser.removeProtocolIfNeed
import com.algorand.android.utils.formatAmount
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class AssetAboutPreviewUseCase @Inject constructor(
    private val cacheAssetDetailToAsaProfile: CacheAssetDetailToAsaProfile,
    private val getAssetFlowFromAsaProfileCache: GetAssetFlowFromAsaProfileCache,
    private val clearAsaProfileCache: ClearAsaProfileCache,
    private val assetAboutPreviewMapper: AssetAboutPreviewMapper,
    private val baseAssetAboutListItemMapper: BaseAssetAboutListItemMapper,
    private val getAssetDetail: GetAssetDetail,
    private val getAssetExchangeParityValue: GetAssetExchangeParityValue
) {

    suspend fun clearAsaProfileLocalCache() {
        clearAsaProfileCache()
    }

    suspend fun cacheAssetDetailToAsaProfileLocalCache(assetId: Long) {
        cacheAssetDetailToAsaProfile(assetId)
    }

    fun getAssetAboutPreview(assetId: Long) = flow {
        emit(assetAboutPreviewMapper.mapToAssetAboutPreviewInitialState())
        if (assetId == ALGO_ASSET_ID) {
            val assetDetail = getAssetDetail(assetId) ?: return@flow
            val algoAboutPreview = createAlgoAboutPreview(assetDetail)
            emit(algoAboutPreview)
        } else {
            getAssetFlowFromAsaProfileCache().collect { cacheResult ->
                cacheResult?.getDataOrNull()?.let { assetDetail ->
                    emit(createAssetAboutPreview(assetDetail))
                }
            }
        }
    }

    private fun createAlgoAboutPreview(asset: Asset): AssetAboutPreview {
        val algorandAboutList = mutableListOf<BaseAssetAboutListItem>().apply {
            with(asset) {
                add(createStatisticsItem(this))
                add(BaseAssetAboutListItem.DividerItem)
                add(
                    baseAssetAboutListItemMapper.mapToAboutAssetItem(
                        assetName = AssetName.create(asset.assetInfo?.name?.fullName),
                        assetId = null,
                        assetCreatorAddress = null,
                        asaUrl = null,
                        displayAsaUrl = null,
                        peraExplorerUrl = null,
                        projectWebsiteUrl = null
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

    private fun createAssetAboutPreview(asset: Asset): AssetAboutPreview {
        val assetAboutList = mutableListOf<BaseAssetAboutListItem>().apply {
            with(asset) {

                add(createStatisticsItem(this))
                add(BaseAssetAboutListItem.DividerItem)

                add(createAboutAssetItem(asset))

                createAssetDescriptionItem(assetInfo?.description)?.run {
                    add(BaseAssetAboutListItem.DividerItem)
                    add(this)
                }

                createSocialMediaItem(assetInfo?.social)?.run {
                    add(BaseAssetAboutListItem.DividerItem)
                    add(this)
                }
                addReportItemIfNeed(this@apply, verificationTier, id, assetInfo?.name?.shortName)
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

    private fun createStatisticsItem(asset: Asset): BaseAssetAboutListItem.StatisticsItem {
        with(asset) {
            val formattedAssetPrice = getAssetExchangeParityValue(
                isAlgo = isAlgo,
                usdValue = assetInfo?.fiat?.usdValue ?: BigDecimal.ZERO,
                decimals = getDecimalsOrZero()
            ).getFormattedValue(minValueToDisplayExactAmount = MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT)
            return baseAssetAboutListItemMapper.mapToStatisticsItem(
                formattedPriceText = formattedAssetPrice,
                formattedCompactTotalSupplyText = assetInfo?.supply?.total?.formatAmount(
                    decimals = getDecimalsOrZero(),
                    isCompact = true,
                    isDecimalFixed = false
                )
            )
        }
    }

    private fun createAboutAssetItem(asset: Asset): BaseAssetAboutListItem.AboutAssetItem {
        return with(asset) {
            baseAssetAboutListItemMapper.mapToAboutAssetItem(
                assetName = AssetName.create(asset.assetInfo?.name?.fullName),
                assetId = asset.id,
                assetCreatorAddress = assetInfo?.creator?.publicKey,
                asaUrl = assetInfo?.url.addProtocolIfNeed(),
                displayAsaUrl = assetInfo?.url.removeProtocolIfNeed(),
                peraExplorerUrl = assetInfo?.explorerUrl,
                projectWebsiteUrl = assetInfo?.project?.url
            )
        }
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
