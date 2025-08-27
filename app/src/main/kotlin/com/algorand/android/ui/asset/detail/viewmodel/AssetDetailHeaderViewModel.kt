/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.asset.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewState
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewState.Content
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewState.Idle
import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.wallet.asset.domain.usecase.GetAssetDetail
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AssetDetailHeaderViewModel @Inject constructor(
    private val getAssetDetail: GetAssetDetail,
    private val assetIconDrawableMapper: AssetIconDrawableMapper,
    private val verificationTierMapper: VerificationTierConfigurationDecider,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(Idle)
    }

    fun init(assetId: Long) {
        stateDelegate.onState<Idle> {
            viewModelScope.launch {
                val assetDetail = getAssetDetail(assetId) ?: return@launch
                val viewState = Content(
                    assetId = assetDetail.id,
                    assetIconDrawable = assetIconDrawableMapper.map(assetDetail),
                    assetName = assetDetail.fullName.orEmpty(),
                    isFavorite = false, // TODO
                    isNotificationsEnabled = false, // TODO
                    verificationTierConfiguration = verificationTierMapper
                        .decideVerificationTierConfiguration(assetDetail.verificationTier)
                )
                stateDelegate.updateState { viewState }
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val assetId: Long,
            val assetIconDrawable: AssetIconDrawable,
            val assetName: String,
            val verificationTierConfiguration: VerificationTierConfiguration,
            val isFavorite: Boolean,
            val isNotificationsEnabled: Boolean
        ) : ViewState
    }
}
