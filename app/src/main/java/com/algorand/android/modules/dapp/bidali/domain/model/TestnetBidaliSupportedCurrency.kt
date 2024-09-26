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

package com.algorand.android.modules.dapp.bidali.domain.model

import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.component.AssetConstants.USDC_TESTNET_ID

enum class TestnetBidaliSupportedCurrency(
    override val key: String,
    override val assetId: Long
) : BidaliSupportedCurrency {
    ALGORAND("algorand", ALGO_ASSET_ID),
    USDC("testusdcalgorand", USDC_TESTNET_ID)
}
