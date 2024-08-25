package com.algorand.android.transaction_history_component.domain.model

enum class OnCompletion(val value: String?) {
    OPT_IN("optin"),
    NO_OP("noop"),
    CLOSE_OUT("closeout"),
    CLEAR_STATE("clear"),
    UPDATE_APPLICATION("update"),
    DELETE_APPLICATION("delete"),
    UNKNOWN(null)
}
