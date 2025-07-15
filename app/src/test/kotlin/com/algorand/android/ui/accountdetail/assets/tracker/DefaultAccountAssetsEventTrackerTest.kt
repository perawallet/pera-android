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

package com.algorand.android.ui.accountdetail.assets.tracker

import com.algorand.wallet.analytics.tracking.domain.tracker.PeraAnalyticsEventTracker
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DefaultAccountAssetsEventTrackerTest {

    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker = mockk(relaxed = true)

    private val sut = DefaultAccountAssetsEventTracker(peraAnalyticsEventTracker)

    @Test
    fun `EXPECT chart click to be logged`() {
        sut.logChartTap()

        verify { peraAnalyticsEventTracker.logEvent("accountscr_chart_tap") }
    }

    @Test
    fun `EXPECT swap click to be logged`() {
        sut.logSwapClick()

        verify { peraAnalyticsEventTracker.logEvent("accountscr_swap_click") }
    }

    @Test
    fun `EXPECT buy algo click to be logged`() {
        sut.logBuyAlgoClick()

        verify { peraAnalyticsEventTracker.logEvent("acccountscr_buysell_click") }
    }

    @Test
    fun `EXPECT asset inbox click to be logged`() {
        sut.logAssetInboxClick()

        verify { peraAnalyticsEventTracker.logEvent("accountscr_tapmenu_asset_inbox_tap") }
    }

    @Test
    fun `EXPECT more click to be logged`() {
        sut.logMoreClick()

        verify { peraAnalyticsEventTracker.logEvent("accountscr_tapmenu_more_tap") }
    }

    @Test
    fun `EXPECT add asset click to be logged`() {
        sut.logAddAssetClick()

        verify { peraAnalyticsEventTracker.logEvent("assetscr_asset_add") }
    }

    @Test
    fun `EXPECT manage assets click to be logged`() {
        sut.logManageAssetsClick()

        verify { peraAnalyticsEventTracker.logEvent("assetscr_assets_manage") }
    }
}
