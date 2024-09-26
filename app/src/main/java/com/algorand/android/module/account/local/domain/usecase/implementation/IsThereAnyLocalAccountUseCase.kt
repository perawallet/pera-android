package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.usecase.*
import javax.inject.Inject

internal class IsThereAnyLocalAccountUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts
) : IsThereAnyLocalAccount {

    override suspend fun invoke(): Boolean {
        return getLocalAccounts().isNotEmpty()
    }
}
