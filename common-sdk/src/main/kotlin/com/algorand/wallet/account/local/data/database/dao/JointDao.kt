/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.local.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.algorand.wallet.account.local.data.database.model.JointEntity
import com.algorand.wallet.account.local.data.database.model.JointWithParticipants
import kotlinx.coroutines.flow.Flow

@Dao
internal interface JointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: JointEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<JointEntity>)

    @Transaction
    @Query("SELECT * FROM joint_account")
    suspend fun getAllWithParticipants(): List<JointWithParticipants>

    @Transaction
    @Query("SELECT * FROM joint_account")
    fun getAllWithParticipantsAsFlow(): Flow<List<JointWithParticipants>>

    @Transaction
    @Query("SELECT * FROM joint_account WHERE :algoAddress = algo_address")
    suspend fun getWithParticipants(algoAddress: String): JointWithParticipants?

    @Query("SELECT algo_address FROM joint_account")
    suspend fun getAllAddresses(): List<String>

    @Query("SELECT COUNT(*) FROM joint_account")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM joint_account")
    suspend fun getTableSize(): Int

    @Query("DELETE FROM joint_account WHERE :algoAddress = algo_address")
    suspend fun delete(algoAddress: String)

    @Query("DELETE FROM joint_account")
    suspend fun clearAll()

    @Query("SELECT EXISTS(SELECT * FROM joint_account WHERE :address = algo_address)")
    suspend fun isAddressExists(address: String): Boolean
}
