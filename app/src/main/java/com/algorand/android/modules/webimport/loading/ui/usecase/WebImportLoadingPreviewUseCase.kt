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

package com.algorand.android.modules.webimport.loading.ui.usecase

import com.algorand.android.core.component.caching.domain.usecase.FetchAccountInformationAndCacheAssets
import com.algorand.android.module.deeplink.model.WebImportQrCode
import com.algorand.android.modules.webimport.loading.domain.model.ImportedAccountResult
import com.algorand.android.modules.webimport.loading.domain.usecase.WebImportAccountDecryptionUseCase
import com.algorand.android.modules.webimport.loading.ui.model.WebImportLoadingPreview
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class WebImportLoadingPreviewUseCase @Inject constructor(
    private val webImportAccountDecryptionUseCase: WebImportAccountDecryptionUseCase,
    private val fetchAccountInformationAndCacheAssets: FetchAccountInformationAndCacheAssets
) {

    fun getInitialPreview(): WebImportLoadingPreview {
        return WebImportLoadingPreview(isLoadingVisible = true)
    }

    fun importEncryptedBackup(
        previousState: WebImportLoadingPreview,
        webImportQrCode: WebImportQrCode,
    ) = flow {
        emit(previousState.copy(isLoadingVisible = true))
        webImportAccountDecryptionUseCase.importEncryptedBackup(
            backupId = webImportQrCode.backupId,
            encryptionKey = webImportQrCode.encryptionKey
        ).collect {
            when (it) {
                is DataResource.Success -> {
                    cacheAccounts(it.data.importedAccountList)
                    emit(getSuccessStateOfImportRequest(previousState, it.data))
                }
                is DataResource.Error -> emit(getErrorStateOfImportRequest(previousState, it.exception))
                is DataResource.Loading -> { /* TODO handle loading if needed in future */
                }
            }
        }
    }

    private suspend fun cacheAccounts(importedAccountList: List<String>) {
        importedAccountList.forEach {
            fetchAccountInformationAndCacheAssets(it)
        }
    }

    private fun getSuccessStateOfImportRequest(
        previewState: WebImportLoadingPreview,
        data: ImportedAccountResult
    ): WebImportLoadingPreview {
        return previewState.copy(
            isLoadingVisible = false,
            requestSendSuccessEvent = Event(data)
        )
    }

    private fun getErrorStateOfImportRequest(
        previewState: WebImportLoadingPreview,
        exception: Throwable?
    ): WebImportLoadingPreview {
        return previewState.copy(
            isLoadingVisible = false,
            globalErrorEvent = if (exception?.message.isNullOrBlank()) null else Event(exception?.message.orEmpty())
        )
    }
}
