package com.algorand.android.shared_db.accountinformation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.algorand.android.shared_db.accountinformation.model.AssetHoldingEntity.Companion.ASSET_HOLDING_TABLE_NAME
import java.math.BigInteger

@Entity(
    tableName = ASSET_HOLDING_TABLE_NAME,
    indices = [Index(value = ["encrypted_address", "asset_id"], unique = true)]
)
data class AssetHoldingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "asset_id")
    val assetId: Long,

    @ColumnInfo(name = "amount")
    val amount: BigInteger,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "is_frozen")
    val isFrozen: Boolean,

    @ColumnInfo(name = "opted_in_at_round")
    val optedInAtRound: Long?,

    @ColumnInfo(name = "opted_out_at_round")
    val optedOutAtRound: Long?,

    @ColumnInfo(name = "asset_status")
    val assetStatusEntity: AssetStatusEntity
) {

    companion object {
        const val ASSET_HOLDING_TABLE_NAME = "asset_holding_table"
    }
}
