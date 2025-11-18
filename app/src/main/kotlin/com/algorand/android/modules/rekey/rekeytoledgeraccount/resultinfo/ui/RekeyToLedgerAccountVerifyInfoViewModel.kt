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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.resultinfo.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.rekey.rekeytoledgeraccount.resultinfo.ui.RekeyToLedgerAccountVerifyInfoViewModel.ViewState
import com.algorand.android.utils.launchIO
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RekeyToLedgerAccountVerifyInfoViewModel @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val stateDelegate: StateDelegate<ViewState>,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate {

    private val navArgs = RekeyToLedgerAccountVerifyInfoFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val accountAddress = navArgs.accountAddress

    init {
        initViewState()
    }

    private fun initViewState() {
        stateDelegate.setDefaultState(ViewState(accountDisplayName = ""))
        viewModelScope.launchIO {
            val displayName = getAccountDisplayName(accountAddress).primaryDisplayName
            stateDelegate.updateState { ViewState(displayName) }
        }
    }

    data class ViewState(val accountDisplayName: String)
}
