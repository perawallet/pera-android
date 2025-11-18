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

package com.algorand.android.ui.accounts.tracker

import com.algorand.wallet.analytics.tracking.domain.tracker.PeraAnalyticsEventTracker
import com.algorand.wallet.banner.domain.model.Banner
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DefaultAccountsEventTrackerTest {

    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker = mockk(relaxed = true)

    private val sut = DefaultAccountsEventTracker(peraAnalyticsEventTracker)

    @Test
    fun `EXPECT add account click to be logged`() {
        sut.logAddAccountClick()

        verify { peraAnalyticsEventTracker.logEvent("homescr_account_add") }
    }

    @Test
    fun `EXPECT swap tutorial try swap click to be logged`() {
        sut.logSwapTutorialTrySwapClick()

        verify { peraAnalyticsEventTracker.logEvent("banner_swap_tryswap") }
    }

    @Test
    fun `EXPECT swap later click to be logged`() {
        sut.logSwapLaterClick()

        verify { peraAnalyticsEventTracker.logEvent("banner_swap_later") }
    }

    @Test
    fun `EXPECT chart tap to be logged`() {
        sut.logChartTap()

        verify { peraAnalyticsEventTracker.logEvent("homescr_chart_tap") }
    }

    @Test
    fun `EXPECT swap quick action click to be logged`() {
        sut.logSwapQuickActionClick()

        verify { peraAnalyticsEventTracker.logEvent("homescr_swap_click") }
    }

    @Test
    fun `EXPECT buy sell quick action click to be logged`() {
        sut.logBuySellQuickActionClick()

        verify { peraAnalyticsEventTracker.logEvent("homescr_buysell_click") }
    }

    @Test
    fun `EXPECT stake quick action click to be logged`() {
        sut.logStakeQuickActionClick()

        verify { peraAnalyticsEventTracker.logEvent("homescr_stake_click") }
    }

    @Test
    fun `EXPECT send quick action click to be logged`() {
        sut.logSendQuickActionClick()

        verify { peraAnalyticsEventTracker.logEvent("homescr_send_click") }
    }

    @Test
    fun `EXPECT spot banner click to be logged`() {
        sut.logSpotBannerClick("banner title")

        verify {
            peraAnalyticsEventTracker.logEvent(
                "homescr_banner_click",
                mapOf("banner_name" to "banner title")
            )
        }
    }

    @Test
    fun `EXPECT spot banner dismiss click to be logged`() {
        sut.logSpotBannerDismissClick("banner title")

        verify {
            peraAnalyticsEventTracker.logEvent(
                "homescr_banner_close_click",
                eq(mapOf("banner_name" to "banner title"))
            )
        }
    }

    @Test
    fun `EXPECT sort click to be logged`() {
        sut.logSortClick()

        verify { peraAnalyticsEventTracker.logEvent("homescr_sort_tap") }
    }

    @Test
    fun `EXPECT QR scan click to be logged`() {
        sut.logQrScanClick()

        verify { peraAnalyticsEventTracker.logEvent("homescr_qr_scan") }
    }

    @Test
    fun `EXPECT notification click to be logged`() {
        sut.logNotificationClick()

        verify { peraAnalyticsEventTracker.logEvent("homescr_notification_tap") }
    }

    @Test
    fun `EXPECT card banner click to be logged`() {
        sut.logBannerClick(Banner.BannerType.Card)

        verify { peraAnalyticsEventTracker.logEvent("homescr_visitcard") }
    }

    @Test
    fun `EXPECT governance banner click to be logged`() {
        sut.logBannerClick(Banner.BannerType.Governance)

        verify { peraAnalyticsEventTracker.logEvent("homescr_visitgovernance") }
    }

    @Test
    fun `EXPECT staking banner click to be logged`() {
        sut.logBannerClick(Banner.BannerType.Staking)

        verify { peraAnalyticsEventTracker.logEvent("homescr_visitstaking") }
    }

    @Test
    fun `EXPECT retail banner click to be logged`() {
        sut.logBannerClick(Banner.BannerType.Retail)

        verify { peraAnalyticsEventTracker.logEvent("homescr_visitretail") }
    }

    @Test
    fun `EXPECT generic banner click to be logged`() {
        sut.logBannerClick(Banner.BannerType.Generic)

        verify { peraAnalyticsEventTracker.logEvent("homescr_visitgeneric") }
    }
}
