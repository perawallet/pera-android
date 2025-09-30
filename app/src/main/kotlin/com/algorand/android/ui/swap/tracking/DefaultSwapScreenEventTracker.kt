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

package com.algorand.android.ui.swap.tracking

import com.algorand.android.modules.tracking.core.BaseEventTracker
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import javax.inject.Inject

internal class DefaultSwapScreenEventTracker @Inject constructor(
    peraEventTracker: PeraEventTracker
) : BaseEventTracker(peraEventTracker), SwapScreenEventTracker {

    override suspend fun logAccountSelectionClick() {
        logEvent(ACCOUNT_SELECTION_EVENT_NAME)
    }

    override suspend fun logAssetInSelectionClick() {
        logEvent(ASSET_IN_SELECTION_EVENT_NAME)
    }

    override suspend fun logAssetInSelection(assetName: String) {
        logEvent(ASSET_IN_SELECTION_EVENT_NAME, mapOf(ASSET_NAME_KEY to assetName))
    }

    override suspend fun logAssetOutSelectionClick() {
        logEvent(ASSET_OUT_SELECTION_EVENT_NAME)
    }

    override suspend fun logAssetOutSelection(assetName: String) {
        logEvent(ASSET_OUT_SELECTION_EVENT_NAME, mapOf(ASSET_NAME_KEY to assetName))
    }

    override suspend fun logSelectProviderClick() {
        logEvent(SELECT_PROVIDER_EVENT_NAME)
    }

    override suspend fun logSwapButtonClick() {
        logEvent(SWAP_CLICK_EVENT_NAME)
    }

    override suspend fun logSettingsApplyClick() {
        logEvent(SETTINGS_APPLY_EVENT_NAME)
    }

    override suspend fun logSettingsCancelClick() {
        logEvent(SETTINGS_CLOSE_EVENT_NAME)
    }

    override suspend fun logBalancePercentageSelection(percentage: Float) {
        logEvent(SETTINGS_BALANCE_PERCENTAGE_SELECTED_EVENT_NAME, mapOf(PERCENTAGE_KEY to percentage))
    }

    override suspend fun logSlippageSelection(percentage: Float) {
        logEvent(SETTINGS_SLIPPAGE_PERCENTAGE_SELECTED_EVENT_NAME, mapOf(PERCENTAGE_KEY to percentage))
    }

    override suspend fun logProviderApplyClick() {
        logEvent(PROVIDER_APPLY_EVENT_NAME)
    }

    override suspend fun logProviderSelection(providerName: String) {
        logEvent(PROVIDER_SELECTION_EVENT_NAME, mapOf(PROVIDER_KEY to providerName))
    }

    override suspend fun logProviderCancelClick() {
        logEvent(PROVIDER_CLOSE_EVENT_NAME)
    }

    override suspend fun logLocalCurrencyEnabled() {
        logEvent(LOCAL_CURRENCY_ENABLED_EVENT_NAME)
    }

    override suspend fun logLocalCurrencyDisabled() {
        logEvent(LOCAL_CURRENCY_DISABLED_EVENT_NAME)
    }

    private companion object {
        const val ACCOUNT_SELECTION_EVENT_NAME = "swapscr_account_select_open"
        const val ASSET_IN_SELECTION_EVENT_NAME = "swapscr_asset_top_select"
        const val ASSET_OUT_SELECTION_EVENT_NAME = "swapscr_asset_bot_select"
        const val SELECT_PROVIDER_EVENT_NAME = "swapscr_swap_select_provider"
        const val SWAP_CLICK_EVENT_NAME = "swapscr_assets_swap"
        const val SETTINGS_CLOSE_EVENT_NAME = "swapscr_swap_settings_close"
        const val SETTINGS_APPLY_EVENT_NAME = "swapscr_swap_settings_apply"
        const val SETTINGS_BALANCE_PERCENTAGE_SELECTED_EVENT_NAME = "swapscr_swap_settings_balance_percent"
        const val SETTINGS_SLIPPAGE_PERCENTAGE_SELECTED_EVENT_NAME = "swapscr_swap_settings_slippage_percent"
        const val PROVIDER_APPLY_EVENT_NAME = "swapscr_swap_select_provider_apply"
        const val PROVIDER_CLOSE_EVENT_NAME = "swapscr_swap_select_provider_close"
        const val PROVIDER_SELECTION_EVENT_NAME = "swapscr_swap_select_provider_router"
        const val LOCAL_CURRENCY_ENABLED_EVENT_NAME = "swapscr_swap_settings_local_currency_on"
        const val LOCAL_CURRENCY_DISABLED_EVENT_NAME = "swapscr_swap_settings_local_currency_off"

        const val ASSET_NAME_KEY = "asset_name"
        const val PERCENTAGE_KEY = "percentage"
        const val PROVIDER_KEY = "router_name"
    }
}
