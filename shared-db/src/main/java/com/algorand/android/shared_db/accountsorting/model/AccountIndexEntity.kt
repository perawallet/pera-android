package com.algorand.android.shared_db.accountsorting.model

import androidx.room.*

@Entity(tableName = "account_index_table")
data class AccountIndexEntity(

    @PrimaryKey
    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "index")
    val index: Int
)
