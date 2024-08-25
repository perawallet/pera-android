/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.send.transferamount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class BalanceWarningViewModel @Inject constructor(
    private val balanceWarningPreviewUseCase: BalanceWarningPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val accountAddress = savedStateHandle.getOrThrow<String>(ACCOUNT_ADDRESS_KEY)

    val balanceWarningPreviewFlow: StateFlow<BalanceWarningPreview?>
        get() = _balanceWarningPreviewFlow
    private val _balanceWarningPreviewFlow = MutableStateFlow<BalanceWarningPreview?>(null)

    init {
        setInitialPreview()
    }

    private fun setInitialPreview() {
        viewModelScope.launchIO {
            _balanceWarningPreviewFlow.value = balanceWarningPreviewUseCase.getInitialPreview(accountAddress)
        }
    }

    companion object {
        private const val ACCOUNT_ADDRESS_KEY = "accountAddress"
    }
}
