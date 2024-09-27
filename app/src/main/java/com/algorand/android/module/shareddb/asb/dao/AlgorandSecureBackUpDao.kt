package com.algorand.android.module.shareddb.asb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.module.shareddb.asb.model.AlgorandSecureBackUpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlgorandSecureBackUpDao {

    @Query("SELECT * FROM algorand_secure_back_up")
    fun getAllAsFlow(): Flow<List<AlgorandSecureBackUpEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AlgorandSecureBackUpEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<AlgorandSecureBackUpEntity>)

    @Query("SELECT * FROM algorand_secure_back_up")
    suspend fun getAll(): List<AlgorandSecureBackUpEntity>

    @Query("SELECT * FROM algorand_secure_back_up WHERE :encryptedAddress = encrypted_address")
    suspend fun get(encryptedAddress: String): AlgorandSecureBackUpEntity?

    @Query("DELETE FROM algorand_secure_back_up WHERE :encryptedAddress = encrypted_address")
    suspend fun delete(encryptedAddress: String)

    @Query("DELETE FROM algorand_secure_back_up")
    suspend fun clearAll()
}
