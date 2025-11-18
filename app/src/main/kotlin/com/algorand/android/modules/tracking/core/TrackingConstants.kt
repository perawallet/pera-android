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

package com.algorand.android.modules.tracking.core

internal object PeraClickEvent {

    const val TAP_MELD_SCREEN_ALGO_SELECT_WALLET = "meldscr_algo_select_wallet_tap"
    const val TAP_BIDALI_SCREEN_ALGO_SELL = "bidscr_algo_sell_tap"

    const val TAP_ONBOARDING_WELCOME_SKIP = "onb_welcome_skip_tap"
    const val TAP_ONBOARDING_WELCOME_WATCH = "onb_welcome_watch_tap"
    const val TAP_ONBOARDING_CREATE_WALLET = "onb_create_wallet_tap"
    const val TAP_ONBOARDING_CREATE_ACCOUNT = "onb_create_account_tap"
    const val TAP_ONBOARDING_CREATE_PASSPHRASE_SKIP = "onb_create_pass_skip_tap"
    const val TAP_ONBOARDING_WRITE_PASSPHRASE_SKIP = "onb_write_pass_skip_tap"
    const val TAP_ONBOARDING_RECOVER_PASSPHRASE_SKIP = "onb_rev_pass_skip_tap"
    const val TAP_ONBOARDING_RECOVER_UNIVERSAL = "onb_createacc_recover_24"
    const val TAP_ONBOARDING_RECOVER_ALGO25 = "onb_createacc_recover_25"
}

internal object PeraEvent {

    const val ONBOARDING_PASSPHRASE_VERIFIED_COMPLETE = "onb_pass_verified_complete"
    const val ONBOARDING_NAME_WALLET_COMPLETE = "onb_name_wallet_complete"
    const val ONBOARDING_WATCH_COMPLETE = "onb_watch_complete"
}
