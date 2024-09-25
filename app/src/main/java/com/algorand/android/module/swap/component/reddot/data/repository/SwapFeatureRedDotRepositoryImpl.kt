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

package com.algorand.android.module.swap.component.reddot.data.repository

import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.module.swap.component.reddot.data.storage.SwapFeatureRedDotPreferenceLocalSource.Companion.defaultSwapFeatureRedDotPreference
import com.algorand.android.module.swap.component.reddot.domain.repository.SwapFeatureRedDotRepository

internal class SwapFeatureRedDotRepositoryImpl(
    private val swapFeatureRedDotLocalSource: SharedPrefLocalSource<Boolean>
) : SwapFeatureRedDotRepository {

    override suspend fun getSwapFeatureRedDotVisibility(): Boolean {
        return swapFeatureRedDotLocalSource.getData(defaultSwapFeatureRedDotPreference)
    }

    override suspend fun setSwapFeatureRedDotVisibility(isVisible: Boolean) {
        swapFeatureRedDotLocalSource.saveData(isVisible)
    }
}
