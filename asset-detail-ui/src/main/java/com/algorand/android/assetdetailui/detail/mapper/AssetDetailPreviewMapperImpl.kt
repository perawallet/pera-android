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

package com.algorand.android.assetdetailui.detail.mapper

import com.algorand.android.accountcore.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.accountcore.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.summary.model.AccountDetailSummary
import com.algorand.android.accountcore.ui.usecase.GetAssetName
import com.algorand.android.assetdetailui.detail.model.AssetDetailPreview
import com.algorand.android.assetdetailui.detail.usecase.GetAssetDetailIconResOfChangePercentage
import com.algorand.android.assetdetailui.detail.usecase.GetAssetDetailTextColorResOfChangePercentage
import com.algorand.android.assetdetailui.detail.usecase.IsChangePercentageVisible
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import java.math.BigDecimal
import javax.inject.Inject

internal class AssetDetailPreviewMapperImpl @Inject constructor(
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper,
    private val getAssetName: GetAssetName,
    private val isChangePercentageVisible: IsChangePercentageVisible,
    private val getAssetDetailIconResOfChangePercentage: GetAssetDetailIconResOfChangePercentage,
    private val getAssetDetailTextColorResOfChangePercentage: GetAssetDetailTextColorResOfChangePercentage,
    private val getAssetDrawableProvider: GetAssetDrawableProvider
) : AssetDetailPreviewMapper {

    override suspend fun invoke(
        baseOwnedAssetDetail: BaseAccountAssetData.BaseOwnedAssetData,
        accountDisplayName: AccountDisplayName,
        isQuickActionButtonsVisible: Boolean,
        isSwapButtonSelected: Boolean,
        isSwapButtonVisible: Boolean,
        isMarketInformationVisible: Boolean,
        last24HoursChange: BigDecimal?,
        formattedAssetPrice: String?,
        accountDetailSummary: AccountDetailSummary,
    ): AssetDetailPreview {
        return with(baseOwnedAssetDetail) {
            AssetDetailPreview(
                assetId = id,
                assetFullName = getAssetName(name),
                isAlgo = isAlgo,
                formattedPrimaryValue = formattedAmount,
                formattedSecondaryValue = getSelectedCurrencyParityValue().getFormattedValue(),
                accountDisplayName = accountDisplayName,
                baseAssetDrawableProvider = getAssetDrawableProvider(id),
                assetPrismUrl = prismUrl,
                verificationTierConfiguration = verificationTierConfigurationMapper(verificationTier),
                isQuickActionButtonsVisible = isQuickActionButtonsVisible,
                isSwapButtonSelected = isSwapButtonSelected,
                isSwapButtonVisible = isSwapButtonVisible,
                isMarketInformationVisible = isMarketInformationVisible,
                isChangePercentageVisible = isChangePercentageVisible(last24HoursChange),
                changePercentage = last24HoursChange,
                formattedAssetPrice = formattedAssetPrice.orEmpty(),
                changePercentageIcon = getAssetDetailIconResOfChangePercentage(last24HoursChange),
                changePercentageTextColor = getAssetDetailTextColorResOfChangePercentage(last24HoursChange),
                accountDetailSummary = accountDetailSummary,
                swapNavigationDestinationEvent = null,
                navigateToDiscoverMarket = null
            )
        }
    }
}
