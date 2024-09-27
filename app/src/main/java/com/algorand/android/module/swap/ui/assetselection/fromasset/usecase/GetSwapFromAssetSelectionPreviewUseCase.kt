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

package com.algorand.android.module.swap.ui.assetselection.fromasset.usecase

import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetsData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.module.foundation.common.isGreaterThan
import com.algorand.android.module.swap.ui.assetselection.mapper.SwapAssetSelectionItemMapper
import com.algorand.android.module.swap.ui.assetselection.model.SwapAssetSelectionItem
import com.algorand.android.module.swap.ui.assetselection.model.SwapAssetSelectionPreview
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetSwapFromAssetSelectionPreviewUseCase @Inject constructor(
    private val getAccountOwnedAssetsData: GetAccountOwnedAssetsData,
    private val swapAssetSelectionItemMapper: SwapAssetSelectionItemMapper
) : GetSwapFromAssetSelectionPreview {

    override suspend fun invoke(accountAddress: String, query: String?): Flow<SwapAssetSelectionPreview> = flow {
        val balanceFilteredAccountAssetList = getBalanceFilteredAccountAssetList(accountAddress, query)
        val swapAssetSelectionItemList = createSwapAssetSelectionItemList(balanceFilteredAccountAssetList)

        val preview = SwapAssetSelectionPreview(
            swapAssetSelectionItemList = swapAssetSelectionItemList,
            isLoading = false,
            screenState = null,
            navigateToAssetAdditionBottomSheetEvent = null,
            assetSelectedEvent = null
        )
        emit(preview)
    }

    private fun doesAssetPassSearchFilter(query: String?, baseAssetData: BaseAccountAssetData): Boolean {
        if (query == null) return true
        val trimmedQuery = query.trim()
        return with(baseAssetData) {
            val doesIdContainQuery = id.toString().contains(trimmedQuery, ignoreCase = true)
            val doesShortNameContainQuery = shortName?.contains(trimmedQuery, ignoreCase = true) == true
            val doesNameContainQuery = name?.contains(trimmedQuery, ignoreCase = true) == true
            doesIdContainQuery || doesShortNameContainQuery || doesNameContainQuery
        }
    }

    private suspend fun createSwapAssetSelectionItemList(
        filteredAccountAssetList: List<OwnedAssetData>
    ): List<SwapAssetSelectionItem> {
        return filteredAccountAssetList.map { ownedAssetData ->
            swapAssetSelectionItemMapper(
                ownedAssetData = ownedAssetData,
                formattedPrimaryValue = ownedAssetData.formattedCompactAmount,
                formattedSecondaryValue = ownedAssetData.parityValueInSelectedCurrency.getFormattedCompactValue(),
                arePrimaryAndSecondaryValueVisible = true
            )
        }
    }

    private suspend fun getBalanceFilteredAccountAssetList(
        accountAddress: String,
        query: String?
    ): List<OwnedAssetData> {
        val accountAssets = getAccountOwnedAssetsData(accountAddress, includeAlgo = true)
        return accountAssets.filter { ownedAssetData ->
            if (ownedAssetData.isAlgo && query.isNullOrBlank()) return@filter true

            val hasUserBalanceOnAsset = ownedAssetData.amount isGreaterThan BigInteger.ZERO
            if (!hasUserBalanceOnAsset) return@filter false

            doesAssetPassSearchFilter(query, ownedAssetData)
        }
    }
}
