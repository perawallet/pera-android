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

package com.algorand.android.ui.main.tracker

import com.algorand.wallet.analytics.tracking.domain.tracker.PeraAnalyticsEventTracker
import javax.inject.Inject

internal class DefaultBottomNavigationEventTracker @Inject constructor(
    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker
) : BottomNavigationEventTracker {

    override fun logHomeClick() {
        peraAnalyticsEventTracker.logEvent(HOME_CLICK)
    }

    override fun logDiscoverClick() {
        peraAnalyticsEventTracker.logEvent(DISCOVER_CLICK)
    }

    override fun logSwapClick() {
        peraAnalyticsEventTracker.logEvent(SWAP_CLICK)
    }

    override fun logCollectiblesClick() {
        peraAnalyticsEventTracker.logEvent(COLLECTIBLES_CLICK)
    }

    override fun logStakeClick() {
        peraAnalyticsEventTracker.logEvent(STAKE_CLICK)
    }

    override fun logMenuClick() {
        peraAnalyticsEventTracker.logEvent(MENU_CLICK)
    }

    private companion object EventNames {
        const val HOME_CLICK = "lowermenu_home_tap"
        const val DISCOVER_CLICK = "lowermenu_discover_tap"
        const val SWAP_CLICK = "lowermenu_swap_tap"
        const val STAKE_CLICK = "lowermenu_stake_tap"
        const val MENU_CLICK = "lowermenu_menu_tap"
        const val COLLECTIBLES_CLICK = "lowermenu_nfts_tap"
    }
}
