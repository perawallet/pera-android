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

package com.algorand.android.modules.onboarding.pairledger.accountselection.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.baseledgeraccountselection.accountselection.ui.BaseLedgerAccountSelectionViewModel
import com.algorand.android.modules.onboarding.pairledger.accountselection.ui.model.RegisterLedgerAccountSelectionPreview
import com.algorand.android.modules.onboarding.pairledger.accountselection.ui.usecase.RegisterLedgerAccountSelectionPreviewUseCase
import com.algorand.android.modules.rekey.model.AccountSelectionListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterBaseLedgerAccountSelectionViewModel @Inject constructor(
    private val registerLedgerAccountSelectionPreviewUseCase: RegisterLedgerAccountSelectionPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseLedgerAccountSelectionViewModel() {

    private val args = RegisterLedgerAccountSelectionFragmentArgs
        .fromSavedStateHandle(savedStateHandle)
        .registerLedgerAccountSelectionNavArgs

    val ledgerBluetoothName: String?
        get() = args.bluetoothName

    private val _registerLedgerAccountSelectionPreviewFlow = MutableStateFlow<RegisterLedgerAccountSelectionPreview?>(
        null
    )
    val registerLedgerAccountSelectionPreviewFlow: StateFlow<RegisterLedgerAccountSelectionPreview?>
        get() = _registerLedgerAccountSelectionPreviewFlow

    override val accountSelectionList: List<AccountSelectionListItem>
        get() = registerLedgerAccountSelectionPreviewFlow.value?.accountSelectionListItems.orEmpty()

    init {
        getAccountSelectionListItems()
    }

    private fun getAccountSelectionListItems() {
        viewModelScope.launch(Dispatchers.IO) {
            registerLedgerAccountSelectionPreviewUseCase.getRegisterLedgerAccountSelectionPreview(
                ledgerAccountsInformation = args.ledgerAccounts,
                bluetoothAddress = args.bluetoothAddress,
                bluetoothName = args.bluetoothName
            ).collectLatest { preview ->
                _registerLedgerAccountSelectionPreviewFlow.emit(preview)
            }
        }
    }

    override fun onNewAccountSelected(accountItem: AccountSelectionListItem.AccountItem) {
        val preview = _registerLedgerAccountSelectionPreviewFlow.value ?: return
        val updatedPreview = registerLedgerAccountSelectionPreviewUseCase.getUpdatedPreviewAccordingToAccountSelection(
            previousPreview = preview,
            accountItem = accountItem
        )
        _registerLedgerAccountSelectionPreviewFlow.update { updatedPreview }
    }
}
