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

package com.algorand.android.ui.swap.confirmation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable
import com.algorand.android.ui.swap.confirmation.mapper.SwapConfirmationContentMapper
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Idle
import com.algorand.wallet.swap.domain.model.SwapQuote
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SwapConfirmationViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val contentMapper: SwapConfirmationContentMapper
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(Idle)
    }

    fun init(quote: SwapQuote) {
        stateDelegate.onState<Idle> {
            viewModelScope.launch {
                val contentState = contentMapper.map(quote)
                stateDelegate.updateState { contentState }
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val quote: SwapQuote,
            val accountDisplayName: AccountDisplayName,
            val accountIconDrawable: AccountIconDrawablePreview,
            val priceImpact: SwapPriceImpact,
            val assetInDetail: AssetDetail,
            val assetOutDetail: AssetDetail,
            val exchangeFee: AmountRenderer,
            val peraFee: AmountRenderer,
            val minReceivedAssetAmount: AmountRenderer,
            val assetInToOutPriceRatio: PriceRatio,
            val assetOutToInPriceRatio: PriceRatio,
            val buttonStatus: ButtonStatus
        ) : ViewState {

            class AssetDetail(
                val amount: AmountRenderer,
                val approximateValue: AmountRenderer,
                val shortName: String?,
                val assetIconDrawable: AssetIconDrawable,
                val verificationTier: VerificationTierConfiguration
            )

            data class PriceRatio(
                val ratio: AmountRenderer,
                val firstAssetUnitName: String,
                val secondAssetUnitName: String
            )

            sealed interface ButtonStatus {
                data object Enabled : ButtonStatus
                data object Disabled : ButtonStatus
                data object PriceImpactConfirmationRequired : ButtonStatus
            }
        }
    }
}
