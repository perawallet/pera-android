package com.algorand.android.module.account.local.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "no_auth")
internal data class NoAuthEntity(
    @PrimaryKey
    @ColumnInfo("encrypted_address")
    val encryptedAddress: String
)
