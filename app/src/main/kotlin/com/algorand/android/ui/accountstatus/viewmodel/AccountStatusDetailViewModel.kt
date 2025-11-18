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

package com.algorand.android.ui.accountstatus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountOriginalStateIconDrawablePreview
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.decider.AccountStatusDetailPreviewDecider
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent.NavToNoRekeyedAccounts
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent.NavToRekeyedAccountSelection
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.info.domain.usecase.FetchRekeyedAddresses
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.account.local.domain.usecase.GetHdSeedId
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.encryption.domain.manager.Base64Manager
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class AccountStatusDetailViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountOriginalStateIconDrawablePreview: GetAccountOriginalStateIconDrawablePreview,
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val fetchRekeyedAddresses: FetchRekeyedAddresses,
    private val accountActionProcessor: AccountStatusAccountActionProcessor,
    private val statusTypeProcessor: AccountStatusTypeDetailProcessor,
    private val accountStatusDetailPreviewDecider: AccountStatusDetailPreviewDecider,
    private val getHdSeedId: GetHdSeedId,
    private val getHdEntropy: GetHdEntropy,
    private val base64Manager: Base64Manager,
    private val aesPlatformManager: AESPlatformManager,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private var rekeyedAccountFetchingJob: Job? = null

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initializeAccountStatus(address: String) {
        viewModelScope.launch {
            getAccountLiteCacheFlow().collectLatest { cacheStatus ->
                val accountLite = (cacheStatus as? AccountLiteCacheStatus.Data)?.accountLites?.get(address)
                if (accountLite?.cachedInfo == null) return@collectLatest

                val accountType = accountLite.cachedInfo.type

                val viewState = ViewState.Content(
                    mainTitle = accountStatusDetailPreviewDecider.decideTitleString(accountType),
                    accountDisplayName = getAccountDisplayName(accountLite),
                    iconDrawablePreview = getAccountOriginalStateIconDrawablePreview(accountType),
                    accountActions = accountActionProcessor.getAccountActions(accountLite),
                    accountTypeDisplayName = accountStatusDetailPreviewDecider.decideAccountTypeString(accountLite),
                    accountTypeIconDrawablePreview = getAccountIconDrawablePreview(accountLite),
                    description = accountStatusDetailPreviewDecider.decideDescriptionDetail(accountLite),
                    detail = statusTypeProcessor.getAccountStatusTypeDetail(accountLite) ?: return@collectLatest,
                    rekeyAuthDetail = getRekeyAuthDetail(accountLite)
                )
                stateDelegate.updateState { viewState }
            }
        }
    }

    fun scanRekeyedAccounts(address: String) {
        stateDelegate.onState<ViewState.Content> { contentState ->
            eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowFetchingRekeyedAccountsDialog)
            rekeyedAccountFetchingJob = viewModelScope.launch {
                val viewEvent = fetchRekeyedAddresses(address).use(
                    onSuccess = { rekeyedAddresses ->
                        val notImported = rekeyedAddresses.notImportedAddresses
                        if (notImported.isEmpty()) {
                            NavToNoRekeyedAccounts
                        } else {
                            NavToRekeyedAccountSelection(address, contentState.iconDrawablePreview, notImported)
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

    private suspend fun getRekeyAuthDetail(accountLite: AccountLite): ViewState.Content.RekeyAuthDetail? {
        val rekeyAuthAddress = accountLite.cachedInfo?.rekeyAuthAddress
        return if (!rekeyAuthAddress.isNullOrBlank()) {
            ViewState.Content.RekeyAuthDetail(
                authAddressDisplayName = getAccountDisplayName(rekeyAuthAddress),
                authAddressIcon = getAccountIconDrawablePreview(rekeyAuthAddress),
                canSignTransaction = accountLite.cachedInfo.type.canSignTransaction()
            )
        } else {
            null
        }
    }

    fun navigateToRecoverRegisteredAccounts(address: String) {
        viewModelScope.launch {
            val hdSeed = getHdSeedId(address) ?: return@launch
            val entropy = getHdEntropy(hdSeed) ?: return@launch
            val encryptedEntropy = aesPlatformManager.encryptByteArray(entropy)
            val encryptedEntropyBase64 = base64Manager.encode(encryptedEntropy)
            eventDelegate.sendEvent(ViewEvent.NavToRecoverRegisteredAccounts(encryptedEntropyBase64))
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState

        data class Content(
            val mainTitle: String,
            val accountDisplayName: AccountDisplayName,
            val iconDrawablePreview: AccountIconDrawablePreview,
            val accountActions: List<AccountAction>,
            val accountTypeIconDrawablePreview: AccountIconDrawablePreview,
            val accountTypeDisplayName: String,
            val description: DescriptionDetail,
            val detail: AccountStatusTypeDetail,
            val rekeyAuthDetail: RekeyAuthDetail?
        ) : ViewState {

            data class DescriptionDetail(
                val description: String,
                val hyperlinkText: String,
                val hyperlinkUrl: String
            )

            data class RekeyAuthDetail(
                val authAddressDisplayName: AccountDisplayName,
                val authAddressIcon: AccountIconDrawablePreview,
                val canSignTransaction: Boolean
            )

            sealed interface AccountStatusTypeDetail {
                data object Standard : AccountStatusTypeDetail
                data object Ledger : AccountStatusTypeDetail
                data object Rekeyed : AccountStatusTypeDetail
                data object RekeyedAuth : AccountStatusTypeDetail
                data object Algo25 : AccountStatusTypeDetail
                data object NoAuth : AccountStatusTypeDetail
                data class HdKey(
                    val seedId: Int,
                    val walletName: String,
                    val iconDrawablePreview: AccountIconDrawablePreview
                ) : AccountStatusTypeDetail
            }

            sealed interface AccountAction {
                data object RekeyToStandard : AccountAction
                data object RekeyToLedger : AccountAction
                data object RescanRekeyedAddresses : AccountAction
            }
        }
    }

    sealed interface ViewEvent {
        data object ShowFetchingRekeyedAccountsDialog : ViewEvent
        data object HideFetchingRekeyedAccountsDialog : ViewEvent
        data class NavToRecoverRegisteredAccounts(val encryptedEntropyBase64: String) : ViewEvent
        data class NavToRekeyedAccountSelection(
            val authAddress: String,
            val authDrawable: AccountIconDrawablePreview,
            val rekeyedAddresses: List<String>
        ) : ViewEvent

        data object NavToNoRekeyedAccounts : ViewEvent
        data object ShowGenericError : ViewEvent
    }
}
