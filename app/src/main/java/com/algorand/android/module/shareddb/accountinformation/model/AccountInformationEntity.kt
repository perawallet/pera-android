package com.algorand.android.module.shareddb.accountinformation.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.algorand.android.module.shareddb.accountinformation.model.AccountInformationEntity.Companion.ACCOUNT_INFORMATION_TABLE_NAME
import java.math.BigInteger

@Entity(tableName = ACCOUNT_INFORMATION_TABLE_NAME)
data class AccountInformationEntity(
    @PrimaryKey
    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "algo_amount")
    val algoAmount: BigInteger,

    @ColumnInfo(name = "opted_in_apps_count")
    val optedInAppsCount: Int,

    @ColumnInfo(name = "apps_total_extra_pages")
    val appsTotalExtraPages: Int,

    @ColumnInfo(name = "auth_address")
    val authAddress: String?,

    @ColumnInfo(name = "created_at_round")
    val createdAtRound: Long?,

    @ColumnInfo(name = "last_fetched_round")
    val lastFetchedRound: Long,

    @ColumnInfo(name = "total_created_apps_count")
    val totalCreatedAppsCount: Int,

    @ColumnInfo(name = "total_created_assets_count")
    val totalCreatedAssetsCount: Int,

    @ColumnInfo(name = "app_state_schema_num_byte_slice")
    val appStateNumByteSlice: Long?,

    @ColumnInfo(name = "app_state_schema_num_uint")
    val appStateSchemaUint: Long?
) {

    companion object {
        const val ACCOUNT_INFORMATION_TABLE_NAME = "account_information"
    }
}
