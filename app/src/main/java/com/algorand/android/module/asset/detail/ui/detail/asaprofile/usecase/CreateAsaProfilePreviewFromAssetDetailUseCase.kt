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

package com.algorand.android.module.asset.detail.ui.detail.asaprofile.usecase

import com.algorand.android.module.account.core.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.module.account.core.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.assetdetail.component.AssetConstants.MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier.SUSPICIOUS
import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.model.AsaProfilePreview
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.model.AsaStatusPreview
import com.algorand.android.module.asset.detail.ui.detail.usecase.GetAssetDetailIconResOfChangePercentage
import com.algorand.android.module.asset.detail.ui.detail.usecase.GetAssetDetailTextColorResOfChangePercentage
import com.algorand.android.module.asset.detail.ui.detail.usecase.IsChangePercentageVisible
import com.algorand.android.parity.domain.usecase.GetAssetExchangeParityValue
import java.math.BigDecimal.ZERO
import javax.inject.Inject

internal class CreateAsaProfilePreviewFromAssetDetailUseCase @Inject constructor(
    private val getAssetName: GetAssetName,
    private val getSelectedAssetExchangeParityValue: GetAssetExchangeParityValue,
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper,
    private val getAssetDrawableProvider: GetAssetDrawableProvider,
    private val getAssetDetailTextColorResOfChangePercentage: GetAssetDetailTextColorResOfChangePercentage,
    private val getAssetDetailIconResOfChangePercentage: GetAssetDetailIconResOfChangePercentage,
    private val isChangePercentageVisible: IsChangePercentageVisible
) : CreateAsaProfilePreviewFromAssetDetail {

    override suspend fun invoke(asset: Asset, asaStatusPreview: AsaStatusPreview?): AsaProfilePreview {
        val last24HoursAlgoPriceChangePercentage = asset.assetInfo?.fiat?.last24HoursAlgoPriceChangePercentage
        return with(asset) {
            AsaProfilePreview(
                isAlgo = isAlgo,
                assetFullName = getAssetName(asset.assetInfo?.name?.fullName),
                assetShortName = getAssetName(asset.assetInfo?.name?.shortName),
                assetId = id,
                formattedAssetPrice = getFormattedAssetPrice(),
                verificationTierConfiguration = verificationTierConfigurationMapper(verificationTier),
                baseAssetDrawableProvider = getAssetDrawableProvider(asset),
                assetPrismUrl = assetInfo?.logo?.uri,
                asaStatusPreview = asaStatusPreview,
                isMarketInformationVisible = isMarketInformationVisible(),
                isChangePercentageVisible = isChangePercentageVisible(last24HoursAlgoPriceChangePercentage),
                changePercentage = last24HoursAlgoPriceChangePercentage,
                changePercentageIcon = getAssetDetailIconResOfChangePercentage(last24HoursAlgoPriceChangePercentage),
                changePercentageTextColor = getAssetDetailTextColorResOfChangePercentage(
                    last24HoursAlgoPriceChangePercentage
                ),
                asset = asset
            )
        }
    }

    private fun Asset.getFormattedAssetPrice(): String {
        return getSelectedAssetExchangeParityValue(isAlgo, assetInfo?.fiat?.usdValue ?: ZERO, getDecimalsOrZero())
            .getFormattedValue(
                isCompact = true,
                minValueToDisplayExactAmount = MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT
            )
    }

    private fun Asset.isMarketInformationVisible(): Boolean {
        return assetInfo?.isAvailableOnDiscoverMobile ?: false && verificationTier != SUSPICIOUS && hasUsdValue()
    }
}
