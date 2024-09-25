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

package com.algorand.android.modules.assets.profile.asaprofile.base

import androidx.lifecycle.viewModelScope
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.model.AsaProfilePreview
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.usecase.GetAsaProfilePreview
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AssetAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

abstract class BaseAsaProfileViewModel(
    private val getAsaProfilePreview: GetAsaProfilePreview
) : BaseViewModel() {

    abstract val accountAddress: String?
    abstract val assetId: Long

    private val _asaProfilePreviewFlow = MutableStateFlow<AsaProfilePreview?>(null)
    val asaProfilePreviewFlow: StateFlow<AsaProfilePreview?> get() = _asaProfilePreviewFlow.asStateFlow()

    fun getAssetAction(): AssetAction {
        return AssetAction(
            publicKey = accountAddress,
            assetId = assetId,
            assetFullName = _asaProfilePreviewFlow.value?.assetFullName?.assetName,
            assetShortName = _asaProfilePreviewFlow.value?.assetShortName?.assetName
        )
    }

    protected fun initAsaPreviewFlow() {
        viewModelScope.launch {
            getAsaProfilePreview(accountAddress, assetId).distinctUntilChanged().collect { asaProfilePreview ->
                _asaProfilePreviewFlow.emit(asaProfilePreview)
            }
        }
    }

    protected companion object {
        const val ACCOUNT_ADDRESS_KEY = "accountAddress"
        const val ASSET_ID_KEY = "assetId"
    }
}
