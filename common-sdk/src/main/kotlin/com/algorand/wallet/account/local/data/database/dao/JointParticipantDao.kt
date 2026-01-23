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
import com.algorand.wallet.account.local.data.database.model.JointParticipantEntity

@Dao
internal interface JointParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<JointParticipantEntity>)

    @Query("SELECT participant_address FROM joint_participant WHERE joint_address = :jointAddress ORDER BY participant_index")
    suspend fun getParticipantAddresses(jointAddress: String): List<String>

    @Query("SELECT COUNT(*) FROM joint_participant WHERE joint_address = :jointAddress")
    suspend fun getParticipantCount(jointAddress: String): Int

    @Query("SELECT joint_address FROM joint_participant WHERE participant_address = :participantAddress")
    suspend fun getJointAddressesByParticipant(participantAddress: String): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM joint_participant WHERE joint_address = :jointAddress AND participant_address = :participantAddress)")
    suspend fun isParticipant(jointAddress: String, participantAddress: String): Boolean

    @Query("DELETE FROM joint_participant WHERE joint_address = :jointAddress")
    suspend fun deleteByJointAddress(jointAddress: String)

    @Query("DELETE FROM joint_participant")
    suspend fun clearAll()
}
