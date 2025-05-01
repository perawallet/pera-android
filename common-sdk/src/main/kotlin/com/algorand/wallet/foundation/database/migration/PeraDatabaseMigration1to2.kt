package com.algorand.wallet.foundation.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal object PeraDatabaseMigration1to2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE account_information ADD COLUMN min_required_balance TEXT NOT NULL DEFAULT '0'"
        )
    }
}
