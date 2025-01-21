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

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.model.AssetInboxAllAccountsPreview
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.usecase.AssetInboxAllAccountsPreviewUseCase
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class AssetInboxAllAccountsViewModel @Inject constructor(
    private val assetInboxAllAccountsPreviewUseCase: AssetInboxAllAccountsPreviewUseCase
) : ViewModel() {

    private val _viewStateFlow = MutableStateFlow(assetInboxAllAccountsPreviewUseCase.getInitialPreview())

    val viewStateFlow: StateFlow<AssetInboxAllAccountsPreview> = _viewStateFlow.asStateFlow()

    fun initializePreview() {
        viewModelScope.launchIO {
            assetInboxAllAccountsPreviewUseCase.getAssetInboxAllAccountsPreview(
                _viewStateFlow.value
            ).collectLatest { preview ->
                _viewStateFlow.value = preview
            }
        }
    }
}
