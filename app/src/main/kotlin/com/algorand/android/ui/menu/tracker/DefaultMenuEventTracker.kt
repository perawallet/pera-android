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
import javax.inject.Inject

internal class DefaultMenuEventTracker @Inject constructor(
    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker
) : MenuEventTracker {

    override fun logQrScanClick() {
        peraAnalyticsEventTracker.logEvent(QR_SCAN_CLICK)
    }

    override fun logSettingsClick() {
        peraAnalyticsEventTracker.logEvent(SETTINGS_CLICK)
    }

    override fun logCreateCardClick() {
        peraAnalyticsEventTracker.logEvent(CREATE_CARD_CLICK)
    }

    override fun logGoToCardsClick() {
        peraAnalyticsEventTracker.logEvent(GO_TO_CARD_CLICK)
    }

    override fun logCollectiblesClick() {
        peraAnalyticsEventTracker.logEvent(COLLECTIBLES_CLICK)
    }

    override fun logTransferClick() {
        peraAnalyticsEventTracker.logEvent(TRANSFER_CLICK)
    }

    override fun logBuyAlgoClick() {
        peraAnalyticsEventTracker.logEvent(BUY_ALGO_CLICK)
    }

    override fun logReceiveClick() {
        peraAnalyticsEventTracker.logEvent(RECEIVE_CLICK)
    }

    override fun logInviteFriendsClick() {
        peraAnalyticsEventTracker.logEvent(INVITE_FRIENDS_CLICK)
    }

    private companion object EventNames {
        const val QR_SCAN_CLICK = "menuscr_qr_scan"
        const val SETTINGS_CLICK = "lowermenu_settings_tap"
        const val CREATE_CARD_CLICK = "menuscr_create_card_tap"
        const val GO_TO_CARD_CLICK = "menuscr_cards_tap"
        const val COLLECTIBLES_CLICK = "menuscr_nfts_tap"
        const val TRANSFER_CLICK = "menuscr_transfer_tap"
        const val BUY_ALGO_CLICK = "menuscr_buyalgo_tap"
        const val RECEIVE_CLICK = "menuscr_receive_tap"
        const val INVITE_FRIENDS_CLICK = "menuscr_invite_friends_tap"
    }
}
