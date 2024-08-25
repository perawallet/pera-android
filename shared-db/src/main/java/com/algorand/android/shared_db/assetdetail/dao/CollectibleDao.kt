package com.algorand.android.shared_db.assetdetail.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.shared_db.assetdetail.model.CollectibleEntity

@Dao
interface CollectibleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CollectibleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CollectibleEntity>)

    @Query("DELETE FROM collectible WHERE collectible_asset_id = :collectibleAssetId")
    suspend fun deleteAllByCollectibleAssetId(collectibleAssetId: Long)

    @Query("SELECT * FROM collectible WHERE collectible_asset_id = :collectibleAssetId")
    suspend fun getByCollectibleAssetId(collectibleAssetId: Long): CollectibleEntity?

    @Query("SELECT * FROM collectible WHERE collectible_asset_id IN (:collectibleAssetIds)")
    suspend fun getByCollectibleAssetIds(collectibleAssetIds: List<Long>): List<CollectibleEntity>

    @Query("DELETE FROM collectible")
    suspend fun clearAll()
}
