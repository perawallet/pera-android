package com.algorand.android.module.account.info.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

internal data class AccountInformationResponsePayloadResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("amount")
    val amount: BigInteger?,
    @SerializedName("participation")
    val participation: ParticipationResponse?,
    @SerializedName("auth-addr")
    val rekeyAdminAddress: String?,
    @SerializedName("assets")
    val allAssetHoldingList: List<AssetHoldingResponse>?,
    @SerializedName("created-at-round")
    val createdAtRound: Long?,
    @SerializedName("apps-total-schema")
    val appStateSchemaResponse: AppStateSchemaResponse?,
    @SerializedName("apps-total-extra-pages")
    val appsTotalExtraPages: Int?,
    @SerializedName("total-apps-opted-in")
    val totalAppsOptedIn: Int?,
    @SerializedName("total-assets-opted-in")
    val totalAssetsOptedIn: Int?,
    @SerializedName("total-created-apps")
    val totalCreatedApps: Int?,
    @SerializedName("total-created-assets")
    val totalCreatedAssets: Int?,
)
