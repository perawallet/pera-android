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
import javax.inject.Inject

internal class DefaultAccountAssetsEventTracker @Inject constructor(
    private val peraAnalyticsEventTracker: PeraAnalyticsEventTracker
) : AccountAssetsEventTracker {

    override fun logChartClick() {
        peraAnalyticsEventTracker.logEvent(CHART_CLICK)
    }

    override fun logSwapClick() {
        peraAnalyticsEventTracker.logEvent(SWAP_CLICK)
    }

    override fun logBuyAlgoClick() {
        peraAnalyticsEventTracker.logEvent(BUY_SELL_CLICK)
    }

    override fun logAssetInboxClick() {
        peraAnalyticsEventTracker.logEvent(ASSET_INBOX_CLICK)
    }

    override fun logMoreClick() {
        peraAnalyticsEventTracker.logEvent(MORE_CLICK)
    }

    override fun logAddAssetClick() {
        peraAnalyticsEventTracker.logEvent(ADD_ASSET_CLICK)
    }

    override fun logManageAssetsClick() {
        peraAnalyticsEventTracker.logEvent(MANAGE_ASSETS_CLICK)
    }

    private companion object {
        const val CHART_CLICK = "accountscr_chart_tap"
        const val SWAP_CLICK = "accountscr_swap_click"
        const val BUY_SELL_CLICK = "acccountscr_buysell_click"
        const val ASSET_INBOX_CLICK = "accountscr_tapmenu_asset_inbox_tap"
        const val MORE_CLICK = "accountscr_tapmenu_more_tap"
        const val ADD_ASSET_CLICK = "assetscr_asset_add"
        const val MANAGE_ASSETS_CLICK = "assetscr_assets_manage"
    }
}
