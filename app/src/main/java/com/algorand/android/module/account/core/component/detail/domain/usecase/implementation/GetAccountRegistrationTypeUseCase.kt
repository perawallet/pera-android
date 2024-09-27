package com.algorand.android.module.account.core.component.detail.domain.usecase.implementation

import com.algorand.android.module.account.core.component.detail.domain.model.AccountRegistrationType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.model.LocalAccount.Algo25
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class GetAccountRegistrationTypeUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts
) : GetAccountRegistrationType {

    override suspend fun invoke(address: String): AccountRegistrationType? {
        return when (getLocalAccounts().firstOrNull { it.address == address }) {
            is Algo25 -> AccountRegistrationType.Algo25
            is LocalAccount.LedgerBle -> AccountRegistrationType.LedgerBle
            is LocalAccount.NoAuth -> AccountRegistrationType.NoAuth
            is LocalAccount.LedgerUsb -> TODO("Not yet implemented")
            else -> null
        }
    }
}
