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

package com.algorand.android.customviews.accountandassetitem.mapper

import com.algorand.android.module.account.core.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.module.account.core.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.module.account.core.ui.model.AssetName
import com.algorand.android.module.account.core.ui.model.BaseItemConfiguration
import com.algorand.android.module.account.core.ui.model.VerificationTierConfiguration
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import java.math.BigDecimal
import javax.inject.Inject

class AssetItemConfigurationMapper @Inject constructor(
    private val getAssetName: GetAssetName,
    private val getAssetDrawableProvider: GetAssetDrawableProvider,
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper
) {

    suspend fun mapTo(
        isAmountInSelectedCurrencyVisible: Boolean,
        secondaryValueText: String,
        assetId: Long,
        name: String?,
        shortName: String?,
        formattedCompactAmount: String,
        verificationTier: VerificationTier,
        primaryValue: BigDecimal?
    ): BaseItemConfiguration.BaseAssetItemConfiguration.AssetItemConfiguration {
        return BaseItemConfiguration.BaseAssetItemConfiguration.AssetItemConfiguration(
            assetId = assetId,
            assetIconDrawableProvider = getAssetDrawableProvider(assetId),
            primaryAssetName = getAssetName(name),
            secondaryAssetName = getAssetName(shortName),
            primaryValueText = formattedCompactAmount,
            secondaryValueText = secondaryValueText.takeIf { isAmountInSelectedCurrencyVisible },
            verificationTierConfiguration = verificationTierConfigurationMapper(verificationTier),
            primaryValue = primaryValue
        )
    }

    suspend fun mapTo(
        assetId: Long,
        assetFullName: AssetName,
        assetShortName: AssetName,
        showWithAssetId: Boolean,
        verificationTierConfiguration: VerificationTierConfiguration
    ): BaseItemConfiguration.BaseAssetItemConfiguration.AssetItemConfiguration {
        return BaseItemConfiguration.BaseAssetItemConfiguration.AssetItemConfiguration(
            assetId = assetId,
            assetIconDrawableProvider = getAssetDrawableProvider(assetId),
            primaryAssetName = assetFullName,
            secondaryAssetName = assetShortName,
            showWithAssetId = showWithAssetId,
            verificationTierConfiguration = verificationTierConfiguration
        )
    }
}
