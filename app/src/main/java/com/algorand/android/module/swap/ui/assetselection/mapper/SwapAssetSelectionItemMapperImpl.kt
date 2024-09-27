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

package com.algorand.android.module.swap.ui.assetselection.mapper

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.account.core.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.module.account.core.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.module.swap.component.domain.model.AvailableSwapAsset
import com.algorand.android.module.swap.ui.assetselection.model.SwapAssetSelectionItem
import javax.inject.Inject

internal class SwapAssetSelectionItemMapperImpl @Inject constructor(
    private val getAssetName: GetAssetName,
    private val getAssetDrawableProvider: GetAssetDrawableProvider,
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper
) : SwapAssetSelectionItemMapper {

    override suspend fun invoke(
        availableSwapAsset: AvailableSwapAsset,
        formattedPrimaryValue: String,
        formattedSecondaryValue: String,
        arePrimaryAndSecondaryValueVisible: Boolean
    ): SwapAssetSelectionItem {
        val assetName = getAssetName(availableSwapAsset.assetName)
        return with(availableSwapAsset) {
            SwapAssetSelectionItem(
                assetId = assetId,
                assetFullName = assetName,
                assetShortName = getAssetName(assetShortName),
                formattedPrimaryValue = formattedPrimaryValue,
                formattedSecondaryValue = formattedSecondaryValue,
                arePrimaryAndSecondaryValueVisible = arePrimaryAndSecondaryValueVisible,
                assetDrawableProvider = getAssetDrawableProvider(assetId, assetName, logoUrl),
                verificationTier = verificationTierConfigurationMapper(verificationTier)
            )
        }
    }

    override suspend fun invoke(
        ownedAssetData: BaseAccountAssetData.BaseOwnedAssetData,
        formattedPrimaryValue: String,
        formattedSecondaryValue: String,
        arePrimaryAndSecondaryValueVisible: Boolean
    ): SwapAssetSelectionItem {
        val assetFullName = getAssetName(ownedAssetData.name)
        return SwapAssetSelectionItem(
            assetId = ownedAssetData.id,
            assetFullName = assetFullName,
            assetShortName = getAssetName(ownedAssetData.shortName),
            formattedPrimaryValue = formattedPrimaryValue,
            formattedSecondaryValue = formattedSecondaryValue,
            arePrimaryAndSecondaryValueVisible = arePrimaryAndSecondaryValueVisible,
            assetDrawableProvider = getAssetDrawableProvider(ownedAssetData.id, assetFullName, ownedAssetData.prismUrl),
            verificationTier = verificationTierConfigurationMapper(ownedAssetData.verificationTier)
        )
    }
}
