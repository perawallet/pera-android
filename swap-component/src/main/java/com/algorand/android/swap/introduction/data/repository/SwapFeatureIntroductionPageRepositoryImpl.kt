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

package com.algorand.android.swap.introduction.data.repository

import com.algorand.android.swap.introduction.data.storage.SwapFeatureIntroductionPagePreferenceLocalSource
import com.algorand.android.swap.introduction.data.storage.SwapFeatureIntroductionPagePreferenceLocalSource.Companion.defaultSwapFeatureIntroductionPagePreference
import com.algorand.android.swap.introduction.domain.repository.SwapFeatureIntroductionPageRepository
import javax.inject.Inject

internal class SwapFeatureIntroductionPageRepositoryImpl @Inject constructor(
    private val visibilityLocalSource: SwapFeatureIntroductionPagePreferenceLocalSource
) : SwapFeatureIntroductionPageRepository {

    override suspend fun getSwapFeatureIntroductionPageVisibility(): Boolean {
        return visibilityLocalSource.getData(defaultSwapFeatureIntroductionPagePreference)
    }

    override suspend fun setSwapFeatureIntroductionPageVisibility(isVisible: Boolean) {
        visibilityLocalSource.saveData(isVisible)
    }
}
