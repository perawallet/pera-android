package com.algorand.common.account.info.domain.usecase

import com.algorand.common.account.info.domain.repository.AccountInformationRepository

internal class IsThereAnyCachedSuccessAccountUseCase(
    private val accountInformationRepository: AccountInformationRepository
) : IsThereAnyCachedSuccessAccount {

    override suspend fun invoke(excludeNoAuthAccounts: Boolean): Boolean {
        return accountInformationRepository.getAllAccountInformation().any { it.value != null }
    }
}
