package com.algorand.wallet.account.webauthn.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a database entity for storing site information.
 *
 * This entity is part of the Room persistence library and defines the structure
 * of the "sites" table in the database. Each entry corresponds to a site
 * that can be associated with passkeys or other data.
 *
 * Database Table: "sites"
 * - The primary key for this table is `id`, which is auto-generated.
 * - An index is defined on the `url` column to enforce uniqueness constraints.
 *
 * Fields:
 * - `id`: Unique identifier for the site.
 * - `url`: URL associated with the site. This field is unique and indexed.
 * - `packageName`: Package name associated with the site, typically used for identifying applications.
 * - `name`: Human-readable name of the site.
 *
 * Relationships:
 * - This entity is referenced in other entities such as `PasskeyEntity`, where it acts
 *   as a parent or foreign key to establish a relational mapping.
 */
@Entity(
    tableName = "sites",
    indices = [
        Index("url", unique = true),
    ],
)
data class SiteEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "url") val url: String = "",
    @ColumnInfo(name = "packageName") val packageName: String = "",
    @ColumnInfo(name = "name") val name: String = "",
)
