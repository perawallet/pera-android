@file:Suppress("LongParameterList")
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

package com.algorand.android.module.asset.detail.ui.detail.usecase

import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetailFlow
import com.algorand.android.module.account.core.ui.summary.usecase.GetAccountDetailSummary
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.info.domain.usecase.IsAssetOwnedByAccount
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier.SUSPICIOUS
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAsset
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.module.asset.detail.ui.detail.mapper.AssetDetailPreviewMapper
import com.algorand.android.module.asset.detail.ui.detail.model.AssetDetailPreview
import com.algorand.android.module.parity.domain.usecase.GetAssetExchangeParityValue
import com.algorand.android.module.swap.component.reddot.domain.usecase.GetSwapFeatureRedDotVisibility
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class GetAssetDetailPreviewFlowUseCase @Inject constructor(
    private val getAccountDetailFlow: GetAccountDetailFlow,
    private val getSwapFeatureRedDotVisibility: GetSwapFeatureRedDotVisibility,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val isAssetOwnedByAccount: IsAssetOwnedByAccount,
    private val getAssetDetail: GetAssetDetail,
    private val fetchAsset: FetchAsset,
    private val assetDetailPreviewMapper: AssetDetailPreviewMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAssetExchangeParityValue: GetAssetExchangeParityValue,
    private val getAccountDetailSummary: GetAccountDetailSummary
) : GetAssetDetailPreviewFlow {

    override suspend fun invoke(
        accountAddress: String,
        assetId: Long,
        isQuickActionButtonsVisible: Boolean
    ): Flow<AssetDetailPreview?> {
        return getAccountDetailFlow(accountAddress).filterNotNull().map {
            val baseOwnedAssetDetail = getAccountBaseOwnedAssetData(accountAddress, assetId) ?: return@map null
            val assetDetail = getAssetDetail(assetId) ?: fetchAsset(assetId).getDataOrNull()
            val isWatchAccount = it.accountType == AccountType.NoAuth
            val safeIsQuickActionButtonsVisible = isQuickActionButtonsVisible && !isWatchAccount
            assetDetailPreviewMapper(
                baseOwnedAssetDetail = baseOwnedAssetDetail,
                accountDisplayName = getAccountDisplayName(accountAddress),
                isQuickActionButtonsVisible = safeIsQuickActionButtonsVisible,
                isSwapButtonSelected = getSwapFeatureRedDotVisibility() && baseOwnedAssetDetail.isAlgo,
                isSwapButtonVisible = isAssetOwnedByAccount(accountAddress, assetId) && safeIsQuickActionButtonsVisible,
                isMarketInformationVisible = isMarketInformationVisible(assetDetail),
                last24HoursChange = assetDetail?.assetInfo?.fiat?.last24HoursAlgoPriceChangePercentage,
                formattedAssetPrice = getFormattedAssetPrice(assetDetail),
                accountDetailSummary = getAccountDetailSummary(it)
            )
        }.distinctUntilChanged()
    }

    private fun isMarketInformationVisible(asset: Asset?): Boolean {
        return asset?.run {
            (assetInfo?.isAvailableOnDiscoverMobile ?: false) && verificationTier != SUSPICIOUS && hasUsdValue()
        } ?: false
    }

    private fun getFormattedAssetPrice(assetDetail: Asset?): String {
        if (assetDetail == null || assetDetail.hasUsdValue()) return ""
        return with(assetDetail) {
            val safeUsdValue = this.assetInfo?.fiat?.usdValue ?: BigDecimal.ZERO
            getAssetExchangeParityValue(isAlgo, safeUsdValue, getDecimalsOrZero()).getFormattedValue(isCompact = true)
        }
    }
}
