package com.algorand.wallet.account.webauthn.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a database entity for storing passkey information associated with a user and site.
 *
 * This entity is part of the Room persistence library, defining the structure of the "passkeys" table
 * in the database. Each entry corresponds to the details of a passkey, including user information,
 * credential data, and its association with a specific site.
 *
 * Database Table: "passkeys"
 * - The primary key for this table is `id`, which is auto-generated.
 * - An index is defined on the `credentialId` column to enforce uniqueness constraints.
 *
 * Fields:
 * - `id`: Unique identifier for the passkey.
 * - `userId`: Identifier of the user associated with the passkey.
 * - `username`: Name of the user associated with the passkey.
 * - `userHandle`: User handle linked to the passkey.
 * - `credentialId`: Credential ID associated with the passkey (uniquely indexed).
 * - `count`: Counter associated with the passkey, typically incremented with usage.
 * - `lastUsedTimeMs`: Timestamp representing the last usage time of the passkey in milliseconds.
 * - `siteId`: Foreign key referencing the site to which the passkey belongs.
 *
 * Relationships:
 * - This entity is associated with the `SiteEntity` class via the `siteId` foreign key.
 */
@Entity(
    tableName = "passkeys",
    indices = [
        Index("credential_id", unique = true),
    ],
)
data class PasskeyEntity(
    // Primary Key
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,

    // Foreign Keys
    @ColumnInfo(name = "site_id") val siteId: Long,
    @ColumnInfo(name = "seed_id") val seedId: Int,

    // User Data
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "user_name") val username: String,
    @ColumnInfo(name = "user_handle") val userHandle: String,

    // Key Data
    @ColumnInfo(name = "credential_id") val credentialId: String,
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "last_used_time_ms") val lastUsedTimeMs: Long,

    )
