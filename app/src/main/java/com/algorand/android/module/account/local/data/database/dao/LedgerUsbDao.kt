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

package com.algorand.android.module.account.local.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.algorand.android.module.account.local.data.database.model.LedgerUsbEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LedgerUsbDao {

    @Insert
    suspend fun insert(entity: LedgerUsbEntity)

    @Insert
    suspend fun insertAll(entities: List<LedgerUsbEntity>)

    @Query("SELECT * FROM ledger_usb")
    suspend fun getAll(): List<LedgerUsbEntity>

    @Query("SELECT * FROM ledger_usb")
    fun getAllAsFlow(): Flow<List<LedgerUsbEntity>>

    @Query("SELECT COUNT(*) FROM ledger_usb")
    fun getTableSizeAsFlow(): Flow<Int>

    @Query("SELECT * FROM ledger_usb WHERE :encryptedAddress = encrypted_address")
    suspend fun get(encryptedAddress: String): LedgerUsbEntity?

    @Query("DELETE FROM ledger_usb WHERE :encryptedAddress = encrypted_address")
    suspend fun delete(encryptedAddress: String)

    @Query("DELETE FROM ledger_usb")
    suspend fun clearAll()
}
