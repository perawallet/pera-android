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

package com.algorand.android.ui.invite.tracker

import com.algorand.wallet.analytics.tracking.domain.tracker.PeraAnalyticsEventTracker
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class DefaultInviteFriendsEventTrackerTest {

    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker = mockk(relaxed = true)

    private val sut = DefaultInviteFriendsEventTracker(peraAnalyticsEventTracker)

    @Test
    fun `EXPECT share click to be logged`() {
        sut.logShareClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_invite_share_tap") }
    }

    @Test
    fun `EXPECT close click to be logged`() {
        sut.logCloseClick()

        verify { peraAnalyticsEventTracker.logEvent("menuscr_invite_close_tap") }
    }
}
