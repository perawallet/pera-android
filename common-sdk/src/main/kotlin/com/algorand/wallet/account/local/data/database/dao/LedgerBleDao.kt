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
import com.algorand.wallet.account.local.data.database.model.LedgerBleEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LedgerBleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LedgerBleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<LedgerBleEntity>)

    @Query("SELECT * FROM ledger_ble")
    suspend fun getAll(): List<LedgerBleEntity>

    @Query("SELECT * FROM ledger_ble")
    fun getAllAsFlow(): Flow<List<LedgerBleEntity>>

    @Query("SELECT COUNT(*) FROM ledger_ble")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT * FROM ledger_ble WHERE :algoAddress = algo_address")
    suspend fun get(algoAddress: String): LedgerBleEntity?

    @Query("DELETE FROM ledger_ble WHERE :algoAddress = algo_address")
    suspend fun delete(algoAddress: String)

    @Query("DELETE FROM ledger_ble")
    suspend fun clearAll()
}
