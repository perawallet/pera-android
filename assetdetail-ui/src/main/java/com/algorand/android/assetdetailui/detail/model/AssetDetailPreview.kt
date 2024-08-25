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

package com.algorand.android.assetdetailui.detail.model

import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration
import com.algorand.android.accountcore.ui.summary.model.AccountDetailSummary
import com.algorand.android.drawableui.asset.BaseAssetDrawableProvider
import com.algorand.android.foundation.Event
import com.algorand.android.swap.common.model.SwapNavigationDestination
import java.math.BigDecimal

data class AssetDetailPreview(
    val assetFullName: AssetName,
    val assetId: Long,
    val formattedPrimaryValue: String,
    val formattedSecondaryValue: String,
    val isAlgo: Boolean,
    val verificationTierConfiguration: VerificationTierConfiguration,
    val accountDetailSummary: AccountDetailSummary,
    val accountDisplayName: AccountDisplayName,
    val baseAssetDrawableProvider: BaseAssetDrawableProvider,
    val assetPrismUrl: String?,
    val isQuickActionButtonsVisible: Boolean,
    val isSwapButtonSelected: Boolean,
    val isSwapButtonVisible: Boolean,
    val onShowGlobalErrorEvent: Event<Int>? = null,
    val isMarketInformationVisible: Boolean,
    val formattedAssetPrice: String,
    val isChangePercentageVisible: Boolean,
    val changePercentage: BigDecimal?,
    val changePercentageIcon: Int?,
    val changePercentageTextColor: Int?,
    val swapNavigationDestinationEvent: Event<SwapNavigationDestination>?,
    val navigateToDiscoverMarket: Event<String>?
)
