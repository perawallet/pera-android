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

package com.algorand.android.nft.domain.usecase

import com.algorand.android.R
import com.algorand.android.accountcore.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountsWhichCanSignTransaction
import com.algorand.android.mapper.ScreenStateMapper
import com.algorand.android.models.ScreenState
import com.algorand.android.nft.mapper.CollectibleReceiverAccountSelectionPreviewMapper
import com.algorand.android.nft.ui.model.CollectibleReceiverAccountSelectionPreview
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class CollectibleReceiverAccountSelectionPreviewUseCase @Inject constructor(
    private val previewMapper: CollectibleReceiverAccountSelectionPreviewMapper,
    private val screenStateMapper: ScreenStateMapper,
    private val getAccountSelectionAccountsWhichCanSignTransaction: GetAccountSelectionAccountsWhichCanSignTransaction
) {

    fun getAccountListItems() = flow<CollectibleReceiverAccountSelectionPreview> {
        emit(previewMapper.mapToLoadingPreview())
        val accountListItems = getAccountSelectionAccountsWhichCanSignTransaction(
            showHoldings = false,
            showFailedAccounts = true
        )
        val screenState = createEmptyStateIfNeed(accountListItems)
        val isScreenStateViewVisible = screenState != null
        emit(
            previewMapper.mapToCollectibleReceiverAccountSelectionPreview(
                accountItems = accountListItems,
                screenState = screenState,
                isScreenStateViewVisible = isScreenStateViewVisible
            )
        )
    }

    private fun createEmptyStateIfNeed(accountItems: List<BaseAccountSelectionListItem>): ScreenState.CustomState? {
        return if (accountItems.isEmpty()) {
            screenStateMapper.mapToCustomState(
                title = R.string.no_account_found,
                description = R.string.you_need_to_create,
            )
        } else {
            null
        }
    }
}
