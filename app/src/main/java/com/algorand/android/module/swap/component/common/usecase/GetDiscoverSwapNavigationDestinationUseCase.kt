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

package com.algorand.android.module.swap.component.common.usecase

import com.algorand.android.module.swap.component.common.SwapNavigationDestinationHelper
import com.algorand.android.module.swap.component.common.model.DiscoverNavigationDestination
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination
import javax.inject.Inject

internal class GetDiscoverSwapNavigationDestinationUseCase @Inject constructor(
    private val swapNavigationDestinationHelper: SwapNavigationDestinationHelper
) : GetDiscoverSwapNavigationDestination {

    override suspend fun invoke(): DiscoverNavigationDestination {
        return when (getHelperNavigation()) {
            SwapNavigationDestination.Introduction -> DiscoverNavigationDestination.Introduction
            else -> DiscoverNavigationDestination.AccountSelection
        }
    }

    private suspend fun getHelperNavigation(): SwapNavigationDestination {
        return swapNavigationDestinationHelper {
            SwapNavigationDestination.AccountSelection
        }
    }
}
