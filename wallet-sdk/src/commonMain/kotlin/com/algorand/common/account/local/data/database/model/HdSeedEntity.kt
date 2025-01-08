package com.algorand.common.account.local.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hd_seeds",
    indices = [
        Index(value = ["encrypted_mnemonic_entropy"], unique = true),
        Index(value = ["encrypted_seed"], unique = true)
    ]
)
internal data class HdSeedEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("seed_id")
    val seedId: Int,

    @ColumnInfo("encrypted_mnemonic_entropy")
    val encryptedMnemonicEntropy: String,

    @ColumnInfo("encrypted_seed")
    val encryptedSeed: ByteArray,

    @ColumnInfo("entropy_custom_name")
    val entropyCustomName: String
)
