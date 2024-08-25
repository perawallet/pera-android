package com.algorand.android.shared_db.contact.dao

import androidx.room.*
import com.algorand.android.shared_db.contact.model.ContactEntity

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact_table")
    suspend fun getAll(): List<ContactEntity>

    @Query("SELECT * FROM contact_table WHERE name LIKE '%' || :nameQuery || '%'")
    suspend fun getUsersWithNameFiltered(nameQuery: String): List<ContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Query("DELETE FROM contact_table WHERE encrypted_address = :encryptedAddress")
    suspend fun deleteContact(encryptedAddress: String)

    @Query("DELETE FROM contact_table")
    suspend fun deleteAllContacts()

    @Query("SELECT * FROM contact_table WHERE encrypted_address = :encryptedAddress")
    suspend fun getContactByAddress(encryptedAddress: String): ContactEntity?
}
