package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.*
import javax.inject.Inject

internal class IsThereAnyLocalAccountUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts
) : IsThereAnyLocalAccount {

    override suspend fun invoke(): Boolean {
        return getLocalAccounts().isNotEmpty()
    }
}
