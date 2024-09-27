/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.mapper

import com.algorand.android.models.AssetTransferAmountAssetPreview
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.account.core.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.module.account.core.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import javax.inject.Inject

class AssetTransferAmountAssetPreviewMapper @Inject constructor(
    private val getAssetDrawableProvider: GetAssetDrawableProvider,
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper,
    private val getAssetName: GetAssetName
) {

    suspend fun mapTo(accountAssetData: BaseAccountAssetData.BaseOwnedAssetData): AssetTransferAmountAssetPreview {
        return AssetTransferAmountAssetPreview(
            shortName = getAssetName(accountAssetData.shortName),
            decimals = accountAssetData.decimals,
            formattedSelectedCurrencyValue = accountAssetData.getSelectedCurrencyParityValue()
                .getFormattedCompactValue(),
            assetId = accountAssetData.id,
            fullName = getAssetName(accountAssetData.name),
            isAlgo = accountAssetData.isAlgo,
            verificationTierConfiguration = verificationTierConfigurationMapper(accountAssetData.verificationTier),
            formattedAmount = accountAssetData.formattedCompactAmount,
            isAmountInSelectedCurrencyVisible = accountAssetData.isAmountInSelectedCurrencyVisible,
            prismUrl = accountAssetData.prismUrl,
            assetDrawableProvider = getAssetDrawableProvider(accountAssetData.id)
        )
    }
}
