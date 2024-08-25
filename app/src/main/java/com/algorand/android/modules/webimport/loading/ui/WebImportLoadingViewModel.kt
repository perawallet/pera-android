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

package com.algorand.android.modules.webimport.loading.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.deeplink.model.WebImportQrCode
import com.algorand.android.modules.webimport.loading.ui.model.WebImportLoadingPreview
import com.algorand.android.modules.webimport.loading.ui.usecase.WebImportLoadingPreviewUseCase
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class WebImportLoadingViewModel @Inject constructor(
    private val webImportLoadingPreviewUseCase: WebImportLoadingPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val webImportQrCode = savedStateHandle.getOrThrow<WebImportQrCode>(WEB_IMPORT_QR_CODE_KEY)

    val webImportLoadingPreviewFlow: StateFlow<WebImportLoadingPreview>
        get() = _webImportLoadingPreviewFlow
    private val _webImportLoadingPreviewFlow = MutableStateFlow(getInitialPreview())

    init {
        viewModelScope.launch {
            startImportBackupFlow()
        }
    }

    private fun getInitialPreview(): WebImportLoadingPreview {
        return webImportLoadingPreviewUseCase.getInitialPreview()
    }

    private suspend fun startImportBackupFlow() {
        webImportLoadingPreviewUseCase
            .importEncryptedBackup(_webImportLoadingPreviewFlow.value, webImportQrCode)
            .collectLatest {
                _webImportLoadingPreviewFlow.emit(it)
            }
    }

    companion object {
        private const val WEB_IMPORT_QR_CODE_KEY = "webImportQrCode"
    }
}
