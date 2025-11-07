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

package com.algorand.android.ui.transaction.csv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.transaction.csv.model.CreateCsvArgs
import com.algorand.android.ui.transaction.csv.usecase.CreateCsvFile
import com.algorand.android.ui.transaction.csv.viewmodel.CsvViewModel.ViewState
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CsvViewModel @Inject constructor(
    private val createCsvFile: CreateCsvFile,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<CsvViewModel.ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun createCsv(args: CreateCsvArgs) {
        stateDelegate.onState<ViewState.Idle> {
            stateDelegate.updateState { ViewState.Loading }
            viewModelScope.launch {
                createCsvFile(args).use(onSuccess = ::shareFile, onFailed = ::showError)
            }
        }
    }

    private suspend fun shareFile(file: File) {
        stateDelegate.updateState { ViewState.Idle }
        eventDelegate.sendEvent(ViewEvent.ShareFile(file))
    }

    private suspend fun showError(exception: Exception, code: Int?) {
        stateDelegate.updateState { ViewState.Idle }
        eventDelegate.sendEvent(ViewEvent.ShowErrorMessage)
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
    }

    sealed interface ViewEvent {
        data class ShareFile(val file: File) : ViewEvent
        data object ShowErrorMessage : ViewEvent
    }
}
