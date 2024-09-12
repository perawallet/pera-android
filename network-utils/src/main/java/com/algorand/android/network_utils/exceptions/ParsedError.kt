package com.algorand.android.network_utils.exceptions

data class ParsedError(
    val keyErrorMessageMap: Map<String, List<String>>,
    val message: String,
    val responseCode: Int
)
