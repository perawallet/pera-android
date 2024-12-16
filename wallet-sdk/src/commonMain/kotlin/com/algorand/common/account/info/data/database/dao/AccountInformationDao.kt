/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.account.info.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.common.account.info.data.database.model.AccountInformationEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AccountInformationDao {

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
