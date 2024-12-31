package com.algorand.common.account.info.domain.usecase

import com.algorand.common.account.info.domain.repository.AccountInformationRepository

internal class IsThereAnyCachedErrorAccountUseCase(
    private val accountInformationRepository: AccountInformationRepository
) : IsThereAnyCachedErrorAccount {

    override suspend fun invoke(excludeNoAuthAccounts: Boolean): Boolean {
        return accountInformationRepository.getAllAccountInformation().any { it.value == null }
    }
}
