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

package com.algorand.android.modules.swap.utils

import com.algorand.android.modules.swap.introduction.domain.usecase.IsSwapFeatureIntroductionPageShownUseCase
import com.algorand.android.modules.swap.reddot.domain.usecase.SetSwapFeatureRedDotVisibilityUseCase
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.remoteconfig.domain.usecase.SWAP_V2_TOGGLE
import javax.inject.Inject

class DiscoverSwapNavigationDestinationHelper @Inject constructor(
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    isSwapFeatureIntroductionPageShownUseCase: IsSwapFeatureIntroductionPageShownUseCase,
    setSwapFeatureRedDotVisibilityUseCase: SetSwapFeatureRedDotVisibilityUseCase
) : BaseSwapNavigationDestinationHelper(
    isSwapFeatureIntroductionPageShownUseCase,
    setSwapFeatureRedDotVisibilityUseCase
) {

    suspend fun getSwapNavigationDestination(
        onNavToIntroduction: () -> Unit,
        onNavToAccountSelection: (() -> Unit),
        onNavToSwapV2: () -> Unit
    ) {
        if (isFeatureToggleEnabled(SWAP_V2_TOGGLE)) {
            onNavToSwapV2()
        } else {
            handleNavigationDestination(
                navToIntroduction = { onNavToIntroduction() },
                handleDestinationWithAccount = { onNavToAccountSelection() }
            )
        }
    }
}
