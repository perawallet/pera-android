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

package com.algorand.android.ui.send.receiveraccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.BaseAccountSelectionListItem
import com.algorand.android.models.Result
import com.algorand.android.models.TargetUser
import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.accountasset.domain.model.AccountAssetDetail
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.modules.assetinbox.expresssend.domain.usecase.Arc59ExpressSendUseCase
import com.algorand.android.usecase.ReceiverAccountSelectionUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.Resource
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@HiltViewModel
class ReceiverAccountSelectionViewModel @Inject constructor(
    private val receiverAccountSelectionUseCase: ReceiverAccountSelectionUseCase,
    private val arc59ExpressSendUseCase: Arc59ExpressSendUseCase,
    private val getTransactionSigner: GetTransactionSigner,
    private val getAccountInformation: GetAccountInformation,
    private val getAccountLite: GetAccountLite,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val assetTransaction = savedStateHandle.get<AssetTransaction>(ASSET_TRANSACTION_KEY)!!

    private val _selectableAccountFlow = MutableStateFlow<List<BaseAccountSelectionListItem>?>(null)
    val selectableAccountFlow: StateFlow<List<BaseAccountSelectionListItem>?> = _selectableAccountFlow

    private val _toAccountAddressValidationFlow = MutableStateFlow<Event<Resource<String>>?>(null)
    val toAccountAddressValidationFlow: StateFlow<Event<Resource<String>>?> = _toAccountAddressValidationFlow

    private val _toAccountInformationFlow = MutableStateFlow<Event<Resource<AccountAssetDetail>>?>(null)
    val toAccountInformationFlow: StateFlow<Event<Resource<AccountAssetDetail>>?> = _toAccountInformationFlow

    private val _toAccountTransactionRequirementsFlow = MutableStateFlow<Event<Resource<TargetUser>>?>(null)
    val toAccountTransactionRequirementsFlow: StateFlow<Event<Resource<TargetUser>>?> =
        _toAccountTransactionRequirementsFlow

    private val _sendTransactionDataFlow: MutableStateFlow<Event<TransactionSignData.Send>?> = MutableStateFlow(null)
    val sendTransactionDataFlow: StateFlow<Event<TransactionSignData.Send>?>
        get() = _sendTransactionDataFlow.asStateFlow()

    private var nftDomainAddressServiceLogoPair: Pair<String, String?>? = null

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

    fun checkIsGivenAddressValid(toAccountPublicKey: String) {
        viewModelScope.launch {
            _toAccountAddressValidationFlow.emit(Event(Resource.Loading))
            when (val result = receiverAccountSelectionUseCase.isAccountAddressValid(toAccountPublicKey)) {
                is Result.Error -> _toAccountAddressValidationFlow.emit(Event(result.getAsResourceError()))
                is Result.Success -> _toAccountAddressValidationFlow.emit(Event(Resource.Success(result.data)))
            }
        }
    }

    fun fetchToAccountInformation(
        toAccountPublicKey: String,
        nftDomainAddress: String? = null,
        nftDomainServiceLogoUrl: String? = null
    ) {
        nftDomainAddressServiceLogoPair = nftDomainAddress?.run { Pair(this, nftDomainServiceLogoUrl) }
        viewModelScope.launch {
            _toAccountInformationFlow.emit(Event(Resource.Loading))
            val result = receiverAccountSelectionUseCase.fetchAccountInformationForAsset(
                toAccountPublicKey,
                assetTransaction.assetId
            )
            when (result) {
                is Result.Success -> _toAccountInformationFlow.emit(Event(Resource.Success(result.data)))
                is Result.Error -> _toAccountInformationFlow.emit(Event(result.getAsResourceError()))
            }
        }
    }

    fun checkToAccountTransactionRequirements(accountAssetDetail: AccountAssetDetail) {
        viewModelScope.launch {
            _toAccountTransactionRequirementsFlow.emit(Event(Resource.Loading))
            val result = receiverAccountSelectionUseCase.checkToAccountTransactionRequirements(
                accountAssetDetail,
                assetTransaction.assetId,
                assetTransaction.senderAddress,
                amount = assetTransaction.amount,
                nftDomainAddress = nftDomainAddressServiceLogoPair?.first,
                nftDomainServiceLogoUrl = nftDomainAddressServiceLogoPair?.second
            )
            when (result) {
                is Result.Error -> _toAccountTransactionRequirementsFlow.emit(Event(result.getAsResourceError()))
                is Result.Success -> {
                    _toAccountTransactionRequirementsFlow.emit(Event(Resource.Success(result.data)))
                }
            }
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

    fun getSendTransactionData(targetUser: TargetUser) {
        val assetTransaction = assetTransaction
        val note = assetTransaction.xnote ?: assetTransaction.note
        val minBalanceCalculatedAmount = assetTransaction.amount

        viewModelScope.launch {
            val isArc59Transaction = isArc59Transaction(targetUser.publicKey, assetTransaction.assetId)
            val accountLite = getAccountLite(assetTransaction.senderAddress)
            val accountLiteCachedInfo = accountLite?.cachedInfo ?: return@launch
            val txnData = TransactionSignData.Send(
                senderAccountAddress = assetTransaction.senderAddress,
                senderAccountName = accountLite.customName,
                senderAuthAddress = accountLiteCachedInfo.rekeyAuthAddress,
                senderAlgoAmount = accountLiteCachedInfo.algoAmountValue.amount,
                minimumBalance = accountLiteCachedInfo.minRequiredBalance.toLong(),
                amount = minBalanceCalculatedAmount,
                assetId = assetTransaction.assetId,
                note = note,
                targetUser = targetUser,
                isArc59Transaction = isArc59Transaction,
                signer = getTransactionSigner(assetTransaction.senderAddress)
            )
            _sendTransactionDataFlow.emit(Event(txnData))
        }
    }

    private suspend fun isArc59Transaction(targetUserAddress: String, assetId: Long): Boolean {
        return getAccountInformation(targetUserAddress)?.hasAsset(assetId) == false
    }

    fun isExpressSendWarningEnabled(isArc59Transaction: Boolean): Boolean {
        return arc59ExpressSendUseCase.isExpressSendWarningEnabled(isArc59Transaction)
    }

    companion object {
        private const val ASSET_TRANSACTION_KEY = "assetTransaction"
        private const val QUERY_DEBOUNCE = 300L
    }
}
