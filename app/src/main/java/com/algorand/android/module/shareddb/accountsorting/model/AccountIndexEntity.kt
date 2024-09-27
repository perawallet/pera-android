package com.algorand.android.module.shareddb.accountsorting.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_index_table")
data class AccountIndexEntity(

    @PrimaryKey
    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "index")
    val index: Int
)
