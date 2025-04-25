/*
 * Copyright 2025 Pera Wallet, LDA
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
import com.algorand.android.modules.assets.action.base.BaseAssetActionViewModel
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.usecase.AccountAddressUseCase
import com.algorand.android.utils.getOrThrow
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OptInAssetActionViewModel @Inject constructor(
    accountAddressUseCase: AccountAddressUseCase,
    stateDelegate: StateDelegate<ViewState>,
    verificationTierConfigurationDecider: VerificationTierConfigurationDecider,
    fetchAndCacheAssets: FetchAndCacheAssets,
    getAsset: GetAsset,
    savedStateHandle: SavedStateHandle
) : BaseAssetActionViewModel(
    accountAddressUseCase,
    stateDelegate,
    verificationTierConfigurationDecider,
    fetchAndCacheAssets,
    getAsset
) {

    val assetAction: AssetAction = savedStateHandle.getOrThrow(ASSET_ACTION_KEY)
    val accountAddress: String = assetAction.publicKey.orEmpty()

    override val assetId: Long = assetAction.assetId

    init {
        fetchAssetDescription(assetId)
    }
}
