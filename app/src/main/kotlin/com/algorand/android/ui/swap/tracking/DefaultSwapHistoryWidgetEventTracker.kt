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

package com.algorand.android.ui.swap.tracking

import com.algorand.android.modules.tracking.core.BaseEventTracker
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import javax.inject.Inject

internal class DefaultSwapHistoryWidgetEventTracker @Inject constructor(
    peraEventTracker: PeraEventTracker
) : BaseEventTracker(peraEventTracker), SwapHistoryWidgetEventTracker {

    override suspend fun logPairSelected(assetInName: String?, assetOutName: String?) {
        logEvent(PAIR_SELECTION_EVENT_NAME, mapOf(PAIR_KEY to "${assetInName}_$assetOutName"))
    }

    override suspend fun logSeeAllClick() {
        logEvent(SEE_ALL_EVENT_NAME)
    }

    private companion object {
        const val PAIR_SELECTION_EVENT_NAME = "swapscr_swap_history_select"
        const val SEE_ALL_EVENT_NAME = "swapscr_swap_history_see_all"
        const val PAIR_KEY = "swap_pairing"
    }
}
