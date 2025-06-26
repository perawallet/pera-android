package com.algorand.wallet.account.webauthn.data.database.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Represents a data model for querying a site along with its associated passkeys.
 *
 * This class is used in conjunction with the Room persistence library to define a result
 * from a database query that retrieves a `SiteEntity` and its related `PasskeyEntity` objects.
 *
 * Fields:
 * - `site`: The site information represented as a `SiteEntity`. This corresponds to a row
 *   in the "sites" database table.
 * - `passkeys`: A list of passkeys associated with the site, represented as `PasskeyEntity` objects.
 *   These correspond to related rows in the "passkeys" database table, linked by the `siteId` foreign key.
 *
 * Relationships:
 * - The `@Embedded` annotation is used for including the `SiteEntity` fields as a part of the query result.
 * - The `@Relation` annotation defines the one-to-many relationship between a site and its passkeys,
 *   with the `id` column in `SiteEntity` acting as the parent key and the `siteId` column
 *   in `PasskeyEntity` acting as the child key.
 */
data class SiteWithPasskeysQuery(
    @Embedded val site: SiteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "site_id",
    )
    val passkeys: List<PasskeyEntity>,
)
