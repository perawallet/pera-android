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
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.algorand.wallet.asset.data.database.model.AssetDetailEntity
import com.algorand.wallet.asset.data.database.model.AssetLiteInformationDao
import com.algorand.wallet.foundation.database.util.DaoUtils.smartUpsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AssetDetailDao {

    @Upsert
    suspend fun upsert(entity: AssetDetailEntity)

    @Upsert
    suspend fun upsertAll(entities: List<AssetDetailEntity>)

    @Transaction
    suspend fun insert(entity: AssetDetailEntity) {
        smartUpsert(
            newEntity = entity,
            getKey = { it.assetId },
            fetchExistingByKey = { getByAssetId(it) },
            upsert = { upsert(it) }
        )
    }

    @Transaction
    suspend fun insertAll(entities: List<AssetDetailEntity>) {
        smartUpsert(
            newEntities = entities,
            getKey = { it.assetId },
            fetchExistingByKeys = { getByAssetIds(it) },
            upsert = { upsertAll(it) }
        )
    }

    @Query("SELECT * FROM asset_detail WHERE asset_id = :assetId")
    suspend fun getByAssetId(assetId: Long): AssetDetailEntity?

    @Query("SELECT * FROM asset_detail WHERE asset_id IN (:assetIds)")
    suspend fun getByAssetIds(assetIds: List<Long>): List<AssetDetailEntity>

    @Query("SELECT asset_id FROM asset_detail")
    suspend fun getAllIds(): List<Long>

    @Query("DELETE FROM asset_detail")
    suspend fun clearAll()

    @Query("SELECT asset_id, usd_value, decimals FROM asset_detail WHERE asset_id IN (:assetIds)")
    fun getLiteInformationByAssetIds(assetIds: List<Long>): Flow<List<AssetLiteInformationDao>>

    @Query("SELECT asset_id, usd_value, decimals FROM asset_detail WHERE asset_id = :assetId")
    suspend fun getLiteInformation(assetId: Long): AssetLiteInformationDao?

    @Query("SELECT asset_creator_address FROM asset_detail WHERE asset_id = :assetId")
    suspend fun getAssetCreatorAddress(assetId: Long): String?

    @Query("UPDATE asset_detail SET is_favorite = :isFavorite WHERE asset_id = :assetId")
    suspend fun updateFavoriteStatus(assetId: Long, isFavorite: Boolean)

    @Query("UPDATE asset_detail SET is_price_alert_enabled = :isPriceAlertEnabled WHERE asset_id = :assetId")
    suspend fun updatePriceAlertStatus(assetId: Long, isPriceAlertEnabled: Boolean)

    @Query("SELECT asset_id, is_favorite FROM asset_detail WHERE asset_id IN (:assetIds)")
    suspend fun getFavoriteStatuses(
        assetIds: List<Long>
    ): Map<@MapColumn("asset_id") Long, @MapColumn("is_favorite") Boolean?>
}
