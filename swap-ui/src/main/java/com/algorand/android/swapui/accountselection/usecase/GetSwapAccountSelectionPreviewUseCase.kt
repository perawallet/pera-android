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

package com.algorand.android.swapui.accountselection.usecase

import com.algorand.android.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountsWhichCanSignTransaction
import com.algorand.android.swapui.accountselection.model.SwapAccountSelectionPreview
import javax.inject.Inject

internal class GetSwapAccountSelectionPreviewUseCase @Inject constructor(
    private val getAccountSelectionAccountsWhichCanSignTransaction: GetAccountSelectionAccountsWhichCanSignTransaction,
) : GetSwapAccountSelectionPreview {

    override suspend fun invoke(): SwapAccountSelectionPreview {
        val accountSelectionList = getAccountSelectionAccountsWhichCanSignTransaction(
            showHoldings = false,
            showFailedAccounts = true
        )
        return SwapAccountSelectionPreview(
            accountListItems = accountSelectionList,
            isLoading = false,
            navToSwapNavigationEvent = null,
            errorEvent = null,
            isEmptyStateVisible = accountSelectionList.isEmpty(),
            optInToAssetEvent = null
        )
    }
}
