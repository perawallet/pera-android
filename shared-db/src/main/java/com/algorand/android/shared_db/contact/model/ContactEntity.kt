package com.algorand.android.shared_db.contact.model

import androidx.room.*
import com.algorand.android.shared_db.contact.model.ContactEntity.Companion.CONTACT_TABLE_NAME

@Entity(tableName = CONTACT_TABLE_NAME)
data class ContactEntity(

    @PrimaryKey
    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "image_uri")
    val imageUri: String?
) {

    companion object {
        const val CONTACT_TABLE_NAME = "contact_table"
    }
}
