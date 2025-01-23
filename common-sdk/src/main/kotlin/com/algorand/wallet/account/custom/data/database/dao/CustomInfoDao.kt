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

package com.algorand.wallet.account.custom.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.wallet.account.custom.data.database.model.CustomInfoEntity

@Dao
internal interface CustomInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CustomInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CustomInfoEntity>)

    @Query("SELECT * FROM custom_info")
    suspend fun getAll(): List<CustomInfoEntity>

    @Query("SELECT * FROM custom_info WHERE :address = algo_address")
    suspend fun get(address: String): CustomInfoEntity

    @Query("SELECT * FROM custom_info WHERE :address = algo_address")
    suspend fun getOrNull(address: String): CustomInfoEntity?

    @Query("DELETE FROM custom_info WHERE :address = algo_address")
    suspend fun delete(address: String)

    @Query("UPDATE custom_info SET custom_name = :customName WHERE :address = algo_address")
    suspend fun updateCustomName(address: String, customName: String)

    @Query("UPDATE custom_info SET order_index = :orderIndex WHERE :address = algo_address")
    suspend fun updateOrderIndex(address: String, orderIndex: Int)

    @Query("DELETE FROM custom_info")
    suspend fun clearAll()

    @Query("SELECT algo_address FROM custom_info WHERE is_backed_up = 0")
    suspend fun getNotBackedUpAddresses(): List<String>

    @Query("SELECT algo_address FROM custom_info WHERE is_backed_up = 1")
    suspend fun getBackedUpAddresses(): List<String>

    @Query("SELECT is_backed_up FROM custom_info WHERE :encryptedAddress = algo_address")
    suspend fun isAccountBackedUp(encryptedAddress: String): Boolean

    @Query("SELECT custom_name FROM custom_info WHERE :address = algo_address")
    suspend fun getCustomName(address: String): String?
}
