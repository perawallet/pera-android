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

@file:Suppress("LongParameterList")

package com.algorand.android.ui.swap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.modules.swap.introduction.domain.usecase.IsSwapFeatureIntroductionPageShownUseCase
import com.algorand.android.modules.swap.introduction.domain.usecase.SetSwapFeatureIntroductionPageVisibilityUseCase
import com.algorand.android.ui.swap.configuration.model.SwapConfigurationResult
import com.algorand.android.ui.swap.tracking.SwapScreenEventTracker
import com.algorand.android.ui.swap.usecase.GetPreselectedSwapAddress
import com.algorand.android.ui.swap.view.SwapFragmentArgs
import com.algorand.android.ui.swap.viewmodel.SwapViewModel.ViewState
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.isEqualTo
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHolding
import com.algorand.wallet.account.info.domain.usecase.IsAssetOptedInByAccount
import com.algorand.wallet.asset.domain.usecase.GetAssetDetail
import com.algorand.wallet.asset.domain.usecase.GetUsdcAssetId
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.asset.domain.util.AssetConstants.USDC_MAINNET_ID
import com.algorand.wallet.swap.domain.usecase.GetSwapUseLocalCurrencyPreference
import com.algorand.wallet.swap.domain.usecase.SetSwapUseLocalCurrencyPreference
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SwapViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getPreselectedSwapAddress: GetPreselectedSwapAddress,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getUsdcAssetId: GetUsdcAssetId,
    private val getAccountAssetHolding: GetAccountAssetHolding,
    private val setSwapFeatureIntroductionPageVisibility: SetSwapFeatureIntroductionPageVisibilityUseCase,
    private val isSwapFeatureIntroductionPageShown: IsSwapFeatureIntroductionPageShownUseCase,
    private val getAccountLite: GetAccountLite,
    private val isAssetOptedInByAccount: IsAssetOptedInByAccount,
    private val getSwapUseLocalCurrencyPreference: GetSwapUseLocalCurrencyPreference,
    private val setSwapUseLocalCurrencyPreference: SetSwapUseLocalCurrencyPreference,
    private val parityUseCase: ParityUseCase,
    private val swapScreenEventTracker: SwapScreenEventTracker,
    private val getAssetDetail: GetAssetDetail
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, SwapScreenEventTracker by swapScreenEventTracker {

    private val _swapDetailsFlow = MutableStateFlow<SwapDetails>(SwapDetails())
    val swapDetailsFlow: StateFlow<SwapDetails>
        get() = _swapDetailsFlow.asStateFlow()

    val addressFlow: Flow<String?>
        get() = _swapDetailsFlow.map { it.address }.distinctUntilChanged()

    val assetInFlow: Flow<Long>
        get() = _swapDetailsFlow.map { it.assetInId }.distinctUntilChanged()

    val assetOutFlow: Flow<Long>
        get() = _swapDetailsFlow.map { it.assetOutId }.distinctUntilChanged()

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initViewState(arg: SwapFragmentArgs) {
        stateDelegate.onState<ViewState.Idle> {
            viewModelScope.launch {
                if (isSwapFeatureIntroductionPageShown()) {
                    initSwapViewState(arg)
                } else {
                    stateDelegate.updateState { ViewState.Introduction }
                }
            }
        }
    }

    fun switchAssets() {
        val currentDetails = _swapDetailsFlow.value
        _swapDetailsFlow.value = currentDetails.copy(
            assetInId = currentDetails.assetOutId,
            assetOutId = currentDetails.assetInId
        )
    }

    fun getSwapDetails(): SwapDetails = swapDetailsFlow.value

    fun getAddress(): String? = swapDetailsFlow.value.address

    fun getAssetInId(): Long = swapDetailsFlow.value.assetInId

    fun setAssetInId(assetId: Long) {
        viewModelScope.launch {
            getAssetDetail(assetId)?.shortName?.let { assetUnitName ->
                swapScreenEventTracker.logAssetInSelection(assetUnitName)
            }
        }
        _swapDetailsFlow.value = _swapDetailsFlow.value.copy(assetInId = assetId)
    }

    fun setAssetOutId(assetId: Long) {
        viewModelScope.launch {
            getAssetDetail(assetId)?.shortName?.let { assetUnitName ->
                swapScreenEventTracker.logAssetOutSelection(assetUnitName)
            }
        }
        _swapDetailsFlow.value = _swapDetailsFlow.value.copy(assetOutId = assetId)
    }

    fun setAssetInAndOutIds(assetInId: Long, assetOutId: Long) {
        _swapDetailsFlow.value = _swapDetailsFlow.value.copy(assetOutId = assetOutId, assetInId = assetInId)
    }

    fun applySwapConfigs(result: SwapConfigurationResult) {
        _swapDetailsFlow.update { currentConfig ->
            if (currentConfig.useLocalCurrency != result.useLocalCurrency) {
                viewModelScope.launch {
                    setSwapUseLocalCurrencyPreference(result.useLocalCurrency)
                }
            }
            currentConfig.copy(
                slippage = result.slippageTolerance,
                useLocalCurrency = result.useLocalCurrency
            )
        }
    }

    fun setAddress(address: String) {
        viewModelScope.launch {
            val assetHolding = getAccountAssetHolding(address, _swapDetailsFlow.value.assetInId)
            _swapDetailsFlow.value = if (assetHolding == null || assetHolding.amount isEqualTo BigInteger.ZERO) {
                _swapDetailsFlow.value.copy(address = address, assetInId = ALGO_ID, assetOutId = getUsdcAssetId())
            } else {
                _swapDetailsFlow.value.copy(address = address)
            }
            updateContentState(address)
        }
    }

    fun acceptTermsOfService(arg: SwapFragmentArgs) {
        viewModelScope.launch {
            setSwapFeatureIntroductionPageVisibility(false)
            initSwapViewState(arg)
        }
    }

    private suspend fun initSwapViewState(arg: SwapFragmentArgs) {
        val address = getSwapAddress(arg)
        if (address == null) {
            stateDelegate.updateState { ViewState.NoAccountState }
        } else {
            val assetInId = getAssetInId(address, arg.assetInId)
            val useLocalCurrency = getSwapUseLocalCurrencyPreference()
            _swapDetailsFlow.value = SwapDetails(
                address = address,
                assetInId = assetInId,
                assetOutId = getAssetOutId(assetInId, arg.assetOutId),
                useLocalCurrency = useLocalCurrency,
                primaryCurrencySymbol = getPrimaryCurrencySymbol(useLocalCurrency)
            )
            updateContentState(address)
        }
    }

    private fun getPrimaryCurrencySymbol(useLocalCurrency: Boolean): String {
        return if (useLocalCurrency) parityUseCase.getDisplayedCurrencySymbol() else emptyString()
    }

    private suspend fun getSwapAddress(arg: SwapFragmentArgs): String? {
        val deeplinkAddress = arg.address
        if (!deeplinkAddress.isNullOrBlank()) {
            val canSignTxn = getAccountLite(deeplinkAddress)?.cachedInfo?.type?.canSignTransaction() == true
            if (canSignTxn) return deeplinkAddress
        }
        return getPreselectedSwapAddress()
    }

    private suspend fun getAssetInId(address: String, assetInIdArg: Long): Long {
        return if (assetInIdArg != -1L && isAssetOptedInByAccount(address, assetInIdArg)) {
            assetInIdArg
        } else {
            ALGO_ID
        }
    }

    private suspend fun getAssetOutId(assetInId: Long, assetOutIdArg: Long): Long {
        return if (assetOutIdArg != -1L && assetInId != assetOutIdArg) assetOutIdArg else getUsdcAssetId()
    }

    private suspend fun updateContentState(address: String) {
        val accountIcon = getAccountIconDrawablePreview(address)
        val accountDisplayName = getAccountDisplayName(address)
        stateDelegate.updateState {
            ViewState.Content(accountIcon, accountDisplayName, swapDetailsFlow.value)
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object NoAccountState : ViewState
        data object Introduction : ViewState
        data class Content(
            val accountIconDrawable: AccountIconDrawablePreview,
            val accountDisplayName: AccountDisplayName,
            val swapDetails: SwapDetails
        ) : ViewState
    }

    data class SwapDetails(
        val address: String? = null,
        val assetInId: Long = ALGO_ID,
        val assetOutId: Long = USDC_MAINNET_ID,
        val slippage: Float? = null,
        val useLocalCurrency: Boolean = false,
        val primaryCurrencySymbol: String = ""
    )
}
