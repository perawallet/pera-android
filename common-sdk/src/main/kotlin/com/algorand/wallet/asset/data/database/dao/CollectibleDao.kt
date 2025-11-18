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

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.algorand.wallet.asset.data.database.model.CollectibleEntity
import com.algorand.wallet.foundation.database.util.DaoUtils.smartUpsert

@Dao
internal interface CollectibleDao {

    @Upsert
    suspend fun upsertAll(entities: List<CollectibleEntity>)

    @Transaction
    suspend fun insertAll(entities: List<CollectibleEntity>) {
        smartUpsert(
            newEntities = entities,
            getKey = { it.collectibleAssetId },
            fetchExistingByKeys = { getByCollectibleAssetIds(it) },
            upsert = { upsertAll(it) }
        )
    }

    @Query("SELECT * FROM collectible WHERE collectible_asset_id = :collectibleAssetId")
    suspend fun getByCollectibleAssetId(collectibleAssetId: Long): CollectibleEntity?

    @Query("SELECT EXISTS(SELECT * FROM collectible WHERE collectible_asset_id = :collectibleAssetId)")
    suspend fun isCollectibleExist(collectibleAssetId: Long): Boolean

    @Query("SELECT * FROM collectible WHERE collectible_asset_id IN (:collectibleAssetIds)")
    suspend fun getByCollectibleAssetIds(collectibleAssetIds: List<Long>): List<CollectibleEntity>

    @Query("SELECT collectible_asset_id FROM collectible")
    suspend fun getCollectibleIds(): List<Long>

    @Query("DELETE FROM collectible")
    suspend fun clearAll()

    @Query(
        """
        SELECT collectible.primary_image_url
        FROM asset_holding_table AS asset_holding
        INNER JOIN collectible AS collectible ON asset_holding.asset_id = collectible.collectible_asset_id
        WHERE asset_holding.opted_in_at_round IS NOT NULL
        AND collectible.primary_image_url IS NOT NULL
        ORDER BY asset_holding.opted_in_at_round DESC
        LIMIT :count
    """
    )
    suspend fun getRecentlyAddedCollectibleUrls(count: Int): List<String>
}
