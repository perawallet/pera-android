package com.algorand.wallet.account.info.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.wallet.account.info.data.database.model.EntropyInformationEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface EntropyInformationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EntropyInformationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<EntropyInformationEntity>)

    @Query("UPDATE entropy_information SET entropy_custom_name = :entropyCustomName WHERE seed_id = :seedId")
    suspend fun update(seedId: Int, entropyCustomName: String)

    @Query("SELECT * FROM entropy_information")
    suspend fun getAll(): List<EntropyInformationEntity>

    @Query("SELECT * FROM entropy_information")
    fun getAllAsFlow(): Flow<List<EntropyInformationEntity>>

    @Query("SELECT COUNT(*) FROM entropy_information")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT * FROM entropy_information WHERE :seedId = seed_id")
    suspend fun get(seedId: Int): EntropyInformationEntity?

    @Query("DELETE FROM entropy_information WHERE :seedId = seed_id")
    suspend fun delete(seedId: Int)

    @Query("DELETE FROM entropy_information")
    suspend fun clearAll()
}
