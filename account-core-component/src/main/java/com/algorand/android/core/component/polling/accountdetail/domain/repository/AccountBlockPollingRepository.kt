package com.algorand.android.core.component.polling.accountdetail.domain.repository

internal interface AccountBlockPollingRepository {

    suspend fun isAccountUpdateRequired(
        localAccountAddresses: List<String>,
        latestKnownRound: Long?
    ): Result<Boolean>
}
