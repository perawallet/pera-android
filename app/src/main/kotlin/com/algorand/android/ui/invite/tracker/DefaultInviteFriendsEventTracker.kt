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
import javax.inject.Inject

internal class DefaultInviteFriendsEventTracker @Inject constructor(
    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker
) : InviteFriendsEventTracker {

    override fun logShareClick() {
        peraAnalyticsEventTracker.logEvent(SHARE_CLICK)
    }

    override fun logCloseClick() {
        peraAnalyticsEventTracker.logEvent(CLOSE_CLICK)
    }

    private companion object {
        const val SHARE_CLICK = "menuscr_invite_share_tap"
        const val CLOSE_CLICK = "menuscr_invite_close_tap"
    }
}
