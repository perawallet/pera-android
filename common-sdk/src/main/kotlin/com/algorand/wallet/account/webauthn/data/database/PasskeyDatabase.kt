package com.algorand.wallet.account.webauthn.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.algorand.wallet.account.webauthn.data.database.dao.PasskeyDao
import com.algorand.wallet.account.webauthn.data.database.dao.SiteDao
import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import com.algorand.wallet.account.webauthn.data.database.model.SiteEntity

/**
 * Represents the Room database configuration for the passkey and site data.
 * This class serves as the main database access point for interacting with the
 * underlying SQLite database used to persist entities.
 *
 * The `PasskeyDatabase` is configured with two entities:
 * - PasskeyEntity: Handles the storage of passkey-related information.
 * - SiteEntity: Handles the storage of site-related information.
 *
 * The database version is managed through the `DATABASE_VERSION` constant and each
 * database schema change must be reflected in this version to ensure proper migration.
 */
@Database(entities = [PasskeyEntity::class, SiteEntity::class], version = 1)
abstract class PasskeyDatabase : RoomDatabase() {
    /**
     * Provides access to the PasskeyDao interface for performing database operations
     * related to the PasskeyEntity table.
     *
     * @return an instance of PasskeyDao to interact with passkey-related database queries.
     */
    abstract fun passkeyDao(): PasskeyDao

    /**
     * Provides access to the SiteDao interface for performing database operations
     * related to the SiteEntity table.
     *
     * @return an instance of SiteDao to interact with site-related database queries.
     */
    abstract fun siteDao(): SiteDao

    companion object {
        /**
         * The version of the database schema.
         * Used by the Room database library to manage schema migrations.
         * Changes to the structure of the database must be accompanied by an increment in this version.
         */
        const val DATABASE_VERSION = 1

        /**
         * Represents the name of the database used for storing data related to passkeys and sites.
         *
         * This constant is utilized by the Room Database configuration for identifying the database file.
         */
        const val DATABASE_NAME = "passkey_database"
    }
}
