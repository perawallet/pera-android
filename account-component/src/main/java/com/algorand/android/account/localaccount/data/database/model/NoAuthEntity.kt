package com.algorand.android.account.localaccount.data.database.model

import androidx.room.*

@Entity(tableName = "no_auth")
internal data class NoAuthEntity(
    @PrimaryKey
    @ColumnInfo("encrypted_address")
    val encryptedAddress: String
)
