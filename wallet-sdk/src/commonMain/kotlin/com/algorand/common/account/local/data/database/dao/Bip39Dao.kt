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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.algorand.common.account.local.data.database.model.Bip39Entity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface Bip39Dao {

    @Insert
    suspend fun insert(entity: Bip39Entity)

    @Insert
    suspend fun insertAll(entities: List<Bip39Entity>)

    @Query("SELECT * FROM bip_39")
    suspend fun getAll(): List<Bip39Entity>

    @Query("SELECT * FROM bip_39")
    fun getAllAsFlow(): Flow<List<Bip39Entity>>

    @Query("SELECT COUNT(*) FROM bip_39")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT * FROM bip_39 WHERE :encryptedAddress = encrypted_address")
    suspend fun get(encryptedAddress: String): Bip39Entity?

    @Query("DELETE FROM bip_39 WHERE :encryptedAddress = encrypted_address")
    suspend fun delete(encryptedAddress: String)

    @Query("DELETE FROM bip_39")
    suspend fun clearAll()
}
