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

package com.algorand.android.ui.menu.tracker

import com.algorand.wallet.analytics.tracking.domain.tracker.PeraAnalyticsEventTracker
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DefaultMenuEventTrackerTest {

    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker = mockk(relaxed = true)

    private val sut = DefaultMenuEventTracker(peraAnalyticsEventTracker)

    @Test
    fun `EXPECT menu click to be logged`() {
        sut.logQrScanClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_qr_scan") }
    }

    @Test
    fun `EXPECT settings click to be logged`() {
        sut.logSettingsClick()

        verify { peraAnalyticsEventTracker.logEvent("lowermenu_settings_tap") }
    }

    @Test
    fun `EXPECT create card click to be logged`() {
        sut.logCreateCardClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_create_card_tap") }
    }

    @Test
    fun `EXPECT go to cards click to be logged`() {
        sut.logGoToCardsClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_cards_tap") }
    }

    @Test
    fun `EXPECT collectibles click to be logged`() {
        sut.logCollectiblesClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_nfts_tap") }
    }

    @Test
    fun `EXPECT transfer click to be logged`() {
        sut.logTransferClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_transfer_tap") }
    }

    @Test
    fun `EXPECT buy algo click to be logged`() {
        sut.logBuyAlgoClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_buyalgo_tap") }
    }

    @Test
    fun `EXPECT receive click to be logged`() {
        sut.logReceiveClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_receive_tap") }
    }

    @Test
    fun `EXPECT invite friends click to be logged`() {
        sut.logInviteFriendsClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_invite_friends_tap") }
    }
}
