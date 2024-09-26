package com.algorand.android.module.account.info.domain.usecase.implementation

import com.algorand.android.module.account.info.domain.repository.AccountInformationRepository
import com.algorand.android.module.account.info.domain.usecase.IsThereAnyCachedSuccessAccount
import javax.inject.Inject

internal class IsThereAnyCachedSuccessAccountUseCase @Inject constructor(
    private val accountInformationRepository: AccountInformationRepository
) : IsThereAnyCachedSuccessAccount {

    override suspend fun invoke(excludeNoAuthAccounts: Boolean): Boolean {
        return accountInformationRepository.getAllAccountInformation().any { it.value != null }
    }
}
