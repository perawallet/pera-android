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

package com.algorand.wallet.remoteconfig.domain.model

enum class FeatureToggle(val key: String, val description: String) {
    STAKING("enable_staking", "Staking"),
    HD_WALLET("enable_hd_wallet", "Hd Wallet"),
    DISCOVER_V5("enable_discover_v5", "Discover V5"),
    ACCOUNT_DB_MIGRATION_VIEWER("enable_account_migration_viewer", "Account Migration Viewer"),
    ACCOUNTS_CHART("enable_charts_portfolio", "Portfolio Chart"),
    ACCOUNT_DETAIL_CHART("enable_charts_accounts", "Account Detail Chart"),
    ASSET_DETAIL_CHART("enable_charts_assets", "Asset Detail Chart"),
    SWAP_V2("enable_swap_v2", "Swap v2"),
    LIQUID_AUTH("enable_liquid_auth", "Liquid Auth"),
    LEDGER_DEFLEX_FILTER("enable_ledger_deflex_filter", "Ledger Deflex Filter"),
    ASSET_DETAIL_V2("enable_asset_detail_v2", "Asset Detail V2")
}
