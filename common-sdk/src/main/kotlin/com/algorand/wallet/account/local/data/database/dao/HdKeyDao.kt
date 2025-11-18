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
import com.algorand.wallet.account.local.data.database.model.HdKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface HdKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HdKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<HdKeyEntity>)

    @Query("SELECT * FROM hd_keys")
    suspend fun getAll(): List<HdKeyEntity>

    @Query("SELECT algo_address FROM hd_keys")
    suspend fun getAllAddresses(): List<String>

    @Query("SELECT * FROM hd_keys")
    fun getAllAsFlow(): Flow<List<HdKeyEntity>>

    @Query("SELECT COUNT(*) FROM hd_keys")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM hd_keys")
    suspend fun getTableSize(): Int

    @Query("SELECT COUNT(*) FROM hd_keys WHERE seed_id = :seedId")
    suspend fun getDerivedAddressCountOfSeed(seedId: Int): Int

    @Query("SELECT * FROM hd_keys WHERE :algoAddress = algo_address")
    suspend fun get(algoAddress: String): HdKeyEntity?

    @Query("SELECT seed_id FROM hd_keys WHERE algo_address = :algoAddress")
    suspend fun getHdSeedId(algoAddress: String): Int?

    @Query("DELETE FROM hd_keys WHERE :algoAddress = algo_address")
    suspend fun delete(algoAddress: String)

    @Query("DELETE FROM hd_keys")
    suspend fun clearAll()
}
