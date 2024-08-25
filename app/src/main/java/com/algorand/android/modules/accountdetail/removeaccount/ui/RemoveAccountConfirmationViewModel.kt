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

package com.algorand.android.modules.accountdetail.removeaccount.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.accountdetail.removeaccount.ui.model.RemoveAccountConfirmationPreview
import com.algorand.android.modules.accountdetail.removeaccount.ui.usecase.RemoveAccountConfirmationPreviewUseCase
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RemoveAccountConfirmationViewModel @Inject constructor(
    private val removeAccountConfirmationPreviewUseCase: RemoveAccountConfirmationPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val navArgs = RemoveAccountConfirmationBottomSheetArgs.fromSavedStateHandle(savedStateHandle)
    private val accountAddress = navArgs.accountAddress

    private val _removeAccountConfirmationPreviewFlow = MutableStateFlow<RemoveAccountConfirmationPreview?>(null)
    val removeAccountConfirmationPreviewFlow: StateFlow<RemoveAccountConfirmationPreview?>
        get() = _removeAccountConfirmationPreviewFlow

    fun onRemoveAccountClick() {
        viewModelScope.launch {
            _removeAccountConfirmationPreviewFlow.update { preview ->
                removeAccountConfirmationPreviewUseCase.updatePreviewWithRemoveAccountConfirmation(
                    preview,
                    accountAddress
                )
            }
        }
    }

    init {
        initPreview()
    }

    private fun initPreview() {
        viewModelScope.launchIO {
            _removeAccountConfirmationPreviewFlow.update {
                removeAccountConfirmationPreviewUseCase.getRemoveAccountConfirmationPreview(accountAddress)
            }
        }
    }
}
