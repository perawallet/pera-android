package com.algorand.android.module.shareddb.assetdetail.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_info")
data class CustomInfoEntity(

    @PrimaryKey
    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "custom_name")
    val customName: String?
)
