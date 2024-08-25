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

package com.algorand.android.modules.rekey.previouslyrekeyedaccountconfirmation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.core.BaseViewModel
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class RekeyedAccountRekeyConfirmationViewModel @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val navArgs = RekeyedAccountRekeyConfirmationBottomSheetArgs.fromSavedStateHandle(savedStateHandle)
    private val accountAddress: String = navArgs.accountAddress
    private val authAccountAddress: String = navArgs.authAccountAddress

    private val _previewFlow = MutableStateFlow<RekeyedAccountRekeyConfirmationPreview?>(null)
    val previewFlow
        get() = _previewFlow.asStateFlow()

    init {
        initPreview()
    }

    private fun initPreview() {
        viewModelScope.launchIO {
            val accountDisplayName = getAccountDisplayName(accountAddress)
            val authAccountDisplayName = getAccountDisplayName(authAccountAddress)
            _previewFlow.value = RekeyedAccountRekeyConfirmationPreview(accountDisplayName, authAccountDisplayName)
        }
    }
}
