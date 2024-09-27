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

package com.algorand.android.modules.accountselection.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AssetAction
import com.algorand.android.modules.accountselection.ui.model.AddAssetAccountSelectionPreview
import com.algorand.android.modules.accountselection.ui.usecase.AddAssetAccountSelectionPreviewUseCase
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddAssetAccountSelectionViewModel @Inject constructor(
    private val addAssetAccountSelectionPreviewUseCase: AddAssetAccountSelectionPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val assetId = savedStateHandle.getOrThrow<Long>(ASSET_ID_KEY)

    private val _addAssetAccountSelectionPreviewFlow = MutableStateFlow<AddAssetAccountSelectionPreview>(
        addAssetAccountSelectionPreviewUseCase.getInitialStatePreview()
    )
    val addAssetAccountSelectionPreviewFlow: StateFlow<AddAssetAccountSelectionPreview>
        get() = _addAssetAccountSelectionPreviewFlow

    init {
        initAddAssetAccountSelectionPreviewFlow()
    }

    fun getAddAssetAction(accountAddress: String): AssetAction {
        return AssetAction(
            assetId = assetId,
            publicKey = accountAddress
        )
    }

    private fun initAddAssetAccountSelectionPreviewFlow() {
        viewModelScope.launch {
            _addAssetAccountSelectionPreviewFlow.value = addAssetAccountSelectionPreviewUseCase
                .getAddAssetAccountSelectionPreview()
        }
    }

    companion object {
        private const val ASSET_ID_KEY = "assetId"
    }
}
