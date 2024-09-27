package com.algorand.android.module.shareddb.assetdetail.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.module.shareddb.assetdetail.model.AssetDetailEntity

@Dao
interface AssetDetailDao {

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
