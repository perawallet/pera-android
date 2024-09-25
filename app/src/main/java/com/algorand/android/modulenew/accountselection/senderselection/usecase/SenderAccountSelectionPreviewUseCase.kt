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

package com.algorand.android.modulenew.accountselection.senderselection.usecase

import com.algorand.android.module.account.core.ui.accountselection.model.SenderAccountSelectionPreview
import com.algorand.android.module.account.core.ui.accountselection.usecase.GetAccountSelectionAccountsWhichCanSignTransaction
import com.algorand.android.core.component.caching.domain.usecase.FetchAccountInformationAndCacheAssets
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.PeraResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SenderAccountSelectionPreviewUseCase @Inject constructor(
    private val getAccountSelectionAccountsWhichCanSignTransaction: GetAccountSelectionAccountsWhichCanSignTransaction,
    private val fetchAccountInformationAndCacheAssets: FetchAccountInformationAndCacheAssets
) {

    fun getInitialPreview(): SenderAccountSelectionPreview {
        return SenderAccountSelectionPreview(
            accountList = emptyList(),
            isLoading = true,
            isEmptyStateVisible = false,
            senderAccountInformationSuccessEvent = null,
            senderAccountInformationErrorEvent = null
        )
    }

    suspend fun getUpdatedPreviewWithAccountList(
        preview: SenderAccountSelectionPreview
    ): SenderAccountSelectionPreview {
        val accountList = getAccountSelectionAccountsWhichCanSignTransaction(
            showHoldings = true,
            showFailedAccounts = true,
            excludedAccountTypes = null
        )
        return preview.copy(
            accountList = accountList,
            isEmptyStateVisible = accountList.isEmpty(),
            isLoading = false
        )
    }

    suspend fun getUpdatedPreviewWithAccountListAndSpecificAsset(
        assetId: Long,
        preview: SenderAccountSelectionPreview
    ): SenderAccountSelectionPreview {
        val accountList = getAccountSelectionAccountsWhichCanSignTransaction(
            assetId = assetId,
            showHoldings = true,
            showFailedAccounts = true
        )
        return preview.copy(
            accountList = accountList,
            isEmptyStateVisible = accountList.isEmpty(),
            isLoading = false
        )
    }

    suspend fun getUpdatedPreviewFlowWithAccountInformation(
        senderAccountAddress: String,
        preview: SenderAccountSelectionPreview
    ): Flow<SenderAccountSelectionPreview> = flow {
        emit(preview.copy(isLoading = true))
        val loadingFinishedPreview = preview.copy(isLoading = false)

        fetchAccountInformationAndCacheAssets(senderAccountAddress)
            .onSuccess {
                emit(loadingFinishedPreview.copy(senderAccountInformationSuccessEvent = Event(it)))
            }.onFailure {
                val errorResult = PeraResult.Error(Exception(it.message), null)
                emit(loadingFinishedPreview.copy(senderAccountInformationErrorEvent = Event(errorResult)))
            }
    }
}
