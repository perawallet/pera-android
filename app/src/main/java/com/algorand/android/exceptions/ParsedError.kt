package com.algorand.android.exceptions

data class ParsedError(
    val keyErrorMessageMap: Map<String, List<String>>,
    val message: String,
    val responseCode: Int
)
