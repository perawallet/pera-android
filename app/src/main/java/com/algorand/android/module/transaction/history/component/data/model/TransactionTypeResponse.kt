package com.algorand.android.module.transaction.history.component.data.model

import com.google.gson.annotations.SerializedName

internal enum class TransactionTypeResponse {

    @SerializedName("pay")
    PAY_TRANSACTION,

    @SerializedName("axfer")
    ASSET_TRANSACTION,

    @SerializedName("appl")
    APP_TRANSACTION,

    @SerializedName("acfg")
    ASSET_CONFIGURATION,

    @SerializedName("keyreg")
    KEYREG,

    UNDEFINED
}
