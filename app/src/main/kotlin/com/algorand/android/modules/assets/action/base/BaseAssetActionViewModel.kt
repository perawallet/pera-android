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

package com.algorand.android.modules.assets.action.base

import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.BaseAccountAddress
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.usecase.AccountAddressUseCase
import com.algorand.android.utils.Resource
import com.algorand.android.utils.exception.AssetNotFoundException
import com.algorand.android.utils.launchIO
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseAssetActionViewModel(
    private val accountAddressUseCase: AccountAddressUseCase,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider,
    private val fetchAndCacheAssets: FetchAndCacheAssets,
    private val getAsset: GetAsset
) : BaseViewModel(), EventViewModel<BaseAssetActionViewModel.ViewEvent> by eventDelegate {

    abstract val assetId: Long

    private val _assetFlow = MutableStateFlow<Resource<Asset>?>(null)
    val assetFlow: StateFlow<Resource<Asset>?> = _assetFlow.asStateFlow()

    // TODO: Move this into UseCase
    protected fun fetchAssetDescription(assetId: Long) {
        _assetFlow.value = Resource.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val cachedAsset = getAsset(assetId)
            if (cachedAsset != null) {
                _assetFlow.value = Resource.Success(cachedAsset)
            } else {
                _assetFlow.value = fetchAndCacheAssets(listOf(assetId), includeDeleted = true).use(
                    onSuccess = {
                        Resource.Success(getAsset(assetId))
                    },
                    onFailed = { exception, _ ->
                        val errorResourceId = if (exception is AssetNotFoundException) {
                            R.string.asset_not_found_please_make
                        } else {
                            R.string.an_error_occured
                        }
                        Resource.Error.Annotated(AnnotatedString(errorResourceId))
                    }
                )
            }
        }
    }

    fun getVerificationTierConfiguration(verificationTier: VerificationTier?): VerificationTierConfiguration {
        return verificationTierConfigurationDecider.decideVerificationTierConfiguration(verificationTier)
    }

    fun getAccountName(accountAddress: String) {
        viewModelScope.launchIO {
            eventDelegate.sendEvent(
                ViewEvent.SetAccountName(accountAddressUseCase.getAccountAddress(accountAddress))
            )
        }
    }

    protected companion object {
        const val ASSET_ACTION_KEY = "assetAction"
        const val SHOULD_WAIT_FOR_CONFIRMATION_KEY = "shouldWaitForConfirmation"
        const val DEFAULT_WAIT_FOR_CONFIRMATION_PARAM = false
    }

    interface ViewEvent {
        data class SetAccountName(val accountAddress: BaseAccountAddress.AccountAddress) : ViewEvent
    }
}
