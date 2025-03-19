/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.remoteconfig.domain.usecase

const val IMMERSVE_BUTTON_TOGGLE = "enable_immersve"
const val STAKING_BUTTON_TOGGLE = "enable_staking"
const val HD_WALLET_BUTTON_TOGGLE = "enable_hd_wallet"
const val ENABLE_ACCOUNT_DB_MIGRATION_VIEWER = "enable_account_migration_viewer"

fun interface IsFeatureToggleEnabled {
    operator fun invoke(featureToggleKey: String): Boolean
}
