package com.algorand.android.module.transaction.history.component.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

internal data class AssetConfigurationParamsResponse(
    @SerializedName("creator")
    val creator: String?,
    @SerializedName("decimals")
    val decimals: BigInteger?,
    @SerializedName("default-frozen")
    val defaultFrozen: Boolean?,
    @SerializedName("metadata-hash")
    val metadataHash: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("name-b64")
    val nameB64: String?,
    @SerializedName("total")
    val maxSupply: BigInteger?,
    @SerializedName("unit-name")
    val unitName: String?,
    @SerializedName("unit-name-b64")
    val unitNameB64: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("url-b64")
    val urlB64: String?
)
