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
 (holding.amount * IFNULL(asset.usd_value, 0)) AS total_usd_value,
        holding.opted_in_at_round AS opted_in_at_round,
        asset.asset_id AS asset_id,
        asset.name AS name,
        asset.unit_name AS unit_name,
        asset.logo_url AS logo_url,
        asset.usd_value AS usd_value,
        asset.decimals AS decimals,
        asset.verification_tier AS verification_tier,
        collectible.title AS title,
        collectible.primary_image_url AS primary_image_url,
        collectible.collection_name AS collection_name
    FROM asset_detail AS asset
    INNER JOIN asset_holding_table AS holding 
        ON asset.asset_id = holding.asset_id
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
        (:filterOutZeroAmount = 0 OR holding.amount != 0)
    AND
        (:filterOutCollectibles = 0 OR collectible.title IS NULL)
    AND
        (:filterOutCollectiblesWithZeroAmount = 0 OR NOT (collectible.title IS NOT NULL AND holding.amount = 0))
    ORDER BY
        CASE 
            WHEN holding.asset_status IN ('PENDING_FOR_ADDITION', 'PENDING_FOR_REMOVAL') THEN 0
            ELSE 1
        END ASC,
        CASE WHEN :sortType = 'name_asc' THEN COALESCE(asset.name, collectible.title) END ASC,
        CASE WHEN :sortType = 'name_desc' THEN COALESCE(asset.name, collectible.title) END DESC,
        CASE WHEN :sortType = 'value_asc' THEN total_usd_value END ASC,
        CASE WHEN :sortType = 'value_desc' THEN total_usd_value END DESC
"""
    )
    fun getPaginatedAssetCollectibleItems(
        addressList: List<String>,
        searchKeyword: String?,
        filterOutZeroAmount: Boolean,
        filterOutCollectibles: Boolean,
        filterOutCollectiblesWithZeroAmount: Boolean,
        sortType: String
    ): PagingSource<Int, PaginatedAssetCollectibleItemDto>
}
