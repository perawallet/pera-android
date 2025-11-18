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

package com.algorand.android.ui.asset.lite.mapper

import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.wallet.account.lite.domain.model.AssetHoldingLite
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.swap.domain.model.AvailableSwapAsset

interface AssetListItemMapper {
    operator fun invoke(assetLite: AssetLite): AssetListItem
    operator fun invoke(assetHoldings: AssetHoldingLite, asset: AvailableSwapAsset, isFavorite: Boolean?): AssetListItem
}
