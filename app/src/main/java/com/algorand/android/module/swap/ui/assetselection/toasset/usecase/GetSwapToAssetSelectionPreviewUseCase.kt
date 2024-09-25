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

import com.algorand.android.designsystem.R
import com.algorand.android.designsystem.ScreenStateMapper.mapToCustomState
import com.algorand.android.module.swap.component.domain.model.AvailableSwapAsset
import com.algorand.android.module.swap.component.domain.usecase.GetAvailableTargetSwapAssets
import com.algorand.android.module.swap.ui.assetselection.model.SwapAssetSelectionPreview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetSwapToAssetSelectionPreviewUseCase @Inject constructor(
    private val getAvailableTargetSwapAssets: GetAvailableTargetSwapAssets,
    private val createSwapToAssetSelectionItemList: CreateSwapToAssetSelectionItemList
) : GetSwapToAssetSelectionPreview {

    override suspend fun invoke(
        assetId: Long,
        accountAddress: String,
        query: String?
    ): Flow<SwapAssetSelectionPreview> = flow {
        emit(getLoadingState())
        val preview = getAvailableTargetSwapAssets(assetId, query).use(
            onSuccess = {
                getSuccessState(assetId, accountAddress, it)
            },
            onFailed = { _, _ ->
                getErrorState()
            }
        )
        emit(preview)
    }

    private suspend fun getSuccessState(
        assetId: Long,
        accountAddress: String,
        availableAssets: List<AvailableSwapAsset>
    ): SwapAssetSelectionPreview {
        val swapAssetSelectionItemList = createSwapToAssetSelectionItemList(assetId, accountAddress, availableAssets)

        val screenState = if (swapAssetSelectionItemList.isEmpty()) {
            mapToCustomState(title = R.string.no_asset_found)
        } else {
            null
        }

        return SwapAssetSelectionPreview(
            swapAssetSelectionItemList = swapAssetSelectionItemList,
            isLoading = false,
            screenState = screenState,
            navigateToAssetAdditionBottomSheetEvent = null,
            assetSelectedEvent = null
        )
    }

    private fun getErrorState(): SwapAssetSelectionPreview {
        return SwapAssetSelectionPreview(
            swapAssetSelectionItemList = emptyList(),
            isLoading = false,
            screenState = mapToCustomState(
                title = R.string.something_went_wrong,
                description = R.string.we_can_not_show_assets
            ),
            navigateToAssetAdditionBottomSheetEvent = null,
            assetSelectedEvent = null
        )
    }

    private fun getLoadingState(): SwapAssetSelectionPreview {
        return SwapAssetSelectionPreview(
            swapAssetSelectionItemList = emptyList(),
            isLoading = true,
            screenState = null,
            navigateToAssetAdditionBottomSheetEvent = null,
            assetSelectedEvent = null
        )
    }
}
