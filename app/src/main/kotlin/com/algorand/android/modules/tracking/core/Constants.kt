package com.algorand.android.modules.tracking.core

internal object PeraClickEvent {
    const val TAP_TAB_SEND = "tap_tab_send"
    const val TAP_TAB_RECEIVE = "tap_tab_receive"

    const val TAP_HOME_SCREEN_SWAP = "homescr_swap_tap"
    const val TAP_HOME_SCREEN_STAKE = "homescr_stake_tap"
    const val TAP_HOME_SCREEN_SEND = "homescr_send_tap"
    const val TAP_HOME_SCREEN_SORT = "homescr_sort_tap"

    const val TAP_LOWERMENU_HOME = "lowermenu_home_tap"
    const val TAP_LOWERMENU_DISCOVER = "lowermenu_discover_tap"
    const val TAP_LOWERMENU_PERA = "lowermenu_pera_tap"
    const val TAP_LOWERMENU_NFTS = "lowermenu_nfts_tap"
    const val TAP_LOWERMENU_SETTINGS = "lowermenu_settings_tap"

    const val TAP_BOTTOM_NAVIGATION_BROWSE_DAPPS = "bottommenu_browse_dapps_tap"
    const val TAP_BOTTOM_NAVIGATION_BUY_ALGO = "bottommenu_algo_buy_tap"
    const val TAP_BOTTOM_NAVIGATION_CARDS = "bottommenu_cards_tap"
    const val TAP_BOTTOM_NAVIGATION_QR_SCAN = "bottommenu_qr_scan_tap"
    const val TAP_BOTTOM_NAVIGATION_SWAP = "bottommenu_swap_tap"
    const val TAP_BOTTOM_NAVIGATION_STAKE = "bottommenu_stake_tap"

    const val TAP_ACCOUNT_SCREEN_ASSET_INBOX = "accountscr_tapmenu_asset_inbox_tap"
    const val TAP_ACCOUNT_SCREEN_SEND = "accountscr_tapmenu_send_tap"
    const val TAP_ACCOUNT_SCREEN_SWAP = "accountscr_tapmenu_swap_tap"
    const val TAP_ACCOUNT_SCREEN_MORE = "accountscr_tapmenu_more_tap"
}

internal object PeraEvent {
    const val HOME_SCREEN_QR_SCAN = "homescr_qr_scan"
    const val SWAP_SELECT_ASSET_TOP = "swapscr_select_top_asset_tap"
    const val SWAP_SELECT_ASSET_LOWER = "swapscr_select_lower_asset_tap"
}
