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

package com.algorand.android.ui.send.receiveraccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.models.AssetTransaction
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferTargetUser
import com.algorand.android.module.transaction.ui.sendasset.model.SendTransactionPayload
import com.algorand.android.usecase.ReceiverAccountSelectionUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@HiltViewModel
class ReceiverAccountSelectionViewModel @Inject constructor(
    private val receiverAccountSelectionUseCase: ReceiverAccountSelectionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val assetTransaction = savedStateHandle.get<AssetTransaction>(ASSET_TRANSACTION_KEY)!!

    private val _selectableAccountFlow = MutableStateFlow<List<BaseAccountSelectionListItem>?>(null)
    val selectableAccountFlow: StateFlow<List<BaseAccountSelectionListItem>?> = _selectableAccountFlow

    private val _toAccountTransactionRequirementsFlow = MutableStateFlow<Event<Resource<AssetTransferTargetUser>>?>(
        null
    )
    val toAccountTransactionRequirementsFlow: StateFlow<Event<Resource<AssetTransferTargetUser>>?> =
        _toAccountTransactionRequirementsFlow

    private val queryFlow = MutableStateFlow("")

    private val latestCopiedMessageFlow = MutableStateFlow<String?>(null)

    init {
        combineLatestCopiedMessageAndQueryFlow()
    }

    fun onSearchQueryUpdate(query: String) {
        viewModelScope.launch {
            queryFlow.emit(query)
        }
    }

    fun validateReceiverAccount(
        receiverAddress: String,
        nftDomainAddress: String? = null,
        nftDomainServiceLogoUrl: String? = null
    ) {
        viewModelScope.launch {
            val result = receiverAccountSelectionUseCase.checkToAccountTransactionRequirements(
                receiverAddress = receiverAddress,
                assetId = assetTransaction.assetId,
                fromAccountAddress = assetTransaction.senderAddress,
                amount = assetTransaction.amount,
                nftDomainAddress = nftDomainAddress,
                nftDomainServiceLogoUrl = nftDomainServiceLogoUrl
            )
            _toAccountTransactionRequirementsFlow.emit(Event(result))
        }
    }

    private fun combineLatestCopiedMessageAndQueryFlow() {
        viewModelScope.launch {
            combine(
                latestCopiedMessageFlow,
                queryFlow.debounce(QUERY_DEBOUNCE)
            ) { latestCopiedMessage, query ->
                receiverAccountSelectionUseCase.getToAccountList(
                    query = query,
                    latestCopiedMessage = latestCopiedMessage
                ).collectLatest {
                    _selectableAccountFlow.emit(it)
                }
            }.collect()
        }
    }

    fun updateCopiedMessage(copiedMessage: String?) {
        viewModelScope.launch {
            latestCopiedMessageFlow.emit(copiedMessage)
        }
    }

    fun getSendTransactionPayload(receiver: AssetTransferTargetUser): SendTransactionPayload {
        return SendTransactionPayload(
            assetId = assetTransaction.assetId,
            senderAddress = assetTransaction.senderAddress,
            amount = assetTransaction.amount,
            note = SendTransactionPayload.Note(
                note = assetTransaction.note,
                xnote = assetTransaction.xnote
            ),
            targetUser = receiver
        )
    }

    companion object {
        private const val ASSET_TRANSACTION_KEY = "assetTransaction"
        private const val QUERY_DEBOUNCE = 300L
    }
}
