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

package com.algorand.android.modules.dapp.meld.ui.intro

import androidx.lifecycle.viewModelScope
import com.algorand.android.BuildConfig.MELD_MAINNET_URL
import com.algorand.android.BuildConfig.MELD_TESTNET_URL
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.tracking.meld.MeldAlgoBuyTapEventTracker
import com.algorand.android.network.AlgodInterceptor
import com.algorand.android.usecase.GetIsActiveNodeTestnetUseCase
import com.algorand.android.utils.MAINNET_NETWORK_SLUG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeldIntroViewModel @Inject constructor(
    private val getIsActiveNodeTestnetUseCase: GetIsActiveNodeTestnetUseCase,
    private val algodInterceptor: AlgodInterceptor,
    private val meldAlgoBuyTapEventTracker: MeldAlgoBuyTapEventTracker
) : BaseViewModel() {

    fun getMeldUrl(walletAddress: String): String {
        val meldUrl = if (isConnectedToTestnet())
            MELD_TESTNET_URL
        else
            MELD_MAINNET_URL
        return meldUrl + walletAddress
    }

    fun isMainNet(): Boolean {
        return algodInterceptor.currentActiveNode?.networkSlug == MAINNET_NETWORK_SLUG
    }

    fun logBuyAlgoTapEvent() {
        viewModelScope.launch {
            meldAlgoBuyTapEventTracker.logMeldAlgoBuyTapEvent()
        }
    }

    fun isConnectedToTestnet(): Boolean {
        return getIsActiveNodeTestnetUseCase.invoke()
    }
}
