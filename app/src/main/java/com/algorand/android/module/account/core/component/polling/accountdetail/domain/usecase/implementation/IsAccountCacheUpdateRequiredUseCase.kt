package com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.IsAccountCacheUpdateRequired
import com.algorand.android.accountinfo.component.domain.usecase.GetEarliestLastFetchedRound
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.repository.AccountBlockPollingRepository
import javax.inject.Inject

internal class IsAccountCacheUpdateRequiredUseCase @Inject constructor(
    private val getEarliestLastFetchedRound: GetEarliestLastFetchedRound,
    private val getLocalAccounts: GetLocalAccounts,
    private val blockPollingRepository: AccountBlockPollingRepository
) : IsAccountCacheUpdateRequired {

    override suspend fun invoke(): Result<Boolean> {
        val localAccountAddresses = getLocalAccounts().map { it.address }
        val earliestFetchedRound = getEarliestLastFetchedRound()
        return blockPollingRepository.isAccountUpdateRequired(localAccountAddresses, earliestFetchedRound)
    }
}
