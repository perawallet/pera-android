package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.*
import javax.inject.Inject

internal class IsThereAnyAccountWithAddressUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts
) : IsThereAnyAccountWithAddress {

    override suspend fun invoke(address: String): Boolean {
        return getLocalAccounts().any { it.address == address }
    }
}
