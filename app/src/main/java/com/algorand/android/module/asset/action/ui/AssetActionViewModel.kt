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

package com.algorand.android.module.asset.action.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.asset.action.ui.model.AssetActionInformation
import com.algorand.android.module.asset.action.ui.model.AssetActionPreview
import com.algorand.android.module.asset.action.ui.usecase.GetAssetActionPreview
import com.algorand.android.module.foundation.coroutine.CoroutineExtensions.launchIO
import com.algorand.android.module.transaction.component.domain.TransactionConstants.MIN_FEE
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

abstract class AssetActionViewModel(private val getAssetActionPreview: GetAssetActionPreview) : ViewModel() {

    private val _assetActionPreviewFlow = MutableStateFlow<AssetActionPreview?>(null)
    val assetActionPreviewFlow: StateFlow<AssetActionPreview?> = _assetActionPreviewFlow.asStateFlow()

    abstract val assetId: Long

    protected fun initPreview(accountAddress: String?) {
        viewModelScope.launchIO {
            getAssetActionPreview(assetId, accountAddress).collectLatest {
                _assetActionPreviewFlow.emit(it)
            }
        }
    }

    fun getTransactionFee(): String {
        // For now, the calculation of transactions
        // fee is hard. So, we use `MIN_FEE` for representing `Adding Asset Transaction Fee`
        return MIN_FEE.formatAsAlgoString().formatAsAlgoAmount()
    }

    fun getAssetActionInformation(): AssetActionInformation? {
        return _assetActionPreviewFlow.value?.assetActionInformation
    }

    protected companion object {
        const val ASSET_ACTION_KEY = "assetAction"
        const val SHOULD_WAIT_FOR_CONFIRMATION_KEY = "shouldWaitForConfirmation"
        const val DEFAULT_WAIT_FOR_CONFIRMATION_PARAM = false
    }
}
