package com.algorand.android.module.shareddb.accountsorting.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.module.shareddb.accountsorting.model.AccountIndexEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountIndexDao {

    @Query("SELECT * FROM account_index_table")
    fun getAllAsFlow(): Flow<List<AccountIndexEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AccountIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<AccountIndexEntity>)

    @Query("SELECT * FROM account_index_table")
    suspend fun getAll(): List<AccountIndexEntity>

    @Query("SELECT * FROM account_index_table WHERE :encryptedAddress = encrypted_address")
    suspend fun get(encryptedAddress: String): AccountIndexEntity?

    @Query("DELETE FROM account_index_table WHERE :encryptedAddress = encrypted_address")
    suspend fun delete(encryptedAddress: String)

    @Query("DELETE FROM account_index_table")
    suspend fun clearAll()
}
