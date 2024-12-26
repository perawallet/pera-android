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
import com.algorand.common.account.local.data.database.model.NoAuthEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface NoAuthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: NoAuthEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<NoAuthEntity>)

    @Query("SELECT * FROM no_auth")
    suspend fun getAll(): List<NoAuthEntity>

    @Query("SELECT * FROM no_auth")
    fun getAllAsFlow(): Flow<List<NoAuthEntity>>

    @Query("SELECT COUNT(*) FROM no_auth")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT * FROM no_auth WHERE :encryptedAddress = encrypted_address")
    suspend fun get(encryptedAddress: String): NoAuthEntity?

    @Query("DELETE FROM no_auth WHERE :encryptedAddress = encrypted_address")
    suspend fun delete(encryptedAddress: String)

    @Query("DELETE FROM no_auth")
    suspend fun clearAll()
}
