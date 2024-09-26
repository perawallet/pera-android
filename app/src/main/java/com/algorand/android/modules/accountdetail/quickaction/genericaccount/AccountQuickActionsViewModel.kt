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

package com.algorand.android.modules.accountdetail.quickaction.genericaccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.modules.accountdetail.quickaction.genericaccount.ui.model.AccountQuickActionsPreview
import com.algorand.android.modules.accountdetail.quickaction.genericaccount.ui.usecase.AccountQuickActionsPreviewUseCase
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class AccountQuickActionsViewModel @Inject constructor(
    private val accountQuickActionsPreviewUseCase: AccountQuickActionsPreviewUseCase,
    private val getAccountDetail: GetAccountDetail,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val accountAddress: String = savedStateHandle.getOrThrow(ACCOUNT_ADDRESS_KEY)

    private var accountDetail: AccountDetail? = null

    private val canAccountSignTransaction: Boolean
        get() = accountDetail?.canSignTransaction() ?: false

    private val _accountQuickActionsPreviewFlow = MutableStateFlow(
        accountQuickActionsPreviewUseCase.getInitialPreview()
    )
    val accountQuickActionsPreviewFlow: StateFlow<AccountQuickActionsPreview>
        get() = _accountQuickActionsPreviewFlow

    init {
        initAccountDetail()
    }

    private fun initAccountDetail() {
        viewModelScope.launchIO {
            accountDetail = getAccountDetail(accountAddress)
        }
    }

    fun onSwapClick() {
        viewModelScope.launchIO {
            _accountQuickActionsPreviewFlow.update { preview ->
                accountQuickActionsPreviewUseCase.updatePreviewWithSwapNavigation(
                    accountAddress = accountAddress,
                    preview = preview
                )
            }
        }
    }

    fun onAddAssetClick() {
        _accountQuickActionsPreviewFlow.update { preview ->
            accountQuickActionsPreviewUseCase.updatePreviewWithAssetAdditionNavigation(
                preview = preview,
                canAccountSignTransaction = canAccountSignTransaction,
                accountAddress = accountAddress
            )
        }
    }

    fun onBuySellClick() {
        _accountQuickActionsPreviewFlow.update { preview ->
            accountQuickActionsPreviewUseCase.updatePreviewWithOfframpNavigation(
                preview = preview,
                canAccountSignTransaction = canAccountSignTransaction,
                accountAddress = accountAddress
            )
        }
    }

    fun onSendClick() {
        _accountQuickActionsPreviewFlow.update { preview ->
            accountQuickActionsPreviewUseCase.updatePreviewWithSendNavigation(
                preview = preview,
                canAccountSignTransaction = canAccountSignTransaction,
                accountAddress = accountAddress
            )
        }
    }

    companion object {
        private const val ACCOUNT_ADDRESS_KEY = "accountAddress"
    }
}
