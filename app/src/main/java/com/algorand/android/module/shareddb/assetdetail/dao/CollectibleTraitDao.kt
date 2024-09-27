package com.algorand.android.module.shareddb.assetdetail.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleTraitEntity

@Dao
interface CollectibleTraitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CollectibleTraitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CollectibleTraitEntity>)

    @Query("DELETE FROM collectible_trait WHERE collectible_asset_id = :collectibleAssetId")
    suspend fun deleteAllByCollectibleAssetId(collectibleAssetId: Long)

    @Query("SELECT * FROM collectible_trait WHERE collectible_asset_id = :collectibleAssetId")
    suspend fun getByCollectibleAssetId(collectibleAssetId: Long): List<CollectibleTraitEntity>

    @Query("SELECT * FROM collectible_trait WHERE collectible_asset_id IN (:collectibleAssetIds)")
    suspend fun getByCollectibleAssetIds(collectibleAssetIds: List<Long>): List<CollectibleTraitEntity>

    @Query("DELETE FROM collectible_trait")
    suspend fun clearAll()
}
