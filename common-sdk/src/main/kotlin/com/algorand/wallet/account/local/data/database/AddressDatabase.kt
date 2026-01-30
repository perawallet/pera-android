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

package com.algorand.wallet.account.local.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.algorand.wallet.account.local.data.database.AddressDatabase.Companion.DATABASE_VERSION
import com.algorand.wallet.account.local.data.database.dao.Algo25Dao
import com.algorand.wallet.account.local.data.database.dao.Algo25NoAuthDao
import com.algorand.wallet.account.local.data.database.dao.HdKeyDao
import com.algorand.wallet.account.local.data.database.dao.HdSeedDao
import com.algorand.wallet.account.local.data.database.dao.JointDao
import com.algorand.wallet.account.local.data.database.dao.JointParticipantDao
import com.algorand.wallet.account.local.data.database.dao.LedgerBleDao
import com.algorand.wallet.account.local.data.database.dao.NoAuthDao
import com.algorand.wallet.account.local.data.database.model.Algo25Entity
import com.algorand.wallet.account.local.data.database.model.HdKeyEntity
import com.algorand.wallet.account.local.data.database.model.HdSeedEntity
import com.algorand.wallet.account.local.data.database.model.JointEntity
import com.algorand.wallet.account.local.data.database.model.JointParticipantEntity
import com.algorand.wallet.account.local.data.database.model.LedgerBleEntity
import com.algorand.wallet.account.local.data.database.model.NoAuthEntity

@Database(
    entities = [
        LedgerBleEntity::class,
        NoAuthEntity::class,
        HdKeyEntity::class,
        HdSeedEntity::class,
        Algo25Entity::class,
        JointEntity::class,
        JointParticipantEntity::class
    ],
    version = DATABASE_VERSION
)
internal abstract class AddressDatabase : RoomDatabase() {

    abstract fun ledgerBleDao(): LedgerBleDao
    abstract fun noAuthDao(): NoAuthDao
    abstract fun hdKeyDao(): HdKeyDao
    abstract fun hdSeedDao(): HdSeedDao
    abstract fun algo25Dao(): Algo25Dao
    abstract fun algo25NoAuthDao(): Algo25NoAuthDao
    abstract fun jointDao(): JointDao
    abstract fun jointParticipantDao(): JointParticipantDao

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "address_database"

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS joint_account (
                        algo_address TEXT NOT NULL,
                        threshold INTEGER NOT NULL,
                        version INTEGER NOT NULL,
                        PRIMARY KEY(algo_address)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS joint_participant (
                        joint_address TEXT NOT NULL,
                        participant_index INTEGER NOT NULL,
                        participant_address TEXT NOT NULL,
                        PRIMARY KEY(joint_address, participant_index),
                        FOREIGN KEY(joint_address) REFERENCES joint_account(algo_address) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_joint_participant_joint_address
                    ON joint_participant(joint_address)
                    """.trimIndent()
                )
            }
        }
    }
}
