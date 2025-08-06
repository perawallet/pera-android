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

package com.algorand.wallet.swap.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.algorand.wallet.swap.data.model.SwapSelectedAssetDto

@Dao
internal interface SwapSelectedAssetDao {

    @Query(
        """
    SELECT 
        asset_detail.unit_name AS unit_name,
        asset_detail.verification_tier AS verification_tier,
        COALESCE(asset_detail.logo_url, asset_detail.logo_svg_url) AS image_url,
        asset_holding_table.amount AS amount,
        asset_detail.decimals AS decimals,
        asset_detail.asset_id AS asset_id
    FROM asset_detail 
    LEFT JOIN asset_holding_table 
        ON asset_detail.asset_id = asset_holding_table.asset_id AND asset_holding_table.algo_address = :address
    WHERE asset_detail.asset_id = :assetId
    LIMIT 1
    """
    )
    suspend fun getAssetWithHolding(address: String, assetId: Long): SwapSelectedAssetDto?
}
