/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.expresssend.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.algorand.android.models.TransactionData
import com.algorand.android.modules.assetinbox.expresssend.domain.usecase.Arc59ExpressSendUseCase
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class Arc59ExpressSendViewModel @Inject constructor(
    private val arc59ExpressSendUseCase: Arc59ExpressSendUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val transactionData =
        savedStateHandle.getOrThrow<TransactionData.Send>(TRANSACTION_DATA_KEY)

    fun disableArc59ExpressSendWarning() {
        arc59ExpressSendUseCase.disableExpressSendWarning()
    }

    companion object {
        private const val TRANSACTION_DATA_KEY = "transactionData"
    }
}
