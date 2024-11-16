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

package com.algorand.android.modules.keyreg.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.keyreg.domain.usecase.CreateKeyRegTransaction
import com.algorand.android.modules.keyreg.ui.mapper.KeyRegTransactionPreviewMapper
import com.algorand.android.modules.keyreg.ui.model.KeyRegTransactionDetail
import com.algorand.android.modules.keyreg.ui.model.KeyRegTransactionPreview
import com.algorand.android.usecase.SendSignedTransactionUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class KeyRegTransactionViewModel @Inject constructor(
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val createKeyRegTransaction: CreateKeyRegTransaction,
    private val previewMapper: KeyRegTransactionPreviewMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var signingAccountAddress = savedStateHandle.getOrThrow<String>(
        SIGNING_ACCOUNT_ADDRSS
    )
    var keyRegTransactionDetail = savedStateHandle.getOrThrow<KeyRegTransactionDetail>(
        KEY_REG_DETAIL
    )

    private val _confirmedTransactionIdState = MutableStateFlow<String?>(null)
    val confirmedTransactionIdState
        get() = _confirmedTransactionIdState.asStateFlow()

    private val _previewState = MutableStateFlow<KeyRegTransactionPreview?>(null)
    val previewState
        get() = _previewState.asStateFlow()

    fun initUi() {
        _previewState.value = previewMapper.createInitialPreview(keyRegTransactionDetail, signingAccountAddress)
    }

    fun confirmTransaction() {
        viewModelScope.launchIO {
            createKeyRegTransaction(keyRegTransactionDetail).use(
                onSuccess = { transaction ->
                    _previewState.value = _previewState.value?.copy(signTransactionEvent = Event(transaction))
                },
                onFailed = { exception, _ ->
                    Log.e(TAG, exception.message.toString())
                    _previewState.value = _previewState.value?.copy(showErrorEvent = Event(Unit))
                }
            )
        }
    }

    fun sendSignedTransaction(signedTransactions: List<Any?>) {
        viewModelScope.launchIO {
            val signedTxnByteArray = signedTransactions.first() as? ByteArray ?: return@launchIO
            val signedTransactionDetail = SignedTransactionDetail.ExternalTransaction(signedTxnByteArray)
            sendSignedTransactionUseCase.sendSignedTransaction(signedTransactionDetail).collectLatest {
                it.useSuspended(
                    onSuccess = {
                        _confirmedTransactionIdState.value = it
                    },
                    onFailed = {
                        Log.d(TAG, it.exception.toString())
                        _confirmedTransactionIdState.value = TRANSACTION_ERROR
                    }
                )
            }
        }
    }

    companion object {
        const val SIGNING_ACCOUNT_ADDRSS = "signingAccountAddress"
        const val KEY_REG_DETAIL = "keyRegTransactionDetail"
        const val TAG = "KeyRegTransactionViewModel"
        const val TRANSACTION_ERROR = "transaction_error"
    }
}
