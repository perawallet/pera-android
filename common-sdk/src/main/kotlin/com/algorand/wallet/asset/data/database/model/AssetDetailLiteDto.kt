/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.data.database.model

import androidx.room.ColumnInfo
import java.math.BigDecimal

internal data class AssetDetailLiteDto(
    @ColumnInfo(name = "asset_id")
    val id: Long,
    @ColumnInfo("name")
    val name: String?,
    @ColumnInfo("unit_name")
    val unitName: String?,
    @ColumnInfo("asset_creator_address")
    val assetCreatorAddress: String?,
    @ColumnInfo("logo_url")
    val logoUrl: String?,
    @ColumnInfo("usd_value")
    val usdValue: BigDecimal?,
    @ColumnInfo("decimals")
    val decimals: Int
)
