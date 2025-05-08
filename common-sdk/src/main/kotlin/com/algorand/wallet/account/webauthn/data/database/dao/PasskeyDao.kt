package com.algorand.wallet.account.webauthn.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) providing database operations for the `PasskeyEntity` table.
 * This interface defines methods for interacting with the stored passkey data in the database.
 *
 * The primary entity associated with this DAO is `PasskeyEntity`.
 * It supports operations such as inserting, updating, deleting, and querying passkey records.
 *
 * Methods:
 * - `insert`: Inserts a `PasskeyEntity` into the database. Replaces the entity if a conflict occurs.
 * - `update`: Updates the information of an existing `PasskeyEntity`.
 * - `getAll`: Retrieves all `PasskeyEntity` records from the database.
 * - `getAlLAsFlow`: Streams a list of all `PasskeyEntity` records as a `Flow`.
 * - `getTableSize`: Returns the number of items in the `PasskeyEntity` table.
 * - `getTableSizeAsFlow`: Streams the count of items in the `PasskeyEntity` table as a `Flow`.
 * - `get`: Retrieves a `PasskeyEntity` by its `credentialId`.
 * - `delete`: Deletes a `PasskeyEntity` based on its `credentialId`.
 * - `clearAll`: Deletes all records from the `PasskeyEntity` table.
 */
@Dao
interface PasskeyDao {
    /**
     * Inserts a `PasskeyEntity` into the database. If the entity conflicts with an existing entry
     * (same primary key), it will replace the existing entry.
     *
     * @param entity The `PasskeyEntity` to be inserted into the database.
     * @return The newly inserted row ID as a `Long`.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PasskeyEntity): Long

    /**
     * Updates an existing `PasskeyEntity` in the database. The entity to be updated
     * is matched based on its primary key. If an entity with the same primary key
     * exists, it is updated with the provided entity.
     *
     * @param entity The `PasskeyEntity` containing the updated data.
     */
    @Update
    suspend fun update(entity: PasskeyEntity)

    /**
     * Retrieves all `PasskeyEntity` entries from the database.
     *
     * @return A list of all `PasskeyEntity` objects stored in the database.
     */
    @Query("SELECT * from passkeys")
    suspend fun getAll(): List<PasskeyEntity>

    /**
     * Retrieves all `PasskeyEntity` entries from the database as a Flow.
     * This allows observing changes to the list of passkeys in real-time.
     *
     * @return A Flow that emits lists of all `PasskeyEntity` objects stored in the database.
     */
    @Query("SELECT * from passkeys")
    fun getAlLAsFlow(): Flow<List<PasskeyEntity>>

    /**
     * Retrieves the total number of entries in the "passkeys" table.
     *
     * @return The count of rows in the "passkeys" table as an integer.
     */
    @Query("SELECT COUNT(*) FROM passkeys")
    suspend fun getTableSize(): Int

    /**
     * Returns the total number of entries in the "passkeys" table as a Flow.
     * This allows observing real-time updates to the row count as changes occur in the table.
     *
     * @return A Flow emitting the count of rows in the "passkeys" table as an integer.
     */
    @Query("SELECT COUNT(*) FROM passkeys")
    fun getTableSizeAsFlow(): Flow<Int>

    /**
     * Retrieves a `PasskeyEntity` from the database based on the provided credential ID.
     *
     * @param credentialId The unique credential ID associated with the passkey to retrieve.
     * @return The matching `PasskeyEntity` if it exists; otherwise, null.
     */
    @Query("SELECT * FROM passkeys WHERE credential_id = :credentialId")
    suspend fun get(credentialId: String): PasskeyEntity?

    /**
     * Deletes a passkey entry from the database based on the given credential ID.
     *
     * @param credentialId The unique identifier of the passkey to be deleted.
     */
    @Query("DELETE FROM passkeys WHERE credential_id = :credentialId")
    suspend fun delete(credentialId: String)

    /**
     * Deletes all entries from the "passkeys" table in the database.
     * This operation removes all stored passkey entities, effectively clearing the table.
     */
    @Query("DELETE FROM passkeys")
    suspend fun clearAll()
}
