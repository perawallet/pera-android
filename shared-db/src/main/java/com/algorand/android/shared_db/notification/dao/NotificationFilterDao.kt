/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.shared_db.notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.shared_db.notification.model.NotificationFilterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationFilterDao {

    @Query("SELECT * FROM notificationfilter")
    fun getAllAsFlow(): Flow<List<NotificationFilterEntity>>

    @Query("SELECT * FROM notificationfilter WHERE public_key = :address")
    fun getNotificationFilters(address: String): List<NotificationFilterEntity>

    @Query("DELETE FROM notificationfilter WHERE public_key = :address")
    suspend fun deleteByAddress(address: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notificationFilter: NotificationFilterEntity)
}
