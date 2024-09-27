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

package com.algorand.android.module.swap.ui.accountselection.usecase

import com.algorand.android.R
import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.module.foundation.Event
import com.algorand.android.module.swap.ui.accountselection.model.SwapAccountSelectionNavDirection
import com.algorand.android.module.swap.ui.accountselection.model.SwapAccountSelectionPreview
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

internal class GetSwapAccountSelectionAssetAddedPreviewUseCase @Inject constructor(
    private val cacheAccountDetail: CacheAccountDetail
) : GetSwapAccountSelectionAssetAddedPreview {

    override suspend fun invoke(
        accountAddress: String,
        fromAssetId: Long,
        toAssetId: Long,
        previousState: SwapAccountSelectionPreview,
        scope: CoroutineScope
    ): SwapAccountSelectionPreview {
        return cacheAccountDetail(accountAddress).use(
            onSuccess = {
                val swapNavigationEvent = getSwapNavigationDestinationEvent(accountAddress, fromAssetId, toAssetId)
                previousState.copy(navToSwapNavigationEvent = swapNavigationEvent)
            },
            onFailed = { _, _ ->
                val annotatedErrorString = AnnotatedString(R.string.an_error_occured)
                previousState.copy(errorEvent = Event(annotatedErrorString))
            }
        )
    }

    private fun getSwapNavigationDestinationEvent(
        accountAddress: String,
        fromAssetId: Long,
        toAssetId: Long
    ): Event<SwapAccountSelectionNavDirection.SwapNavigation> {
        return Event(SwapAccountSelectionNavDirection.SwapNavigation(accountAddress, fromAssetId, toAssetId))
    }
}
