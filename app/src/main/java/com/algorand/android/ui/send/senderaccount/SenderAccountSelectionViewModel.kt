/*
 * Copyright 2022 Pera Wallet, LDA
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

package com.algorand.android.ui.send.senderaccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.account.core.ui.accountselection.model.SenderAccountSelectionPreview
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.foundation.Event
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modulenew.accountselection.senderselection.usecase.SenderAccountSelectionPreviewUseCase
import com.algorand.android.module.transaction.ui.sendasset.domain.GetAssetTransferTargetUser
import com.algorand.android.module.transaction.ui.sendasset.model.SendTransactionPayload
import com.algorand.android.ui.send.senderaccount.SenderAccountSelectionNavigationDirections.AssetSelectionFragment
import com.algorand.android.ui.send.senderaccount.SenderAccountSelectionNavigationDirections.AssetTransferAmountFragment
import com.algorand.android.ui.send.senderaccount.SenderAccountSelectionNavigationDirections.AssetTransferPreviewFragment
import com.algorand.android.ui.send.senderaccount.SenderAccountSelectionNavigationDirections.ReceiverAccountSelectionFragment
import com.algorand.android.usecase.TransactionTipsUseCase
import com.algorand.android.utils.getOrElse
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SenderAccountSelectionViewModel @Inject constructor(
    private val senderAccountSelectionPreviewUseCase: SenderAccountSelectionPreviewUseCase,
    private val transactionTipsUseCase: TransactionTipsUseCase,
    private val getAssetTransferTargetUser: GetAssetTransferTargetUser,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val assetTransaction: AssetTransaction = savedStateHandle.getOrElse(ASSET_TRANSACTION_KEY, AssetTransaction())

    private val _senderAccountSelectionPreviewFlow =
        MutableStateFlow(senderAccountSelectionPreviewUseCase.getInitialPreview())
    val senderAccountSelectionPreviewFlow: StateFlow<SenderAccountSelectionPreview> = _senderAccountSelectionPreviewFlow

    private val _senderAccountSelectionNavigationDirectionFlow =
        MutableStateFlow<Event<SenderAccountSelectionNavigationDirections>?>(null)
    val senderAccountSelectionNavigationDirectionFlow = _senderAccountSelectionNavigationDirectionFlow.asStateFlow()

    init {
        // If user came with deeplink or qr code then we have to filter accounts that have incoming asset id
        if (assetTransaction.assetId != -1L && assetTransaction.assetId != ALGO_ASSET_ID) {
            getAccountCacheWithSpecificAsset(assetTransaction.assetId)
        } else {
            getAccounts()
        }
    }

    private fun getAccounts() {
        viewModelScope.launch {
            _senderAccountSelectionPreviewFlow.emit(
                senderAccountSelectionPreviewUseCase.getUpdatedPreviewWithAccountList(
                    preview = _senderAccountSelectionPreviewFlow.value
                )
            )
        }
    }

    private fun getAccountCacheWithSpecificAsset(assetId: Long) {
        viewModelScope.launch {
            _senderAccountSelectionPreviewFlow.emit(
                senderAccountSelectionPreviewUseCase.getUpdatedPreviewWithAccountListAndSpecificAsset(
                    assetId = assetId,
                    preview = _senderAccountSelectionPreviewFlow.value
                )
            )
        }
    }

    fun fetchSenderAccountInformation(senderAccountAddress: String) {
        viewModelScope.launch {
            senderAccountSelectionPreviewUseCase.getUpdatedPreviewFlowWithAccountInformation(
                senderAccountAddress = senderAccountAddress,
                preview = _senderAccountSelectionPreviewFlow.value
            ).collectLatest {
                _senderAccountSelectionPreviewFlow.emit(it)
            }
        }
    }

    fun shouldShowTransactionTips(): Boolean {
        return transactionTipsUseCase.shouldShowTransactionTips()
    }

    // If user enter Send Algo flow via deeplink or qr code, then we have to check asset transaction params then
    // we should navigate user to proper screen
    fun handleSenderAccountSelectionSuccessResult(accountInformation: AccountInformation) {
        viewModelScope.launchIO {
            assetTransaction.copy(senderAddress = accountInformation.address).run {
                val result = when {
                    assetId == -1L -> AssetSelectionFragment(this)
                    amount == BigInteger.ZERO -> AssetTransferAmountFragment(this)
                    receiverUser == null -> ReceiverAccountSelectionFragment(this)
                    else -> AssetTransferPreviewFragment(createSendTransactionData(this))
                }
                _senderAccountSelectionNavigationDirectionFlow.emit(Event(result))
            }
        }
    }

    private suspend fun createSendTransactionData(assetTransaction: AssetTransaction): SendTransactionPayload {
        return SendTransactionPayload(
            assetId = assetTransaction.assetId,
            senderAddress = assetTransaction.senderAddress,
            amount = assetTransaction.amount,
            note = SendTransactionPayload.Note(assetTransaction.note, assetTransaction.xnote),
            targetUser = getAssetTransferTargetUser(assetTransaction.receiverUser?.address.orEmpty())
        )
    }

    companion object {
        private const val ASSET_TRANSACTION_KEY = "assetTransaction"
    }
}
