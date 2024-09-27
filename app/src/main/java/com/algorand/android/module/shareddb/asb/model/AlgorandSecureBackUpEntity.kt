package com.algorand.android.module.shareddb.asb.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "algorand_secure_back_up")
data class AlgorandSecureBackUpEntity(

    @PrimaryKey
    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "is_backed_up")
    val isBackedUp: Boolean
)
