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

package com.algorand.wallet.asset.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.algorand.wallet.asset.data.database.model.AssetCategoryEntity
import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto
import com.algorand.wallet.asset.data.database.model.VerificationTierEntity

@Dao
internal interface PaginatedSwappableAssetDao {

    @Query(
        """
    SELECT 
        holding.algo_address AS algo_address,
        holding.amount AS amount,
        holding.asset_status AS asset_status,
        (holding.amount * IFNULL(asset.usd_value, 0)) AS total_usd_value,
        holding.opted_in_at_round AS opted_in_at_round,
        
        asset.asset_id AS asset_id,
        asset.name AS name,
        asset.unit_name AS unit_name,
        asset.logo_url AS logo_url,
        asset.usd_value AS usd_value,
        asset.decimals AS decimals,
        asset.verification_tier AS verification_tier,
        asset.is_favorite AS is_favorite,
        
        collectible.title AS title,
        collectible.primary_image_url AS primary_image_url,
        collectible.collection_name AS collection_name,
        collectible.media_type AS media_type
    FROM asset_detail AS asset
    INNER JOIN asset_holding_table AS holding 
        ON asset.asset_id = holding.asset_id
    LEFT JOIN collectible AS collectible 
        ON asset.asset_id = collectible.collectible_asset_id
    WHERE
        holding.algo_address = :address
        AND holding.amount != 0
        AND collectible.title IS NULL
        AND (
            :searchKeyword IS NULL OR
            CAST(asset.asset_id AS TEXT) LIKE '%' || :searchKeyword || '%' OR 
            asset.name LIKE '%' || :searchKeyword || '%' OR 
            asset.unit_name LIKE '%' || :searchKeyword || '%'
        )
        AND (
           asset.verification_tier IN (:verificationTiers) OR asset.category IN (:categories)
        )
    ORDER BY
        CASE
            WHEN asset.is_favorite = 1 THEN 0
            ELSE 1
        END ASC,
        COALESCE(asset.name, collectible.title) ASC
    """
    )
    fun getPaginatedAssetCollectibleItems(
        address: String,
        searchKeyword: String?,
        verificationTiers: List<VerificationTierEntity>,
        categories: List<AssetCategoryEntity>
    ): PagingSource<Int, PaginatedAssetCollectibleItemDto>
}
