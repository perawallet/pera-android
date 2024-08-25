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

package com.algorand.android.modules.swap.assetselection.toasset.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.swap.assetselection.base.BaseSwapAssetSelectionViewModel
import com.algorand.android.swapui.assetselection.model.SwapAssetSelectionItem
import com.algorand.android.swapui.assetselection.model.SwapAssetSelectionPreview
import com.algorand.android.swapui.assetselection.toasset.usecase.GetSwapToAssetSelectionPreview
import com.algorand.android.swapui.assetselection.toasset.usecase.GetUpdatedPreviewWithAssetSelection
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@HiltViewModel
class SwapToAssetSelectionViewModel @Inject constructor(
    private val getSwapToAssetSelectionPreview: GetSwapToAssetSelectionPreview,
    private val getUpdatedPreviewWithAssetSelection: GetUpdatedPreviewWithAssetSelection,
    savedState: SavedStateHandle
) : BaseSwapAssetSelectionViewModel() {

    private val fromAssetId: Long = savedState.getOrThrow(ASSET_ID_KEY)
    val accountAddress: String = savedState.getOrThrow(ACCOUNT_ADDRESS)

    override suspend fun onQueryChanged(query: String?): Flow<SwapAssetSelectionPreview> {
        return getSwapToAssetSelectionPreview(
            assetId = fromAssetId,
            accountAddress = accountAddress,
            query = searchQuery
        )
    }

    fun onAssetSelected(swapAssetSelectionItem: SwapAssetSelectionItem) {
        val previousState = swapAssetSelectionPreviewFlow.value ?: return
        viewModelScope.launch {
            val newPreview = getUpdatedPreviewWithAssetSelection(
                accountAddress,
                swapAssetSelectionItem,
                previousState
            )
            updatePreview(newPreview)
        }
    }

    companion object {
        private const val ASSET_ID_KEY = "fromAssetId"
        private const val ACCOUNT_ADDRESS = "accountAddress"
    }
}
