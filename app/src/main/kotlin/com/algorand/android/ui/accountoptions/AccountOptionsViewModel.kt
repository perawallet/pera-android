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

package com.algorand.android.ui.accountoptions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.database.NotificationFilterDao
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.repository.NotificationRepository
import com.algorand.android.ui.accountoptions.AccountOptionsViewModel.ViewEvent
import com.algorand.android.ui.accountoptions.AccountOptionsViewModel.ViewEvent.NavToNoRekeyedAccounts
import com.algorand.android.ui.accountoptions.AccountOptionsViewModel.ViewEvent.NavToRekeyedAccountSelection
import com.algorand.android.ui.accountoptions.model.AccountOptionsPreview
import com.algorand.android.usecase.AccountDeletionUseCase
import com.algorand.android.usecase.SecurityUseCase
import com.algorand.android.utils.Resource
import com.algorand.wallet.account.info.domain.usecase.FetchRekeyedAccounts
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AccountOptionsViewModel @Inject constructor(
    private val notificationFilterDao: NotificationFilterDao,
    private val notificationRepository: NotificationRepository,
    private val accountDeletionUseCase: AccountDeletionUseCase,
    private val securityUseCase: SecurityUseCase,
    private val accountOptionsPreviewUseCase: AccountOptionsPreviewUseCase,
    private val fetchRekeyedAccounts: FetchRekeyedAccounts,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses,
    savedStateHandle: SavedStateHandle
) : ViewModel(), EventViewModel<ViewEvent> by eventDelegate {

    val accountAddress = savedStateHandle.get<String>(ACCOUNT_ADDRESS).orEmpty()

    val notificationFilterOperationFlow = MutableStateFlow<Resource<Unit>?>(null)
    val notificationFilterCheckFlow = MutableStateFlow<Boolean?>(null)
    private val _accountOptionsPreviewFlow = MutableStateFlow<AccountOptionsPreview?>(null)
    val accountOptionsPreviewFlow: StateFlow<AccountOptionsPreview?>
        get() = _accountOptionsPreviewFlow
    private var rekeyedAccountFetchingJob: Job? = null

    init {
        checkIfNotificationFiltered()
        initAccountOptionsPreview()
    }

    private fun initAccountOptionsPreview() {
        viewModelScope.launch(Dispatchers.IO) {
            accountOptionsPreviewUseCase.getPreview(accountAddress)?.let {
                _accountOptionsPreviewFlow.value = it
            }
        }
    }

    private fun checkIfNotificationFiltered() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationFilterCheckFlow.value =
                notificationFilterDao.getNotificationFilterForUser(accountAddress).isNotEmpty()
        }
    }

    fun startFilterOperation(isFiltered: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationFilterOperationFlow.value = Resource.Loading
            notificationFilterOperationFlow.value = notificationRepository
                .addNotificationFilter(accountAddress, isFiltered)
        }
    }

    fun scanRekeyedAccounts() {
        _accountOptionsPreviewFlow.value?.let { preview ->
            eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowFetchingRekeyedAccountsDialog)
            rekeyedAccountFetchingJob = viewModelScope.launch {
                val viewEvent = getNotImportedRekeyedAddresses().use(
                    onSuccess = { rekeyedAddresses ->
                        if (rekeyedAddresses.isEmpty()) {
                            NavToNoRekeyedAccounts
                        } else {
                            NavToRekeyedAccountSelection(accountAddress, preview.accountIconDrawable, rekeyedAddresses)
                        }
                    },
                    onFailed = { _, _ ->
                        ViewEvent.ShowGenericError
                    }
                )
                eventDelegate.sendEvent(ViewEvent.HideFetchingRekeyedAccountsDialog)
                eventDelegate.sendEvent(viewEvent)
            }
        }
    }

    fun stopFetchingRekeyedAccounts() {
        rekeyedAccountFetchingJob?.cancel()
        rekeyedAccountFetchingJob = null
    }

    fun canDisplayPassphrases(): Boolean {
        return _accountOptionsPreviewFlow.value?.isPassphraseButtonVisible == true
    }

    fun getAccountName(): String {
        return _accountOptionsPreviewFlow.value?.accountDisplayName?.primaryDisplayName.orEmpty()
    }

    fun removeAccount(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            accountDeletionUseCase.removeAccount(address)
        }
    }

    fun isPinCodeEnabled(): Boolean {
        return securityUseCase.isPinCodeEnabled()
    }

    private suspend fun getNotImportedRekeyedAddresses(): PeraResult<List<String>> {
        return fetchRekeyedAccounts(accountAddress).map { rekeyedAccountInfos ->
            if (rekeyedAccountInfos.isEmpty()) return@map emptyList()
            val localAddresses = getLocalAccountsAddresses()
            rekeyedAccountInfos.mapNotNull { rekeyedAddress ->
                rekeyedAddress.address.takeIf { !localAddresses.contains(it) }
            }
        }
    }

    sealed interface ViewEvent {
        data object ShowFetchingRekeyedAccountsDialog : ViewEvent
        data object HideFetchingRekeyedAccountsDialog : ViewEvent
        data class NavToRekeyedAccountSelection(
            val authAddress: String,
            val authDrawable: AccountIconDrawablePreview,
            val rekeyedAddresses: List<String>
        ) : ViewEvent

        data object NavToNoRekeyedAccounts : ViewEvent
        data object ShowGenericError : ViewEvent
    }

    companion object {
        private const val ACCOUNT_ADDRESS = "accountAddress"
    }
}
