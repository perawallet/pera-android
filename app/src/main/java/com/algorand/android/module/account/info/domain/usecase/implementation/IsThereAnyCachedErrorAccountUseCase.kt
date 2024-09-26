package com.algorand.android.module.account.info.domain.usecase.implementation

import com.algorand.android.module.account.info.domain.repository.AccountInformationRepository
import com.algorand.android.module.account.info.domain.usecase.IsThereAnyCachedErrorAccount
import javax.inject.Inject

internal class IsThereAnyCachedErrorAccountUseCase @Inject constructor(
    private val accountInformationRepository: AccountInformationRepository
) : IsThereAnyCachedErrorAccount {

    override suspend fun invoke(excludeNoAuthAccounts: Boolean): Boolean {
        return accountInformationRepository.getAllAccountInformation().any { it.value == null }
    }
}
