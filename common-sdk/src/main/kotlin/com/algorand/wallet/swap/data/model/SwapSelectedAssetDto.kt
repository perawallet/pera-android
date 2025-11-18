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

package com.algorand.wallet.swap.data.model

import androidx.room.ColumnInfo
import com.algorand.wallet.asset.data.database.model.VerificationTierEntity
import java.math.BigDecimal
import java.math.BigInteger

internal data class SwapSelectedAssetDto(
    @ColumnInfo("asset_id")
    val assetId: Long?,

    @ColumnInfo("unit_name")
    val unitName: String?,

    @ColumnInfo("verification_tier")
    val verificationTier: VerificationTierEntity?,

    @ColumnInfo("image_url")
    val imageUrl: String?,

    @ColumnInfo("amount")
    val assetHoldingAmount: BigInteger?,

    @ColumnInfo("decimals")
    val decimal: Int?,

    @ColumnInfo("usd_value")
    val usdValue: BigDecimal?
)
