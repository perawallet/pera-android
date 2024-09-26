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

package com.algorand.android.modules.dapp.bidali.ui.accountselection.usecase

import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.module.account.core.ui.accountselection.usecase.GetAccountSelectionAccountsWhichCanSignTransaction
import com.algorand.android.modules.dapp.bidali.ui.accountselection.mapper.BidaliAccountSelectionPreviewMapper
import com.algorand.android.modules.dapp.bidali.ui.accountselection.model.BidaliAccountSelectionPreview
import com.algorand.android.module.node.domain.usecase.IsSelectedNodeMainnet
import javax.inject.Inject

class BidaliAccountSelectionPreviewUseCase @Inject constructor(
    private val bidaliAccountSelectionPreviewMapper: BidaliAccountSelectionPreviewMapper,
    private val getAccountSelectionAccountsWhichCanSignTransaction: GetAccountSelectionAccountsWhichCanSignTransaction,
    private val isSelectedNodeMainnet: IsSelectedNodeMainnet
) {

    fun getInitialPreview(): BidaliAccountSelectionPreview {
        return bidaliAccountSelectionPreviewMapper.mapToInitialPreview()
    }

    suspend fun getBidaliAccountSelectionList(): List<BaseAccountSelectionListItem> {
        return getAccountSelectionAccountsWhichCanSignTransaction(
            showHoldings = true,
            showFailedAccounts = false
        )
    }

    fun getOnAccountSelectedPreview(
        previousState: BidaliAccountSelectionPreview,
        accountAddress: String
    ): BidaliAccountSelectionPreview {
        return bidaliAccountSelectionPreviewMapper.mapToAccountSelectedPreview(
            previousState = previousState,
            accountAddress = accountAddress,
            isMainnet = isSelectedNodeMainnet()
        )
    }
}
