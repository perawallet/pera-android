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

package com.algorand.android.credentials.passkeys.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.algorand.android.credentials.passkeys.data.model.SiteEntity
import com.algorand.android.credentials.passkeys.data.model.SiteWithPasskeysQuery
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PasskeySiteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: SiteEntity): Long

    @Transaction
    @Query("SELECT * FROM sites ORDER BY url")
    fun getPasskeysAsFlow(): Flow<List<SiteWithPasskeysQuery>>

    @Transaction
    @Query("SELECT * FROM sites WHERE url = :url ORDER BY url")
    suspend fun getPasskeysByUrl(url: String): List<SiteWithPasskeysQuery>

    @Transaction
    @Query("SELECT * FROM sites WHERE id = :id ORDER BY url")
    suspend fun getPasskeysById(id: Long): List<SiteWithPasskeysQuery>

    @Query("DELETE FROM sites WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT id FROM sites WHERE url = :url")
    suspend fun getSiteId(url: String): Long?

    @Query("SELECT * FROM sites WHERE id = :id")
    suspend fun getSiteById(id: Long): SiteEntity?

    @Query("DELETE FROM sites")
    suspend fun deleteAll()
}
