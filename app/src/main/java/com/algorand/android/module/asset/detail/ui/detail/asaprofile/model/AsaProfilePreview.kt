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

package com.algorand.android.module.asset.detail.ui.detail.asaprofile.model

import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration
import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.drawableui.asset.BaseAssetDrawableProvider
import java.math.BigDecimal

data class AsaProfilePreview(
    val isAlgo: Boolean,
    val assetFullName: AssetName,
    val assetShortName: AssetName,
    val assetId: Long,
    val formattedAssetPrice: String?,
    val verificationTierConfiguration: VerificationTierConfiguration,
    val baseAssetDrawableProvider: BaseAssetDrawableProvider,
    val assetPrismUrl: String?,
    val asaStatusPreview: AsaStatusPreview?,
    val isMarketInformationVisible: Boolean,
    val isChangePercentageVisible: Boolean,
    val changePercentage: BigDecimal?,
    val changePercentageIcon: Int?,
    val changePercentageTextColor: Int?,
    val asset: Asset
) {
    val hasFormattedPrice: Boolean
        get() = formattedAssetPrice.isNullOrBlank().not()
}
