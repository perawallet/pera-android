package com.algorand.android.shared_db.assetdetail.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaEntity

@Dao
interface CollectibleMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CollectibleMediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CollectibleMediaEntity>)

    @Query("DELETE FROM collectible_media WHERE collectible_asset_id = :collectibleAssetId")
    suspend fun deleteAllByCollectibleAssetId(collectibleAssetId: Long)

    @Query("SELECT * FROM collectible_media WHERE collectible_asset_id = :collectibleAssetId")
    suspend fun getByCollectibleAssetId(collectibleAssetId: Long): List<CollectibleMediaEntity>

    @Query("SELECT * FROM collectible_media WHERE collectible_asset_id IN (:collectibleAssetIds)")
    suspend fun getByCollectibleAssetIds(collectibleAssetIds: List<Long>): List<CollectibleMediaEntity>

    @Query("DELETE FROM collectible_media")
    suspend fun clearAll()
}
