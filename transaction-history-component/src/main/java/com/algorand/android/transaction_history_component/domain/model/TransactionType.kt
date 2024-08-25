package com.algorand.android.transaction_history_component.domain.model

enum class TransactionType(val value: String?) {
    PAY_TRANSACTION("pay"),
    ASSET_TRANSACTION("axfer"),
    APP_TRANSACTION("appl"),
    ASSET_CONFIGURATION("acfg"),
    KEYREG_TRANSACTION("keyreg"),
    UNDEFINED(null)
}
