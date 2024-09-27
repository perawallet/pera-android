package com.algorand.android.module.account.local.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ledger_usb")
internal data class LedgerUsbEntity(
    @PrimaryKey
    @ColumnInfo("encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo("ledger_usb_id")
    val ledgerUsbId: Int,

    @ColumnInfo("account_index_in_ledger")
    val accountIndexInLedger: Int
)
