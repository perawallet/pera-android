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
import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto

@Dao
internal interface PaginatedAssetCollectibleDao {

    @Query(
        """
    SELECT 
        holding.algo_address AS algo_address,
        holding.amount AS amount,
        holding.asset_status AS asset_status,
        (holding.amount / ('1e' || IFNULL(asset.decimals, 0))) * IFNULL(asset.usd_value, 0) AS total_usd_value,
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
        collectible.media_type AS media_type,
        LOWER(COALESCE(asset.name, collectible.title)) AS sort_by_name_value
    FROM asset_detail AS asset
    INNER JOIN asset_holding_table AS holding 
        ON asset.asset_id = holding.asset_id
        AND holding.asset_id NOT IN (:excludedAssetIds)
        AND (
            (:filterOutZeroAmount = 0 AND :filterOutNonZeroAmount = 0) OR
            (:filterOutZeroAmount = 1 AND holding.amount != 0) OR
            (:filterOutNonZeroAmount = 1 AND holding.amount = 0)
        )
    LEFT JOIN collectible AS collectible 
        ON asset.asset_id = collectible.collectible_asset_id
    WHERE
        holding.algo_address IN (:addressList)
    AND
        (:searchKeyword IS NULL OR
         asset.asset_id LIKE '%' || :searchKeyword || '%' OR 
         asset.name LIKE '%' || :searchKeyword || '%' OR 
         collectible.title LIKE '%' || :searchKeyword || '%')
    AND
        (:filterOutCollectibles = 0 OR collectible.title IS NULL)
    AND
        (:filterOutCollectiblesWithZeroAmount = 0 OR NOT (collectible.title IS NOT NULL AND holding.amount = 0))
    ORDER BY
        CASE 
            WHEN holding.asset_status IN ('PENDING_FOR_ADDITION', 'PENDING_FOR_REMOVAL') THEN 0
            WHEN asset.is_favorite = 1 THEN 1
            ELSE 2
        END ASC,
        CASE WHEN :sortType = 'name_asc' THEN sort_by_name_value END ASC,
        CASE WHEN :sortType = 'name_desc' THEN sort_by_name_value END DESC,
        CASE WHEN :sortType = 'value_asc' THEN total_usd_value END ASC,
        CASE WHEN :sortType = 'value_desc' THEN total_usd_value END DESC,
        sort_by_name_value ASC,
        asset.asset_id ASC
"""
    )
    fun getPaginatedAssetCollectibleItems(
        addressList: List<String>,
        searchKeyword: String?,
        filterOutZeroAmount: Boolean,
        filterOutNonZeroAmount: Boolean,
        filterOutCollectibles: Boolean,
        filterOutCollectiblesWithZeroAmount: Boolean,
        sortType: String,
        excludedAssetIds: List<Long>,
    ): PagingSource<Int, PaginatedAssetCollectibleItemDto>
}
