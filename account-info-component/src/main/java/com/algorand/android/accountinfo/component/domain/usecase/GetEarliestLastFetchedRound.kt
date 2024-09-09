package com.algorand.android.accountinfo.component.domain.usecase

fun interface GetEarliestLastFetchedRound {
    suspend operator fun invoke(): Long
}
