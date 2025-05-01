package com.algorand.wallet.foundation.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal object PeraDatabaseMigration2to3 : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE account_information ADD COLUMN opted_in_assets_count INTEGER NOT NULL DEFAULT 0"
        )
    }
}
