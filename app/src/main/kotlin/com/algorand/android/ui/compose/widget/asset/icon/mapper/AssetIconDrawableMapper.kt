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

package com.algorand.android.ui.compose.widget.asset.icon.mapper

import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.TopSwapPairs

interface AssetIconDrawableMapper {
    fun map(assetLite: AssetLite): AssetIconDrawable
    fun map(assetDetail: SwapQuoteV2.AssetDetail): AssetIconDrawable
    fun map(assetDetail: TopSwapPairs.AssetDetail): AssetIconDrawable
    fun map(asset: Asset): AssetIconDrawable
    fun map(id: Long, logoUrl: String?, shortName: String?): AssetIconDrawable
}
