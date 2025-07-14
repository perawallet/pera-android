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
import javax.inject.Inject

internal class DefaultAccountsEventTracker @Inject constructor(
    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker
) : AccountsEventTracker {

    override fun logAddAccountClick() {
        peraAnalyticsEventTracker.logEvent(ADD_ACCOUNT_CLICK)
    }

    override fun logSwapTutorialTrySwapClick() {
        peraAnalyticsEventTracker.logEvent(TRY_SWAP_CLICK)
    }

    override fun logSwapLaterClick() {
        peraAnalyticsEventTracker.logEvent(SWAP_TRY_LATER_CLICK)
    }

    override fun logChartTap() {
        peraAnalyticsEventTracker.logEvent(CHART_TAP)
    }

    override fun logSwapQuickActionClick() {
        peraAnalyticsEventTracker.logEvent(SWAP_QUICK_ACTION_CLICK)
    }

    override fun logBuySellQuickActionClick() {
        peraAnalyticsEventTracker.logEvent(BUY_SELL_QUICK_ACTION_CLICK)
    }

    override fun logStakeQuickActionClick() {
        peraAnalyticsEventTracker.logEvent(STAKE_QUICK_ACTION_CLICK)
    }

    override fun logSendQuickActionClick() {
        peraAnalyticsEventTracker.logEvent(SEND_QUICK_ACTION_CLICK)
    }

    override fun logSpotBannerClick(title: String) {
        peraAnalyticsEventTracker.logEvent(SPOT_BANNER_CLICK, mapOf(SPOT_BANNER_NAME_KEY to title))
    }

    override fun logSpotBannerDismissClick(title: String) {
        peraAnalyticsEventTracker.logEvent(SPOT_BANNER_DISMISS_CLICK, mapOf(SPOT_BANNER_NAME_KEY to title))
    }

    override fun logSortClick() {
        peraAnalyticsEventTracker.logEvent(SORT_CLICK)
    }

    override fun logQrScanClick() {
        peraAnalyticsEventTracker.logEvent(QR_SCAN_CLICK)
    }

    override fun logNotificationClick() {
        peraAnalyticsEventTracker.logEvent(NOTIFICATION_CLICK)
    }

    override fun logBannerClick(bannerType: Banner.BannerType) {
        val eventName = when (bannerType) {
            Banner.BannerType.Card -> LARGE_BANNER_CARD_CLICK
            Banner.BannerType.Generic -> LARGE_BANNER_GENERIC_CLICK
            Banner.BannerType.Governance -> LARGE_BANNER_GOVERNANCE_CLICK
            Banner.BannerType.Staking -> LARGE_BANNER_STAKING_CLICK
        }
        peraAnalyticsEventTracker.logEvent(eventName)
    }

    private companion object EventNames {
        const val ADD_ACCOUNT_CLICK = "homescr_account_add"
        const val CHART_TAP = "homescr_chart_tap"
        const val SWAP_QUICK_ACTION_CLICK = "homescr_swap_click"
        const val SWAP_TRY_LATER_CLICK = "banner_swap_later"
        const val TRY_SWAP_CLICK = "banner_swap_tryswap"
        const val BUY_SELL_QUICK_ACTION_CLICK = "homescr_buysell_click"
        const val STAKE_QUICK_ACTION_CLICK = "homescr_stake_click"
        const val SEND_QUICK_ACTION_CLICK = "homescr_send_click"
        const val SPOT_BANNER_CLICK = "homescr_banner_click"
        const val SPOT_BANNER_DISMISS_CLICK = "homescr_banner_close_click"
        const val SORT_CLICK = "homescr_sort_tap"
        const val QR_SCAN_CLICK = "homescr_qr_scan"
        const val NOTIFICATION_CLICK = "homescr_notification_tap"
        const val LARGE_BANNER_STAKING_CLICK = "homescr_visitstaking"
        const val LARGE_BANNER_GOVERNANCE_CLICK = "homescr_visitgovernance"
        const val LARGE_BANNER_CARD_CLICK = "homescr_visitcard"
        const val LARGE_BANNER_GENERIC_CLICK = "homescr_visitgeneric"
        const val SPOT_BANNER_NAME_KEY = "banner_name"
    }
}
