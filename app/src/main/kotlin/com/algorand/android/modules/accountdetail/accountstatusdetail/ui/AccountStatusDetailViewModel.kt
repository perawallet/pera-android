/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountdetail.accountstatusdetail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.ui.AccountAssetItemButtonState
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountOriginalStateIconDrawablePreview
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.AccountStatusDetailViewModel.ViewEvent
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.AccountStatusDetailViewModel.ViewState
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.decider.AccountStatusDetailPreviewDecider
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.wallet.account.core.domain.usecase.GetAccountDetailFlow
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class AccountStatusDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getAccountDetailFlow: GetAccountDetailFlow,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountOriginalStateIconDrawablePreview: GetAccountOriginalStateIconDrawablePreview,
    private val accountStatusDetailPreviewDecider: AccountStatusDetailPreviewDecider,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private val navArgs = AccountStatusDetailBottomSheetArgs.fromSavedStateHandle(savedStateHandle)
    val accountAddress = navArgs.accountAddress

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun loadAccountStatusDetail() {
        stateDelegate.updateState { ViewState.Loading }
        viewModelScope.launch {
            getAccountDetailFlow(accountAddress).collectLatest { accountDetail ->
                if (accountDetail == null) return@collectLatest

                val authAccountAddress = getAccountRekeyAdminAddress(accountAddress)
                val hasAccountAuthority = accountDetail.accountType?.canSignTransaction() == true

                val titleString = accountStatusDetailPreviewDecider.decideTitleString(accountDetail.accountType)
                val accountOriginalTypeDisplayName = getAccountDisplayName(accountAddress)
                val accountOriginalTypeIconDrawablePreview = getAccountOriginalStateIconDrawablePreview(accountAddress)
                val accountTypeDrawablePreview = getAccountIconDrawablePreview(accountAddress)
                val accountTypeString = accountStatusDetailPreviewDecider.decideAccountTypeString(accountDetail)
                val descriptionDetail = accountStatusDetailPreviewDecider.decideDescriptionDetail(
                    accountDetail = accountDetail
                )
                val authAccountDisplayName = authAccountAddress?.let { safeAuthAddress ->
                    getAccountDisplayName(safeAuthAddress)
                }
                val authAccountIconDrawablePreview = authAccountAddress?.let { safeAuthAddress ->
                    getAccountIconDrawablePreview(safeAuthAddress)
                }
                val authAccountActionButton = accountStatusDetailPreviewDecider.decideAuthAccountActionButtonState(
                    accountType = accountDetail.accountType
                )

                stateDelegate.updateState {
                    ViewState.Content(
                        titleString = titleString,
                        accountOriginalTypeDisplayName = accountOriginalTypeDisplayName,
                        accountOriginalTypeIconDrawablePreview = accountOriginalTypeIconDrawablePreview,
                        accountOriginalActionButton = AccountAssetItemButtonState.COPY,
                        authAccountDisplayName = authAccountDisplayName,
                        authAccountIconDrawablePreview = authAccountIconDrawablePreview,
                        authAccountActionButton = authAccountActionButton,
                        accountTypeDrawablePreview = accountTypeDrawablePreview,
                        descriptionDetail = descriptionDetail,
                        accountTypeString = accountTypeString,
                        isRekeyGroupVisible = authAccountAddress != null,
                        isRekeyToLedgerAccountVisible = hasAccountAuthority,
                        isRekeyToStandardAccountVisible = hasAccountAuthority
                    )
                }
            }
        }
    }

    fun onAuthAccountActionButtonClicked() {
        eventDelegate.sendEvent(viewModelScope, ViewEvent.NavigateToUndoRekey)
    }

    fun onAccountActionButtonClicked() {
        eventDelegate.sendEvent(viewModelScope, ViewEvent.CopyAccountAddressToClipboard(accountAddress))
    }

    fun onRekeyToStandardAccountClicked() {
        eventDelegate.sendEvent(viewModelScope, ViewEvent.NavigateToRekeyToStandardAccount)
    }

    fun onRekeyToLedgerAccountClicked() {
        eventDelegate.sendEvent(viewModelScope, ViewEvent.NavigateToRekeyToLedgerAccount)
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState

        data class Content(
            val titleString: String? = null,
            val accountOriginalTypeDisplayName: AccountDisplayName? = null,
            val accountOriginalTypeIconDrawablePreview: AccountIconDrawablePreview? = null,
            val accountOriginalActionButton: AccountAssetItemButtonState? = null,
            val authAccountDisplayName: AccountDisplayName? = null,
            val authAccountIconDrawablePreview: AccountIconDrawablePreview? = null,
            val authAccountActionButton: AccountAssetItemButtonState? = null,
            val accountTypeDrawablePreview: AccountIconDrawablePreview? = null,
            val descriptionDetail: DescriptionDetail,
            val accountTypeString: String? = null,
            val isRekeyGroupVisible: Boolean? = null,
            val isRekeyToLedgerAccountVisible: Boolean? = null,
            val isRekeyToStandardAccountVisible: Boolean? = null
        ) : ViewState {
            data class DescriptionDetail(
                val annotatedString: AnnotatedString,
                val hyperlinkUrl: String
            )
        }

        data class Error(val message: String) : ViewState
    }

    sealed interface ViewEvent {
        data class CopyAccountAddressToClipboard(val address: String) : ViewEvent
        data object NavigateToUndoRekey : ViewEvent
        data object NavigateToRekeyToStandardAccount : ViewEvent
        data object NavigateToRekeyToLedgerAccount : ViewEvent
    }
}
