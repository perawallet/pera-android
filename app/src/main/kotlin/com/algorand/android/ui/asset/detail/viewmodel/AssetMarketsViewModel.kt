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
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail
import com.algorand.android.ui.asset.detail.usecase.GetAssetMarketsDetail
import com.algorand.android.ui.asset.detail.viewmodel.AssetMarketsViewModel.ViewState
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.domain.GetPrimaryFiatAmountRenderer
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.VerificationTier.SUSPICIOUS
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AssetMarketsViewModel @Inject constructor(
    private val getAssetMarketsDetail: GetAssetMarketsDetail,
    private val stateDelegate: StateDelegate<ViewState>,
    private val getPrimaryFiatAmountRenderer: GetPrimaryFiatAmountRenderer
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initViewState(asset: Asset) {
        stateDelegate.onState<ViewState.Idle> {
            val marketsDetail = getAssetMarketsDetail(asset)
            val isAvailableOnDiscover = asset.assetInfo?.isAvailableOnDiscoverMobile == true &&
                    asset.verificationTier != SUSPICIOUS && asset.hasUsdValue()
            val amountRenderer = getPrimaryFiatAmountRenderer(asset.usdValue ?: BigDecimal.ZERO, BigDecimal.ONE, Plain)
            val content = ViewState.Content(amountRenderer, isAvailableOnDiscover, marketsDetail)
            stateDelegate.updateState { content }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val assetPriceRenderer: AmountRenderer,
            val isAvailableOnDiscover: Boolean,
            val details: List<AssetMarketsDetail>
        ) : ViewState
    }
}
