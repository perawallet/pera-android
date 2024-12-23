/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.asset.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.common.asset.data.database.model.AssetDetailEntity

@Dao
internal interface AssetDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AssetDetailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<AssetDetailEntity>)

    @Query("DELETE FROM asset_detail WHERE asset_id = :assetId")
    suspend fun deleteAllByAssetId(assetId: Long)

    @Query("SELECT * FROM asset_detail WHERE asset_id = :assetId")
    suspend fun getByAssetId(assetId: Long): AssetDetailEntity?

    @Query("SELECT * FROM asset_detail WHERE asset_id IN (:assetIds)")
    suspend fun getByAssetIds(assetIds: List<Long>): List<AssetDetailEntity>

    @Query("DELETE FROM asset_detail")
    suspend fun clearAll()
}
