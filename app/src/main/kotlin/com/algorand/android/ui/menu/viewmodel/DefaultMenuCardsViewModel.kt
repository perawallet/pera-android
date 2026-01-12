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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.algorand.android.ui.menu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState
import com.algorand.wallet.cards.domain.model.CardNftRewardState.IS_PROCESSING
import com.algorand.wallet.cards.domain.model.CardNftRewardState.PROCESSED
import com.algorand.wallet.cards.domain.model.FundAddress
import com.algorand.wallet.cards.domain.usecase.GetCardFundAddresses
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DefaultMenuCardsViewModel @Inject constructor(
    private val getCardFundAddresses: GetCardFundAddresses,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), MenuCardsViewModel {

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    override fun initCardState() {
        stateDelegate.onState<ViewState.Idle> {
            viewModelScope.launch {
                val viewState = getCardFundAddresses().use(
                    onSuccess = ::getSuccessViewState,
                    onFailed = { _, _ -> ViewState.Error }
                )
                stateDelegate.updateState { viewState }
            }
        }
    }

    private fun getSuccessViewState(fundAddresses: List<FundAddress>): ViewState {
        val isThereAnyFundAddress = fundAddresses.any { !it.fundAddress.isNullOrBlank() }
        val isCardCreated = fundAddresses.any { it.nftRewardState == IS_PROCESSING || it.nftRewardState == PROCESSED }
        return if (isThereAnyFundAddress && isCardCreated) ViewState.CardCreated else ViewState.NewUser
    }
}
