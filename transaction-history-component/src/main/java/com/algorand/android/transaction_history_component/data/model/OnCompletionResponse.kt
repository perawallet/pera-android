package com.algorand.android.transaction_history_component.data.model

import com.google.gson.annotations.SerializedName

enum class OnCompletionResponse {

    @SerializedName("optin")
    OPT_IN,

    @SerializedName("noop")
    NO_OP,

    @SerializedName("closeout")
    CLOSE_OUT,

    @SerializedName("clear")
    CLEAR_STATE,

    @SerializedName("update")
    UPDATE_APPLICATION,

    @SerializedName("delete")
    DELETE_APPLICATION,

    UNKNOWN
}
