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

package com.algorand.wallet.foundation.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal object PeraDatabaseMigration3to4 : Migration(3, 4) {

    override fun migrate(db: SupportSQLiteDatabase) {
        migrateCollectibleTable(db)
        migrateAssetHoldingTable(db)
    }

    private fun migrateCollectibleTable(database: SupportSQLiteDatabase) {
        dropCollectibleTable(database)
        createNewCollectibleTable(database)
        createUniqueIndexForCollectibleAssetIds(database)
    }

    private fun createNewCollectibleTable(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS collectible (
                collectible_asset_id INTEGER NOT NULL,
                standard_type TEXT,
                media_type TEXT,
                primary_image_url TEXT,
                title TEXT,
                description TEXT,
                collection_id INTEGER,
                collection_name TEXT,
                collection_description TEXT,
                PRIMARY KEY (collectible_asset_id)
            )
        """.trimIndent()
        )
    }

    private fun dropCollectibleTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `collectible`")
    }

    private fun migrateAssetHoldingTable(database: SupportSQLiteDatabase) {
        dropAssetHoldingTable(database)
        createNewAssetHoldingTable(database)
        createUniqueIndexForAssetHoldingTable(database)
    }

    private fun dropAssetHoldingTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `asset_holding_table`")
    }

    private fun createNewAssetHoldingTable(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS asset_holding_table (
                algo_address TEXT NOT NULL,
                asset_id INTEGER NOT NULL,
                amount TEXT NOT NULL,
                is_deleted INTEGER NOT NULL,
                is_frozen INTEGER NOT NULL,
                opted_in_at_round INTEGER,
                opted_out_at_round INTEGER,
                asset_status TEXT NOT NULL,
                PRIMARY KEY(algo_address, asset_id)
            )
        """.trimIndent()
        )
    }

    private fun createUniqueIndexForAssetHoldingTable(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE UNIQUE INDEX IF NOT EXISTS index_asset_holding_table_algo_address_asset_id 
                ON `asset_holding_table` (`algo_address`, `asset_id`)
                """.trimIndent()
        )
    }

    private fun createUniqueIndexForCollectibleAssetIds(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS index_collectible_collectible_asset_id
            ON collectible(collectible_asset_id)
            """.trimIndent()
        )
    }
}
