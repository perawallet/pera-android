package com.algorand.android.shared_db.asb.model

import androidx.room.*

@Entity(tableName = "algorand_secure_back_up")
data class AlgorandSecureBackUpEntity(

    @PrimaryKey
    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "is_backed_up")
    val isBackedUp: Boolean
)
