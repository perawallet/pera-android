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

package com.algorand.android.modules.assets.action.optin

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.models.AssetAction
import com.algorand.android.module.asset.action.ui.AssetActionViewModel
import com.algorand.android.module.asset.action.ui.usecase.GetAssetActionPreview
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OptInAssetActionViewModel @Inject constructor(
    getAssetActionPreview: GetAssetActionPreview,
    savedStateHandle: SavedStateHandle
) : AssetActionViewModel(getAssetActionPreview) {

    val assetAction: AssetAction = savedStateHandle.getOrThrow(ASSET_ACTION_KEY)

    override val assetId: Long = assetAction.assetId

    init {
        initPreview(null)
    }
}
