/*
 * Copyright 2022-2025 Pera Wallet, LDA
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

package com.algorand.android.modules.accountdetail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountDetailTab
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDetailSummary
import com.algorand.android.modules.accountdetail.ui.model.AccountDetailPreview
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.modules.swap.common.domain.usecase.GetSwapNavigationDestination
import com.algorand.android.modules.tracking.accountdetail.AccountDetailFragmentEventTracker
import com.algorand.android.usecase.AccountDeletionUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val accountDeletionUseCase: AccountDeletionUseCase,
    savedStateHandle: SavedStateHandle,
    private val accountDetailFragmentEventTracker: AccountDetailFragmentEventTracker,
    private val getSwapNavigationDestination: GetSwapNavigationDestination,
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val getAccountDetailSummary: GetAccountDetailSummary
) : BaseViewModel() {

    val accountAddress: String = savedStateHandle.getOrThrow(PUBLIC_KEY)
    private val accountDetailTab = savedStateHandle.get<AccountDetailTab?>(ACCOUNT_DETAIL_TAB)

    private val _accountDetailTabArgFlow = MutableStateFlow<Event<Int>?>(null)
    val accountDetailTabArgFlow: StateFlow<Event<Int>?> get() = _accountDetailTabArgFlow

    private val _accountDetailPreviewFlow = MutableStateFlow<AccountDetailPreview?>(null)
    val accountDetailPreviewFlow: StateFlow<AccountDetailPreview?>
        get() = _accountDetailPreviewFlow

    val canAccountSignTransaction: Boolean
        get() = _accountDetailPreviewFlow.value?.accountDetailSummary?.accountType?.canSignTransaction() ?: false

    val accountType: AccountType?
        get() = _accountDetailPreviewFlow.value?.accountDetailSummary?.accountType

    init {
        initAccountDetailPreview()
        checkAccountDetailTabArg()
    }

    private fun checkAccountDetailTabArg() {
        viewModelScope.launch {
            accountDetailTab?.tabIndex?.run {
                _accountDetailTabArgFlow.emit(Event(this))
            }
        }
    }

    fun removeAccount(publicKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            accountDeletionUseCase.removeAccount(publicKey)
            _accountDetailPreviewFlow.update {
                it?.copy(
                    navBackEvent = Event(Unit),
                )
            }
        }
    }

    fun initAccountDetailPreview() {
        viewModelScope.launchIO {
            getAccountLiteCacheFlow().collectLatest { cacheStatus ->
                val accountLite = (cacheStatus as? AccountLiteCacheStatus.Data)?.accountLites?.get(accountAddress)
                if (accountLite != null) {
                    _accountDetailPreviewFlow.update {
                        AccountDetailPreview(getAccountDetailSummary(accountLite), null)
                    }
                }
            }
        }
    }

    fun logAccountDetailAssetsTapEventTracker() {
        viewModelScope.launch {
            accountDetailFragmentEventTracker.logAccountDetailAssetsTapEvent()
        }
    }

    fun logAccountDetailCollectiblesTapEventTracker() {
        viewModelScope.launch {
            accountDetailFragmentEventTracker.logAccountDetailCollectiblesTapEvent()
        }
    }

    fun logAccountDetailTransactionHistoryTapEventTracker() {
        viewModelScope.launch {
            accountDetailFragmentEventTracker.logAccountDetailTransactionHistoryTapEvent()
        }
    }

    fun onSwapClick() {
        viewModelScope.launchIO {
            accountDetailFragmentEventTracker.logAccountDetailSwapButtonClickEvent()
            _accountDetailPreviewFlow.update {
                it?.copy(
                    swapNavigationDestinationEvent = Event(getSwapNavigationDestination(accountAddress))
                )
            }
        }
    }

    companion object {
        private const val PUBLIC_KEY = "publicKey"
        private const val ACCOUNT_ADDRESS = "accountAddress"
        private const val ACCOUNT_DETAIL_TAB = "accountDetailTab"
    }
}
