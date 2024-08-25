package com.algorand.android.core.component.detail.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.model.LocalAccount.Algo25
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.core.component.detail.domain.model.AccountRegistrationType
import com.algorand.android.core.component.detail.domain.usecase.GetAccountRegistrationType
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
