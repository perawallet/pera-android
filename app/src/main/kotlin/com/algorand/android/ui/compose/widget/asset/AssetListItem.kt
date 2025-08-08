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

package com.algorand.android.ui.compose.widget.asset

import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable

data class AssetListItem(
    val assetId: Long,
    val name: String?,
    val unitName: String?,
    val balance: Balance,
    val verificationTier: VerificationTierConfiguration,
    val assetIcon: AssetIconDrawable
) {

    data class Balance(
        val amount: PeraAmount,
        val usdValue: PeraAmount?,
        val primaryAmountRenderer: AmountRenderer,
        val secondaryAmountRenderer: AmountRenderer
    )
}
