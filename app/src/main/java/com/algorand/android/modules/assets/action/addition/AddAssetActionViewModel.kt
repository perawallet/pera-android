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

package com.algorand.android.modules.assets.action.addition

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.assetaction.AssetActionViewModel
import com.algorand.android.assetaction.usecase.GetAssetActionPreview
import com.algorand.android.models.AssetAction
import com.algorand.android.module.transaction.ui.addasset.model.AddAssetTransactionPayload
import com.algorand.android.utils.getOrElse
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddAssetActionViewModel @Inject constructor(
    getAssetActionPreview: GetAssetActionPreview,
    savedStateHandle: SavedStateHandle
) : AssetActionViewModel(getAssetActionPreview) {

    private val assetAction: AssetAction = savedStateHandle.getOrThrow(ASSET_ACTION_KEY)
    val accountAddress: String = assetAction.publicKey.orEmpty()
    val shouldWaitForConfirmation = savedStateHandle.getOrElse(
        SHOULD_WAIT_FOR_CONFIRMATION_KEY,
        DEFAULT_WAIT_FOR_CONFIRMATION_PARAM
    )

    override val assetId: Long
        get() = assetAction.assetId

    init {
        initPreview(accountAddress)
    }

    fun getAddAssetTransactionPayload(): AddAssetTransactionPayload {
        return AddAssetTransactionPayload(
            address = accountAddress,
            assetId = assetAction.assetId,
            shouldWaitForConfirmation = shouldWaitForConfirmation,
            assetName = assetAction.assetFullName ?: assetAction.assetShortName ?: ""
        )
    }
}
