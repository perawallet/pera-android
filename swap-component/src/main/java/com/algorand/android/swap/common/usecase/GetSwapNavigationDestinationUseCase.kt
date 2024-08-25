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

package com.algorand.android.swap.common.usecase

import com.algorand.android.core.component.detail.domain.usecase.GetAccountsDetail
import com.algorand.android.swap.common.SwapNavigationDestinationHelper
import com.algorand.android.swap.common.model.SwapNavigationDestination
import com.algorand.android.swap.common.model.SwapNavigationDestination.AccountSelection
import com.algorand.android.swap.common.model.SwapNavigationDestination.Swap
import javax.inject.Inject

internal class GetSwapNavigationDestinationUseCase @Inject constructor(
    private val getAccountsDetail: GetAccountsDetail,
    private val swapNavigationDestinationHelper: SwapNavigationDestinationHelper
) : GetSwapNavigationDestination {

    override suspend fun invoke(address: String?): SwapNavigationDestination {
        return swapNavigationDestinationHelper {
            getDestinationWithAccount(address)
        }
    }

    private suspend fun getDestinationWithAccount(address: String?): SwapNavigationDestination {
        return if (address != null) {
            Swap(address)
        } else {
            getDestinationWithCheckingAuthAccounts()
        }
    }

    private suspend fun getDestinationWithCheckingAuthAccounts(): SwapNavigationDestination {
        return getAccountsDetail().filter { it.canSignTransaction() }.run {
            if (size == 1) Swap(first().address) else AccountSelection
        }
    }
}
