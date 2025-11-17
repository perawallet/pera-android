package com.algorand.wallet.account.webauthn.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.algorand.wallet.account.webauthn.data.database.model.SiteEntity
import com.algorand.wallet.account.webauthn.data.database.model.SiteWithPasskeysQuery
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) providing database operations for the "sites" table.
 * This interface defines methods for managing `SiteEntity` records as well as their relationships.
 *
 * The primary entity associated with this DAO is `SiteEntity`. It supports CRUD operations, real-time
 * data streaming with `Flow`, and additional utility methods.
 *
 * Methods:
 * - `getPasskeySize`: Returns the number of passkeys associated with a given site by its ID.
 * - `getPasskeys`: Retrieves site information and its associated passkeys based on a given URL.
 * - `getPasskeysAsFlow`: Streams site information and their associated passkeys, ordered by URL.
 * - `insert`: Inserts a new site or updates an existing site based on conflict resolution strategy.
 * - `getAll`: Retrieves all site records from the table.
 * - `getAlLAsFlow`: Streams all site records as a `Flow`.
 * - `getTableSize`: Returns the total number of rows in the "sites" table.
 * - `getTableSizeAsFlow`: Streams the total number of rows in the "sites" table as a `Flow`.
 * - `get`: Retrieves a specific site by its URL.
 * - `delete`: Deletes a specific site by its URL or ID.
 * - `clearAll`: Deletes all entries from the "sites" table.
 */
@Dao
interface SiteDao {
    /**
     * Retrieves the count of entries in the "sites" table that match the given site ID.
     *
     * @param siteId The ID of the site for which to count the entries.
     * @return The number of entries in the "sites" table with the specified site ID.
     */
    @Query("SELECT COUNT(*) FROM sites WHERE id = :siteId")
    fun getPasskeySize(siteId: Long): Int

    @Query("SELECT COUNT(*) FROM sites WHERE url = :url")
    fun getPasskeySize(url: String): Int

    /**
     * Retrieves a site and its associated passkeys from the database based on the provided URL.
     *
     * @param url The URL of the site for which to retrieve the associated passkey data.
     * @return A `SiteWithPasskeysQuery` containing the site information and its associated passkeys, or null if no matching site is found.
     */
    @Transaction
    @Query("SELECT * FROM sites WHERE url = :url")
    fun getPasskeys(url: String): SiteWithPasskeysQuery

    /**
     * Streams a list of sites along with their associated passkeys from the database.
     * The data is ordered by the URL of the sites.
     *
     * @return A Flow that emits lists of `SiteWithPasskeysQuery` objects, where each object contains
     *         a `SiteEntity` and the corresponding list of `PasskeyEntity` objects associated with it.
     */
    @Transaction
    @Query("SELECT * FROM sites ORDER BY url")
    fun getPasskeysAsFlow(): Flow<List<SiteWithPasskeysQuery>>

    /**
     * Inserts a `SiteEntity` into the database. If the entity conflicts with an existing entry
     * (same primary key), it will replace the existing entry.
     *
     * @param entity The `SiteEntity` to be inserted into the database.
     * @return The newly inserted row ID as a `Long`.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SiteEntity): Long

    /**
     * Retrieves all entries from the "sites" table in the database.
     *
     * @return A list of all `SiteEntity` objects stored in the "sites" table.
     */
    @Query("SELECT * from sites")
    suspend fun getAll(): List<SiteEntity>

    /**
     * Streams all entries from the "sites" table as a Flow.
     * This allows observing real-time updates to the list of site entities as changes occur in the database.
     *
     * @return A Flow emitting lists of `SiteEntity` objects representing all entries in the "sites" table.
     */
    @Query("SELECT * from sites")
    fun getAlLAsFlow(): Flow<List<SiteEntity>>

    /**
     * Retrieves the total number of entries in the "sites" table.
     *
     * @return The count of rows in the "sites" table as an integer.
     */
    @Query("SELECT COUNT(*) FROM sites")
    suspend fun getTableSize(): Int

    /**
     * Streams the total number of entries in the "sites" table as a Flow.
     * This allows observing real-time updates to the table size as changes occur in the database.
     *
     * @return A Flow emitting the count of rows in the "sites" table as an integer.
     */
    @Query("SELECT COUNT(*) FROM sites")
    fun getTableSizeAsFlow(): Flow<Int>

    /**
     * Retrieves a `SiteEntity` from the database based on the provided URL.
     *
     * @param url The URL of the site to retrieve.
     * @return The matching `SiteEntity` if it exists; otherwise, null.
     */
    @Query("SELECT * FROM sites WHERE url = :url")
    suspend fun get(url: String): SiteEntity?

    @Query("SELECT * FROM sites WHERE id = :siteId")
    suspend fun get(siteId: Long): SiteEntity?

    /**
     * Deletes a site entry from the database based on the specified URL.
     *
     * @param url The URL of the site to be deleted.
     */
    @Query("DELETE FROM sites WHERE url = :url")
    suspend fun delete(url: String)

    /**
     * Deletes a site entry from the database based on the specified site ID.
     *
     * @param siteId The unique identifier of the site to be deleted.
     */
    @Query("DELETE FROM sites WHERE id = :siteId")
    suspend fun delete(siteId: Long)

    /**
     * Deletes all entries from the "sites" table in the database.
     * This operation removes all stored site entities and clears the table.
     */
    @Query("DELETE FROM sites")
    suspend fun clearAll()
}
