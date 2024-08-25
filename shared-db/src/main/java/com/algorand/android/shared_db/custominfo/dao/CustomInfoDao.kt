package com.algorand.android.shared_db.custominfo.dao

import androidx.room.*
import com.algorand.android.shared_db.assetdetail.model.CustomInfoEntity

@Dao
interface CustomInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CustomInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CustomInfoEntity>)

    @Query("SELECT * FROM custom_info")
    suspend fun getAll(): List<CustomInfoEntity>

    @Query("SELECT * FROM custom_info WHERE :encryptedAddress = encrypted_address")
    suspend fun get(encryptedAddress: String): CustomInfoEntity

    @Query("SELECT * FROM custom_info WHERE :encryptedAddress = encrypted_address")
    suspend fun getOrNull(encryptedAddress: String): CustomInfoEntity?

    @Query("DELETE FROM custom_info WHERE :encryptedAddress = encrypted_address")
    suspend fun delete(encryptedAddress: String)

    @Query("UPDATE custom_info SET custom_name = :customName WHERE :encryptedAddress = encrypted_address")
    suspend fun updateCustomName(encryptedAddress: String, customName: String)

    @Query("DELETE FROM custom_info")
    suspend fun clearAll()
}
