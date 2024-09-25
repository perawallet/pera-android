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

package com.algorand.android.module.swap.ui.assetselection.toasset.usecase

import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.core.component.assetdata.usecase.GetAccountOwnedAssetsData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData
import com.algorand.android.parity.domain.model.ParityValue
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbolOrName
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import com.algorand.android.swap.domain.model.AvailableSwapAsset
import com.algorand.android.module.swap.ui.assetselection.mapper.SwapAssetSelectionItemMapper
import com.algorand.android.module.swap.ui.assetselection.model.SwapAssetSelectionItem
import java.math.BigDecimal.ZERO
import javax.inject.Inject

internal class CreateSwapToToAssetSelectionItemListUseCase @Inject constructor(
    private val getAccountOwnedAssetsData: GetAccountOwnedAssetsData,
    private val swapAssetSelectionItemMapper: SwapAssetSelectionItemMapper,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol
) : CreateSwapToAssetSelectionItemList {

    override suspend operator fun invoke(
        queriedAssetId: Long,
        accountAddress: String,
        availableAssetList: List<AvailableSwapAsset>
    ): List<SwapAssetSelectionItem> {
        val accountAssetsData = getAccountOwnedAssetsData(accountAddress, includeAlgo = queriedAssetId != ALGO_ASSET_ID)
        val (formattedZeroPrimaryValue, formattedZeroSecondaryValue) = getFormattedZeroPrimaryAndSecondaryValues()
        return availableAssetList.map { availableSwapAsset ->
            val accountOwnedAsset = accountAssetsData.firstOrNull { it.id == availableSwapAsset.assetId }
            val (formattedPrimaryValue, formattedSecondaryValue) = getFormattedPrimaryAndSecondaryValuePair(
                accountOwnedAsset,
                formattedZeroPrimaryValue,
                formattedZeroSecondaryValue
            )
            swapAssetSelectionItemMapper(
                availableSwapAsset = availableSwapAsset,
                formattedPrimaryValue = formattedPrimaryValue,
                formattedSecondaryValue = formattedSecondaryValue,
                arePrimaryAndSecondaryValueVisible = accountOwnedAsset != null
            )
        }
    }

    private fun getFormattedZeroPrimaryAndSecondaryValues(): Pair<String, String> {
        val formattedZeroPrimaryValue = ParityValue(ZERO, getPrimaryCurrencySymbolOrName()).getFormattedCompactValue()
        val formattedZeroSecondaryValue = ParityValue(ZERO, getSecondaryCurrencySymbol()).getFormattedCompactValue()
        return formattedZeroPrimaryValue to formattedZeroSecondaryValue
    }

    private fun getFormattedPrimaryAndSecondaryValuePair(
        accountOwnedAsset: BaseOwnedAssetData?,
        formattedZeroPrimaryValue: String,
        formattedZeroSecondaryValue: String,
    ): Pair<String, String> {
        val formattedPrimaryValue = accountOwnedAsset?.formattedCompactAmount ?: formattedZeroPrimaryValue
        val formattedSecondaryValue = accountOwnedAsset?.parityValueInSelectedCurrency?.getFormattedCompactValue()
            ?: formattedZeroSecondaryValue
        return formattedPrimaryValue to formattedSecondaryValue
    }
}