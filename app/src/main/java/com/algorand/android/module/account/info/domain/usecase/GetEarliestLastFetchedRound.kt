package com.algorand.android.module.account.info.domain.usecase

fun interface GetEarliestLastFetchedRound {
    suspend operator fun invoke(): Long
}
