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

package com.algorand.wallet.asset.collectible.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import com.algorand.wallet.asset.collectible.data.database.model.FilteredCollectibleCountDto
import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
internal interface PaginatedCollectibleDao {

    @Query(
        """
       SELECT 
        holding.algo_address AS algo_address,
        holding.amount AS amount,
        holding.asset_status AS asset_status,
        (holding.amount * IFNULL(asset.usd_value, 0)) AS total_usd_value,
        
        asset.asset_id AS asset_id,
        asset.name AS name,
        asset.unit_name AS unit_name,
        asset.logo_url AS logo_url,
        asset.usd_value AS usd_value,
        asset.decimals AS decimals,
        asset.verification_tier AS verification_tier,
        
        collectible.title AS title,
        collectible.primary_image_url AS primary_image_url,
        collectible.collection_name AS collection_name,
        collectible.media_type AS media_type,
        holding.opted_in_at_round AS opted_in_at_round
    FROM collectible
    INNER JOIN asset_detail AS asset
        ON collectible.collectible_asset_id = asset.asset_id
    INNER JOIN asset_holding_table AS holding
        ON asset.asset_id = holding.asset_id
    WHERE
        holding.algo_address IN (:addressList)
    AND
        (:searchKeyword IS NULL OR
        asset.asset_id LIKE '%' || :searchKeyword || '%' OR 
        asset.name LIKE '%' || :searchKeyword || '%' OR 
        collectible.title LIKE '%' || :searchKeyword || '%')
    AND
        (:filterOutZeroAmount = 0 OR holding.amount != 0)
    ORDER BY
        CASE 
            WHEN holding.asset_status IN ('PENDING_FOR_ADDITION', 'PENDING_FOR_REMOVAL') THEN 0
            ELSE 1
        END ASC,
        CASE WHEN :sortType = 'name_asc' THEN COALESCE(asset.name, collectible.title) END ASC,
        CASE WHEN :sortType = 'name_desc' THEN COALESCE(asset.name, collectible.title) END DESC,
        CASE WHEN :sortType = 'optin_asc' THEN holding.opted_in_at_round END ASC,
        CASE WHEN :sortType = 'optin_desc' THEN holding.opted_in_at_round END DESC
        """
    )
    fun getPaginatedAssetCollectibleItems(
        addressList: List<String>,
        searchKeyword: String?,
        filterOutZeroAmount: Boolean,
        sortType: String
    ): PagingSource<Int, PaginatedAssetCollectibleItemDto>

    @Query(
        """
        SELECT COUNT(*) AS total_count,
        SUM(
            CASE
                WHEN :filterOutZeroAmount = 1 AND holding.amount = 0 THEN 1
                ELSE 0
            END
        ) AS filtered_out_count,

        SUM(
            CASE
                WHEN (:searchKeyword IS NULL OR
                      asset.asset_id LIKE '%' || :searchKeyword || '%' OR 
                      asset.name LIKE '%' || :searchKeyword || '%' OR 
                      collectible.title LIKE '%' || :searchKeyword || '%')
                 AND (:filterOutZeroAmount = 0 OR holding.amount != 0)
                THEN 1
                ELSE 0
            END
        ) AS filtered_and_search_count
    FROM collectible
    INNER JOIN asset_detail AS asset
        ON collectible.collectible_asset_id = asset.asset_id
    INNER JOIN asset_holding_table AS holding
        ON asset.asset_id = holding.asset_id
    WHERE holding.algo_address IN (:addressList)
    """
    )
    fun getFilteredCollectibleCount(
        addressList: List<String>,
        searchKeyword: String?,
        filterOutZeroAmount: Boolean
    ): Flow<FilteredCollectibleCountDto>
}
