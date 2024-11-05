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

package com.algorand.android.modules.keyreg.ui.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.keyreg.domain.usecase.CreateKeyRegTransaction
import com.algorand.android.modules.keyreg.ui.presentation.mapper.KeyRegTransactionPreviewMapper
import com.algorand.android.modules.keyreg.ui.presentation.model.KeyRegTransactionDetail
import com.algorand.android.modules.keyreg.ui.presentation.model.KeyRegTransactionFragmentPreview
import com.algorand.android.usecase.SendSignedTransactionUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class KeyRegTransactionViewModel @Inject constructor(
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val createKeyRegTransaction: CreateKeyRegTransaction,
    private val previewMapper: KeyRegTransactionPreviewMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var keyRegTransactionDetail = savedStateHandle.getOrThrow<KeyRegTransactionDetail>(KEY_REG_DETAIL)

    private val _previewState = MutableStateFlow<KeyRegTransactionFragmentPreview?>(null)
    val previewState
        get() = _previewState.asStateFlow()

    fun initUi() {
        _previewState.value = previewMapper.createInitialPreview(keyRegTransactionDetail)
    }

    fun confirmTransaction() {
        viewModelScope.launchIO {
            createKeyRegTransaction(keyRegTransactionDetail).use(
                onSuccess = { transaction ->
                    _previewState.value = _previewState.value?.copy(signTransactionEvent = Event(transaction))
                },
                onFailed = { exception, _ ->
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
                        Log.e("TAG", "Success")
                        // Handle success
                    },
                    onFailed = {
                        Log.e("TAG", "Error = ${it.exception}")
                        // Handle error
                    }
                )
            }
        }
    }

    private companion object {
        const val KEY_REG_DETAIL = "key_reg_transaction_detail"
    }
}
