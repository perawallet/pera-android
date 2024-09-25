/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.transaction.ui.addasset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.coroutine.CoroutineExtensions.launchIfInactive
import com.algorand.android.module.transaction.component.domain.creation.CreateAddAssetTransaction
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult
import com.algorand.android.module.transaction.ui.addasset.model.AddAssetTransactionPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CreateAddAssetTransactionViewModel @Inject constructor(
    private val createAddAssetTransaction: CreateAddAssetTransaction
) : ViewModel() {

    private val _createTransactionFlow = MutableStateFlow<Event<CreateTransactionResult>?>(null)
    val createTransactionFlow: StateFlow<Event<CreateTransactionResult>?> = _createTransactionFlow.asStateFlow()

    private var createTransactionJob: Job? = null

    private var addAssetTransactionPayload: AddAssetTransactionPayload? = null

    fun create(payload: AddAssetTransactionPayload) {
        createTransactionJob = viewModelScope.launchIfInactive(createTransactionJob) {
            addAssetTransactionPayload = payload
            _createTransactionFlow.value = Event(createAddAssetTransaction(payload.address, payload.assetId))
        }
    }

    fun retryAddAssetTransaction() {
        addAssetTransactionPayload?.let { payload ->
            create(payload)
        }
    }
}
