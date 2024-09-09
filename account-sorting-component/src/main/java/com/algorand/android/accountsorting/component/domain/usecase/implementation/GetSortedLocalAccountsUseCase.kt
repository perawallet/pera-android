package com.algorand.android.accountsorting.component.domain.usecase.implementation

import com.algorand.android.accountsorting.component.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.accountsorting.component.domain.model.AccountOrderIndex
import com.algorand.android.accountsorting.component.domain.repository.AccountSortingRepository
import javax.inject.Inject

internal class GetSortedLocalAccountsUseCase @Inject constructor(
    private val accountSortingRepository: AccountSortingRepository
) : GetSortedLocalAccounts {

    override suspend fun invoke(): List<AccountOrderIndex> {
        return accountSortingRepository.getAllAccountOrderIndexes()
            .sortedBy { it.index }
            .partition { it.index != NOT_INITIALIZED_ACCOUNT_INDEX }
            .let { (initializedAccounts, notInitializedAccounts) ->
                initializedAccounts + notInitializedAccounts
            }
    }

    companion object {
        private const val NOT_INITIALIZED_ACCOUNT_INDEX = -1
    }
}
