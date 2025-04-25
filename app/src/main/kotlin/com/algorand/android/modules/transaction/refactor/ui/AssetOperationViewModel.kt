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

package com.algorand.android.modules.transaction.refactor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.AssetActionResult
import com.algorand.android.models.AssetOperationResult
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.transaction.refactor.usecase.CreateAddAssetTransactionData
import com.algorand.android.modules.transaction.refactor.usecase.CreateRemoveAssetTransactionData
import com.algorand.android.usecase.SendSignedTransactionUseCase
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.Event
import com.algorand.android.utils.Resource
import com.algorand.android.utils.exception.AccountAlreadyOptedIntoAssetException
import com.algorand.android.utils.exception.AssetAlreadyPendingForRemovalException
import com.algorand.android.utils.exceptions.TransactionConfirmationAwaitException
import com.algorand.android.utils.exceptions.TransactionIdNullException
import com.algorand.android.utils.sendErrorLog
import com.algorand.wallet.asset.domain.usecase.GetAsset
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AssetOperationViewModel @Inject constructor(
    private val createAddAssetTransactionData: CreateAddAssetTransactionData,
    private val createRemoveAssetTransactionData: CreateRemoveAssetTransactionData,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val getAsset: GetAsset
) : ViewModel() {

    private var latestAddAssetTransaction: TransactionSignData.AddAsset? = null
    private var sendTransactionJob: Job? = null

    private val _assetOperationResultFlow = MutableStateFlow<Event<Resource<AssetOperationResult>>?>(null)
    val assetOperationResultFlow: StateFlow<Event<Resource<AssetOperationResult>>?>
        get() = _assetOperationResultFlow.asStateFlow()

    private val _assetTransactionDataFlow: MutableStateFlow<Event<TransactionSignData>?> = MutableStateFlow(null)
    val assetTransactionDataFlow: StateFlow<Event<TransactionSignData>?>
        get() = _assetTransactionDataFlow.asStateFlow()

    fun createAddAssetTransaction(assetActionResult: AssetActionResult) {
        val address = assetActionResult.publicKey ?: return
        viewModelScope.launch {
            val transactionData = createAddAssetTransactionData(address, assetActionResult.assetId)
            if (transactionData != null) {
                setLatestAddAssetTransaction(transactionData)
                _assetTransactionDataFlow.update { Event(transactionData) }
            }
        }
    }

    fun createRemoveAssetTransaction(assetActionResult: AssetActionResult) {
        val address = assetActionResult.publicKey ?: return
        viewModelScope.launch {
            val transactionData = createRemoveAssetTransactionData(address, assetActionResult.assetId)
            if (transactionData != null) {
                _assetTransactionDataFlow.update { Event(transactionData) }
            }
        }
    }

    fun getLatestAddAssetTransaction(): TransactionSignData.AddAsset? = latestAddAssetTransaction

    fun sendAssetOperationSignedTransaction(transaction: SignedTransactionDetail.AssetOperation) {
        if (sendTransactionJob?.isActive == true) {
            return
        }

        sendTransactionJob = viewModelScope.launch(Dispatchers.IO) {
            sendSignedTransactionUseCase.sendSignedTransaction(transaction).collectLatest { dataResource ->
                when (dataResource) {
                    is DataResource.Success -> {
                        latestAddAssetTransaction = null
                        val assetActionResult = getAssetOperationResult(transaction)
                        _assetOperationResultFlow.emit(Event(Resource.Success(assetActionResult)))
                    }

                    is DataResource.Error.Api -> {
                        _assetOperationResultFlow.emit(Event(Resource.Error.Api(dataResource.exception)))
                    }

                    is DataResource.Error.Local -> {
                        // TODO add specific strings for exceptions
                        val errorResourceId = when (dataResource.exception) {
                            is AccountAlreadyOptedIntoAssetException -> R.string.you_are_already
                            is TransactionConfirmationAwaitException -> R.string.transaction_confirmation_timed_out
                            is TransactionIdNullException -> R.string.an_error_occured
                            is AssetAlreadyPendingForRemovalException -> R.string.this_asset_is
                            else -> R.string.an_error_occured
                        }
                        val assetName = getAsset(transaction.assetId)?.fullName.orEmpty()
                        val error = Resource.Error.GlobalWarning(
                            titleRes = R.string.error,
                            annotatedString = AnnotatedString(
                                stringResId = errorResourceId,
                                replacementList = listOf("asset_name" to assetName)
                            )
                        )
                        _assetOperationResultFlow.emit(Event(error))
                    }
                    else -> sendErrorLog("Unhandled else case in MainViewModel.sendSignedTransaction")
                }
            }
        }
    }

    private suspend fun getAssetOperationResult(
        transaction: SignedTransactionDetail.AssetOperation
    ): AssetOperationResult {
        val asset = getAsset(transaction.assetId)
        val assetName = asset?.fullName ?: asset?.shortName
        return when (transaction) {
            is SignedTransactionDetail.AssetOperation.AssetAddition -> {
                AssetOperationResult.AssetAdditionOperationResult(
                    resultTitleResId = R.string.asset_successfully_opted_in,
                    assetName = AssetName.create(assetName),
                    assetId = transaction.assetId
                )
            }

            is SignedTransactionDetail.AssetOperation.AssetRemoval -> {
                AssetOperationResult.AssetRemovalOperationResult(
                    resultTitleResId = R.string.asset_successfully_opted_out_from_your,
                    assetName = AssetName.create(assetName),
                    assetId = transaction.assetId
                )
            }
        }
    }

    private fun setLatestAddAssetTransaction(transactionData: TransactionSignData.AddAsset) {
        latestAddAssetTransaction = transactionData
    }
}
