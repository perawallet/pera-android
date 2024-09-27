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

package com.algorand.android.ui.wctransactionrequest

import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.core.BaseViewModel
import com.algorand.android.module.foundation.Event
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.BaseWalletConnectTransaction
import com.algorand.android.models.WalletConnectRequest.WalletConnectTransaction
import com.algorand.android.models.WalletConnectSession
import com.algorand.android.models.builder.WalletConnectTransactionListBuilder
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionManager
import com.algorand.android.modulenew.walletconnect.SignWalletConnectTransactionResult
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.android.modules.walletconnect.domain.WalletConnectManager
import com.algorand.android.ui.wctransactionrequest.ui.model.WalletConnectTransactionRequestPreview
import com.algorand.android.ui.wctransactionrequest.ui.usecase.WalletConnectTransactionRequestPreviewUseCase
import com.algorand.android.utils.Resource
import com.algorand.android.utils.getOrElse
import com.algorand.android.utils.preference.getFirstWalletConnectRequestBottomSheetShown
import com.algorand.android.utils.preference.setFirstWalletConnectRequestBottomSheetShown
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WalletConnectTransactionRequestViewModel @Inject constructor(
    private val walletConnectManager: WalletConnectManager,
    private val errorProvider: WalletConnectErrorProvider,
    private val sharedPreferences: SharedPreferences,
    private val transactionListBuilder: WalletConnectTransactionListBuilder,
    private val walletConnectTransactionRequestPreviewUseCase: WalletConnectTransactionRequestPreviewUseCase,
    private val signWalletConnectTransactionManager: SignWalletConnectTransactionManager,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val requestResultLiveData: LiveData<Event<Resource<AnnotatedString>>>
        get() = walletConnectManager.requestResultLiveData.map { Event(it.peek()) }

    val transaction: WalletConnectTransaction?
        get() = walletConnectManager.wcRequest as? WalletConnectTransaction

    private val walletConnectSession: WalletConnectSession?
        get() = transaction?.session

    private val shouldSkipConfirmation = savedStateHandle.getOrElse(SHOULD_SKIP_CONFIRMATION_KEY, false)

    private val _walletConnectTransactionRequestPreviewFlow = MutableStateFlow(getInitialPreview())
    val walletConnectTransactionRequestPreviewFlow: StateFlow<WalletConnectTransactionRequestPreview>
        get() = _walletConnectTransactionRequestPreviewFlow

    val signWcTransactionResultFlow: Flow<Event<SignWalletConnectTransactionResult>?>
        get() = signWalletConnectTransactionManager.signWalletConnectTransactionResultFlow

    fun setup(lifecycle: Lifecycle) {
        signWalletConnectTransactionManager.setup(lifecycle)
    }

    fun rejectRequest() {
        viewModelScope.launch {
            transaction?.let {
                walletConnectManager.rejectRequest(
                    sessionIdentifier = it.session.sessionIdentifier,
                    requestId = it.requestId,
                    errorResponse = errorProvider.getUserRejectionError()
                )
            }
        }
    }

    fun confirmRequest() {
        transaction?.run {
            signWalletConnectTransactionManager.sign(this)
        }
    }

    fun processWaitingTransaction() {
        signWalletConnectTransactionManager.signCachedTransaction()
    }

    fun shouldShowFirstRequestBottomSheet(): Boolean {
        return !sharedPreferences.getFirstWalletConnectRequestBottomSheetShown().also {
            sharedPreferences.setFirstWalletConnectRequestBottomSheetShown()
        }
    }

    fun processWalletConnectSignResult(result: SignWalletConnectTransactionResult.TransactionsSigned) {
        viewModelScope.launch {
            walletConnectManager.processWalletConnectSignResult(result)
        }
    }

    fun handleStartDestinationAndArgs(transactionList: List<WalletConnectTransactionListItem>): Pair<Int, Bundle?> {
        val startDestination = if (
            transactionList.count() == 1 &&
            transactionList.first() is WalletConnectTransactionListItem.SingleTransactionItem
        ) {
            R.id.walletConnectSingleTransactionFragment
        } else {
            R.id.walletConnectMultipleTransactionFragment
        }

        val startDestinationArgs = when (startDestination) {
            R.id.walletConnectSingleTransactionFragment -> {
                Bundle().apply { putParcelable(SINGLE_TRANSACTION_KEY, transactionList.first()) }
            }

            R.id.walletConnectMultipleTransactionFragment -> {
                Bundle().apply { putParcelableArray(MULTIPLE_TRANSACTION_KEY, transactionList.toTypedArray()) }
            }

            else -> null
        }

        return Pair(startDestination, startDestinationArgs)
    }

    fun createTransactionListItems(
        transactionList: List<List<BaseWalletConnectTransaction>>
    ): List<WalletConnectTransactionListItem> {
        return transactionListBuilder.createTransactionItems(transactionList)
    }

    fun onTransactionConfirmed() {
        viewModelScope.launch {
            val preview = walletConnectTransactionRequestPreviewUseCase.updatePreviewWithLaunchBackBrowserNavigation(
                shouldSkipConfirmation = shouldSkipConfirmation,
                walletConnectSession = walletConnectSession,
                preview = _walletConnectTransactionRequestPreviewFlow.value
            )
            _walletConnectTransactionRequestPreviewFlow.emit(preview)
        }
    }

    fun stopAllResources() {
        signWalletConnectTransactionManager.stopAllResources()
    }

    private fun getInitialPreview(): WalletConnectTransactionRequestPreview {
        return walletConnectTransactionRequestPreviewUseCase.getInitialWalletConnectTransactionRequestPreview()
    }

    companion object {
        private const val MULTIPLE_TRANSACTION_KEY = "transactions"
        private const val SINGLE_TRANSACTION_KEY = "transaction"
        private const val SHOULD_SKIP_CONFIRMATION_KEY = "shouldSkipConfirmation"
    }
}
