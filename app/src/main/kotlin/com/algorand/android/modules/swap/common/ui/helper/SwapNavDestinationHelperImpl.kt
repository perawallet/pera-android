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

package com.algorand.android.modules.swap.common.ui.helper

import com.algorand.android.modules.swap.introduction.domain.usecase.IsSwapFeatureIntroductionPageShownUseCase
import com.algorand.android.modules.swap.model.SwapNavigationDestination
import com.algorand.android.modules.swap.model.SwapNavigationDestination.Introduction
import com.algorand.android.modules.swap.reddot.domain.usecase.SetSwapFeatureRedDotVisibilityUseCase
import javax.inject.Inject

internal class SwapNavDestinationHelperImpl @Inject constructor(
    private val isSwapFeatureIntroductionPageShownUseCase: IsSwapFeatureIntroductionPageShownUseCase,
    private val setSwapFeatureRedDotVisibilityUseCase: SetSwapFeatureRedDotVisibilityUseCase
) : SwapNavDestinationHelper {

    override suspend fun invoke(
        handleDestinationWithAccount: suspend () -> SwapNavigationDestination
    ): SwapNavigationDestination {
        hideSwapButtonRedDot()
        return if (isSwapIntroductionShown()) handleDestinationWithAccount() else Introduction
    }

    private suspend fun hideSwapButtonRedDot() {
        setSwapFeatureRedDotVisibilityUseCase.setSwapFeatureRedDotVisibility(isVisible = false)
    }

    private suspend fun isSwapIntroductionShown(): Boolean {
        return isSwapFeatureIntroductionPageShownUseCase.isSwapFeatureIntroductionPageShown()
    }
}
