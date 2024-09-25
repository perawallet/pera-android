package com.algorand.android.module.transaction.history.component.domain.model

data class ApplicationCallHistory(
    val applicationId: Long?,
    val accounts: List<String>?,
    val foreignApps: List<Long>?,
    val foreignAssets: List<Long>?,
    val onCompletion: OnCompletion?,
)
