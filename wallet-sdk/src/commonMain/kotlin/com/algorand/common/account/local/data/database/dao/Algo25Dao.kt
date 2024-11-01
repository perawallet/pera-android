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

package com.algorand.common.account.local.data.database.dao

import androidx.room.*
import com.algorand.common.account.local.data.database.model.Algo25Entity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface Algo25Dao {

    @Insert
    suspend fun insert(entity: Algo25Entity)

    @Insert
    suspend fun insertAll(entities: List<Algo25Entity>)

    @Query("SELECT * FROM algo_25")
    suspend fun getAll(): List<Algo25Entity>

    @Query("SELECT * FROM algo_25")
    fun getAllAsFlow(): Flow<List<Algo25Entity>>

    @Query("SELECT COUNT(*) FROM algo_25")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT * FROM algo_25 WHERE :encryptedAddress = encrypted_address")
    suspend fun get(encryptedAddress: String): Algo25Entity?

    @Query("DELETE FROM algo_25 WHERE :encryptedAddress = encrypted_address")
    suspend fun delete(encryptedAddress: String)

    @Query("DELETE FROM algo_25")
    suspend fun clearAll()
}
