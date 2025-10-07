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
import com.algorand.android.credentials.passkeys.data.model.PasskeyEntity

@Dao
internal interface PasskeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PasskeyEntity): Long

    @Query("SELECT * FROM passkey_table WHERE credential_id = :credentialId")
    suspend fun getByCredId(credentialId: String): PasskeyEntity?

    @Query("SELECT COUNT(*) FROM passkey_table WHERE site_id = :siteId")
    suspend fun getPasskeyCountBySiteId(siteId: Long): Int

    @Query("DELETE FROM passkey_table WHERE credential_id = :credentialId")
    suspend fun deleteByCredId(credentialId: String): Int

    @Query("DELETE FROM passkey_table")
    suspend fun deleteAll()

    @Query("UPDATE passkey_table SET last_used_time_ms = :lastUsed WHERE credential_id = :credentialId")
    suspend fun updateLastUsedTime(credentialId: String, lastUsed: Long)

    @Query(
        """
            SELECT EXISTS (
            SELECT 1 
            FROM passkey_table 
            INNER JOIN sites ON passkey_table.site_id = sites.id 
            WHERE sites.url = :siteUrl AND passkey_table.user_name = :username AND passkey_table.seed_id = :seedId
        )
        """
    )
    suspend fun doesPasskeyExist(siteUrl: String, username: String, seedId: Int): Boolean
}
