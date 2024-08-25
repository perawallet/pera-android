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

package com.algorand.android.modules.asb.importbackup.accountselection.ui

import androidx.lifecycle.*
import com.algorand.android.modules.asb.importbackup.accountselection.ui.model.AsbImportAccountSelectionPreview
import com.algorand.android.modules.asb.importbackup.accountselection.ui.usecase.AsbImportAccountSelectionPreviewUseCase
import com.algorand.android.modules.asb.importbackup.enterkey.ui.model.RestoredAccount
import com.algorand.android.modules.basemultipleaccountselection.ui.BaseMultipleAccountSelectionViewModel
import com.algorand.android.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AsbImportAccountSelectionViewModel @Inject constructor(
    private val asbImportAccountSelectionPreviewUseCase: AsbImportAccountSelectionPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseMultipleAccountSelectionViewModel() {

    private var accountImportJob: Job? = null

    private val restoredAccounts = savedStateHandle.getOrThrow<Array<RestoredAccount>>(
        argKey = RESTORED_ACCOUNTS_KEY
    )

    private val _asbImportAccountSelectionPreviewFlow = MutableStateFlow(getInitialPreview())
    override val multipleAccountSelectionPreviewFlow: StateFlow<AsbImportAccountSelectionPreview>
        get() = _asbImportAccountSelectionPreviewFlow

    init {
        setAsbImportAccountSelectionPreview()
    }

    fun onHeaderCheckBoxClick() {
        _asbImportAccountSelectionPreviewFlow.update { preview ->
            asbImportAccountSelectionPreviewUseCase.updatePreviewAfterHeaderCheckBoxClicked(preview)
        }
    }

    fun onAccountCheckBoxClick(accountAddress: String) {
        _asbImportAccountSelectionPreviewFlow.update { preview ->
            asbImportAccountSelectionPreviewUseCase.updatePreviewAfterAccountCheckBoxClicked(preview, accountAddress)
        }
    }

    fun onRestoreClick() {
        accountImportJob?.cancel()
        accountImportJob = viewModelScope.launchIO {
            asbImportAccountSelectionPreviewUseCase.updatePreviewWithRestoredAccounts(
                preview = _asbImportAccountSelectionPreviewFlow.value,
                restoredAccounts = restoredAccounts
            ).collectLatest { preview ->
                _asbImportAccountSelectionPreviewFlow.emit(preview)
            }
        }
    }

    private fun getInitialPreview(): AsbImportAccountSelectionPreview {
        return asbImportAccountSelectionPreviewUseCase.getInitialPreview()
    }

    private fun setAsbImportAccountSelectionPreview() {
        viewModelScope.launchIO {
            val preview = asbImportAccountSelectionPreviewUseCase.getAsbImportAccountSelectionPreview(
                preview = _asbImportAccountSelectionPreviewFlow.value,
                restoredAccounts = restoredAccounts
            )
            _asbImportAccountSelectionPreviewFlow.value = preview
        }
    }

    companion object {
        private const val RESTORED_ACCOUNTS_KEY = "restoredAccounts"
    }
}
