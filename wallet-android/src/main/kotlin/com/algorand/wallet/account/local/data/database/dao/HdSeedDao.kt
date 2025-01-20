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

package com.algorand.wallet.account.local.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.wallet.account.local.data.database.model.HdSeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface HdSeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HdSeedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<HdSeedEntity>)

    @Query("SELECT * FROM hd_seeds")
    suspend fun getAll(): List<HdSeedEntity>

    @Query("SELECT * FROM hd_seeds")
    fun getAllAsFlow(): Flow<List<HdSeedEntity>>

    @Query("SELECT COUNT(*) FROM hd_seeds")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT * FROM hd_seeds WHERE :seedId = seed_id")
    suspend fun get(seedId: Int): HdSeedEntity?

    @Query("DELETE FROM hd_seeds WHERE :seedId = seed_id")
    suspend fun delete(seedId: Int)

    @Query("DELETE FROM hd_seeds")
    suspend fun clearAll()
}
