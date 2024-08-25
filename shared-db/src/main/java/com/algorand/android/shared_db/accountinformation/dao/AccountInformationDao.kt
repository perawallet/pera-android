package com.algorand.android.shared_db.accountinformation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountInformationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(accountInformationEntity: AccountInformationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accountInformationEntities: List<AccountInformationEntity>)

    @Query("SELECT * FROM account_information WHERE encrypted_address = :encryptedAddress")
    suspend fun get(encryptedAddress: String): AccountInformationEntity?

    @Query("SELECT * FROM account_information WHERE encrypted_address = :encryptedAddress")
    fun getAsFlow(encryptedAddress: String): Flow<AccountInformationEntity?>

    @Query("SELECT COUNT(*) FROM account_information")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT * FROM account_information")
    suspend fun getAll(): List<AccountInformationEntity>

    @Query("SELECT * FROM account_information")
    fun getAllAsFlow(): Flow<List<AccountInformationEntity>>

    @Query("DELETE FROM account_information WHERE encrypted_address = :encryptedAddress")
    suspend fun delete(encryptedAddress: String)

    @Query("SELECT MIN(last_fetched_round) FROM account_information WHERE created_at_round IS NOT NULL")
    suspend fun getEarliestLastFetchedRound(): Long?

    @Query("DELETE FROM account_information")
    suspend fun clearAll()
}
