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
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewEvent
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewState
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewState.Content
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewState.Idle
import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.usecase.SetAssetFavoriteStatus
import com.algorand.wallet.asset.domain.usecase.SetAssetPriceAlertStatus
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@HiltViewModel
class AssetDetailHeaderViewModel @Inject constructor(
    private val assetIconDrawableMapper: AssetIconDrawableMapper,
    private val verificationTierMapper: VerificationTierConfigurationDecider,
    private val setAssetFavoriteStatus: SetAssetFavoriteStatus,
    private val setAssetPriceAlertStatus: SetAssetPriceAlertStatus,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private var toggleFavoriteJob: Job? = null
    private var togglePriceAlertJob: Job? = null

    private val stateUpdateMutex = Mutex()

    init {
        stateDelegate.setDefaultState(Idle)
    }

    fun init(asset: Asset) {
        stateDelegate.onState<Idle> {
            viewModelScope.launch {
                val viewState = Content(
                    assetId = asset.id,
                    assetIconDrawable = assetIconDrawableMapper.map(asset),
                    assetName = asset.fullName.orEmpty(),
                    isFavorite = asset.assetInfo?.isFavorite,
                    isNotificationsEnabled = asset.assetInfo?.isPriceAlertEnabled,
                    verificationTierConfiguration = verificationTierMapper
                        .decideVerificationTierConfiguration(asset.verificationTier)
                )
                stateDelegate.updateState { viewState }
            }
        }
    }

    fun toggleFavoriteStatus() {
        val currentContentState = (stateDelegate.state.value as? Content) ?: return
        val currentFavoriteStatus = currentContentState.isFavorite ?: return
        val updatedFavoriteStatus = !currentFavoriteStatus
        stateDelegate.updateState { currentContentState.copy(isFavorite = updatedFavoriteStatus) }
        toggleFavoriteJob?.cancel()
        toggleFavoriteJob = viewModelScope.launch {
            setAssetFavoriteStatus(currentContentState.assetId, updatedFavoriteStatus).use(
                onSuccess = {},
                onFailed = { _, _ ->
                    updateContentState { it.copy(isFavorite = !updatedFavoriteStatus) }
                    eventDelegate.sendEvent(ViewEvent.DisplayFailedToToggleFavoriteError)
                }
            )
        }
    }

    fun togglePriceAlertStatus() {
        val currentContentState = (stateDelegate.state.value as? Content) ?: return
        val currentPriceAlertStatus = currentContentState.isNotificationsEnabled ?: return
        val updatePriceAlertEnabledStatus = !currentPriceAlertStatus
        stateDelegate.updateState { currentContentState.copy(isNotificationsEnabled = updatePriceAlertEnabledStatus) }
        togglePriceAlertJob?.cancel()
        togglePriceAlertJob = viewModelScope.launch {
            setAssetPriceAlertStatus(currentContentState.assetId, updatePriceAlertEnabledStatus).use(
                onSuccess = {},
                onFailed = { _, _ ->
                    updateContentState { it.copy(isNotificationsEnabled = !updatePriceAlertEnabledStatus) }
                    eventDelegate.sendEvent(ViewEvent.DisplayFailedToTogglePriceAlertError)
                }
            )
        }
    }

    private suspend fun updateContentState(update: (Content) -> Content) {
        stateUpdateMutex.withLock {
            val currentState = (stateDelegate.state.value as? Content) ?: return
            stateDelegate.updateState { update(currentState) }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val assetId: Long,
            val assetIconDrawable: AssetIconDrawable,
            val assetName: String,
            val verificationTierConfiguration: VerificationTierConfiguration,
            val isFavorite: Boolean?,
            val isNotificationsEnabled: Boolean?
        ) : ViewState
    }

    sealed interface ViewEvent {
        data object DisplayFailedToToggleFavoriteError : ViewEvent
        data object DisplayFailedToTogglePriceAlertError : ViewEvent
    }
}
