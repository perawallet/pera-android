package com.algorand.wallet.account.custom.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.wallet.account.custom.data.database.model.CustomHdSeedInfoEntity

@Dao
internal interface CustomHdSeedInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CustomHdSeedInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CustomHdSeedInfoEntity>)

    @Query("SELECT * FROM custom_hd_seed_info")
    suspend fun getAll(): List<CustomHdSeedInfoEntity>

    @Query("SELECT * FROM custom_hd_seed_info WHERE :seedId = seed_id")
    suspend fun get(seedId: Int): CustomHdSeedInfoEntity

    @Query("SELECT * FROM custom_hd_seed_info WHERE :seedId = seed_id")
    suspend fun getOrNull(seedId: Int): CustomHdSeedInfoEntity?

    @Query("DELETE FROM custom_hd_seed_info WHERE :seedId = seed_id")
    suspend fun delete(seedId: Int)

    @Query("UPDATE custom_hd_seed_info SET entropy_custom_name = :entropyCustomName WHERE :seedId = seed_id")
    suspend fun updateCustomName(seedId: Int, entropyCustomName: String)

    @Query("UPDATE custom_hd_seed_info SET order_index = :orderIndex WHERE :seedId = seed_id")
    suspend fun updateOrderIndex(seedId: Int, orderIndex: Int)

    @Query("DELETE FROM custom_hd_seed_info")
    suspend fun clearAll()

    @Query("SELECT seed_id FROM custom_hd_seed_info WHERE is_backed_up = 0")
    suspend fun getNotBackedUpSeedIds(): List<Int>

    @Query("SELECT seed_id FROM custom_hd_seed_info WHERE is_backed_up = 1")
    suspend fun getBackedUpSeedIds(): List<Int>

    @Query("SELECT is_backed_up FROM custom_hd_seed_info WHERE :seedId = seed_id")
    suspend fun isAccountBackedUp(seedId: Int): Boolean

    @Query("SELECT entropy_custom_name FROM custom_hd_seed_info WHERE :seedId = seed_id")
    suspend fun getCustomName(seedId: Int): String?
}
