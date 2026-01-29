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

package com.algorand.wallet.account.local.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "joint_participant",
    primaryKeys = ["joint_address", "participant_index"],
    foreignKeys = [
        ForeignKey(
            entity = JointEntity::class,
            parentColumns = ["algo_address"],
            childColumns = ["joint_address"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("joint_address")]
)
internal data class JointParticipantEntity(
    @ColumnInfo("joint_address")
    val jointAddress: String,
    @ColumnInfo("participant_index")
    val participantIndex: Int,
    @ColumnInfo("participant_address")
    val participantAddress: String
)
