package com.algorand.android.module.shareddb.contact.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.algorand.android.module.shareddb.contact.model.ContactEntity

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
