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
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DefaultBottomNavigationEventTrackerTest {

    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker = mockk(relaxed = true)

    private val sut = DefaultBottomNavigationEventTracker(peraAnalyticsEventTracker)

    @Test
    fun `EXPECT home click to be logged`() {
        sut.logHomeClick()

        verify { peraAnalyticsEventTracker.logEvent("lowermenu_home_tap") }
    }

    @Test
    fun `EXPECT discover click to be logged`() {
        sut.logDiscoverClick()

        verify { peraAnalyticsEventTracker.logEvent("lowermenu_discover_tap") }
    }

    @Test
    fun `EXPECT swap click to be logged`() {
        sut.logSwapClick()

        verify { peraAnalyticsEventTracker.logEvent("lowermenu_swap_tap") }
    }

    @Test
    fun `EXPECT collectibles click to be logged`() {
        sut.logCollectiblesClick()

        verify { peraAnalyticsEventTracker.logEvent("lowermenu_nfts_tap") }
    }

    @Test
    fun `EXPECT stake click to be logged`() {
        sut.logStakeClick()

        verify { peraAnalyticsEventTracker.logEvent("lowermenu_stake_tap") }
    }

    @Test
    fun `EXPECT menu click to be logged`() {
        sut.logMenuClick()

        verify { peraAnalyticsEventTracker.logEvent("lowermenu_menu_tap") }
    }
}
