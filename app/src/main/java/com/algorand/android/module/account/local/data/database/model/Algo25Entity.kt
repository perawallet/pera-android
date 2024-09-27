package com.algorand.android.module.account.local.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "algo_25")
internal data class Algo25Entity(
    @PrimaryKey
    @ColumnInfo("encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo("encrypted_secret_key")
    val encryptedSecretKey: String
)
