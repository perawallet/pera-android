/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.swap.utils

import com.algorand.android.modules.swap.introduction.domain.usecase.IsSwapFeatureIntroductionPageShownUseCase
import com.algorand.android.modules.swap.reddot.domain.usecase.SetSwapFeatureRedDotVisibilityUseCase
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import javax.inject.Inject

class SwapNavigationDestinationHelper @Inject constructor(
    private val getAccountsDetails: GetAccountsDetails,
    isSwapFeatureIntroductionPageShownUseCase: IsSwapFeatureIntroductionPageShownUseCase,
    setSwapFeatureRedDotVisibilityUseCase: SetSwapFeatureRedDotVisibilityUseCase
) : BaseSwapNavigationDestinationHelper(
    isSwapFeatureIntroductionPageShownUseCase,
    setSwapFeatureRedDotVisibilityUseCase
) {

    suspend fun getSwapNavigationDestination(
        accountAddress: String? = null,
        onNavToIntroduction: () -> Unit,
        onNavToSwap: (accountAddress: String) -> Unit,
        onNavToAccountSelection: (() -> Unit)? = null
    ) {
        val authorizedAccounts = getAccountsDetails().filter { it.accountType?.canSignTransaction() == true }

        handleNavigationDestination(
            navToIntroduction = { onNavToIntroduction() },
            handleDestinationWithAccount = {
                handleDestinationWithAccount(accountAddress, onNavToSwap, onNavToAccountSelection, authorizedAccounts)
            }
        )
    }

    private fun handleDestinationWithAccount(
        accountAddress: String?,
        onNavToSwap: (accountAddress: String) -> Unit,
        onNavToAccountSelection: (() -> Unit)?,
        authorizedAccounts: List<AccountDetail>
    ) {
        if (accountAddress != null) {
            onNavToSwap(accountAddress)
        } else {
            if (authorizedAccounts.size == 1) {
                onNavToSwap(authorizedAccounts.first().address)
            } else {
                onNavToAccountSelection?.invoke()
            }
        }
    }
}
