package com.algorand.android.accountcore.ui.usecase.implementation

import com.algorand.android.accountcore.ui.model.AccountIconResource
import com.algorand.android.accountcore.ui.model.AccountIconResource.LEDGER
import com.algorand.android.accountcore.ui.model.AccountIconResource.REKEYED
import com.algorand.android.accountcore.ui.model.AccountIconResource.REKEYED_AUTH
import com.algorand.android.accountcore.ui.model.AccountIconResource.STANDARD
import com.algorand.android.accountcore.ui.model.AccountIconResource.UNDEFINED
import com.algorand.android.accountcore.ui.model.AccountIconResource.WATCH
import com.algorand.android.accountcore.ui.usecase.GetAccountIconResourceByAccountType
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import javax.inject.Inject

internal class GetAccountIconResourceByAccountTypeUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail
) : GetAccountIconResourceByAccountType {

    override fun invoke(accountType: AccountType?): AccountIconResource {
        return when (accountType) {
            AccountType.Algo25 -> STANDARD
            AccountType.LedgerBle -> LEDGER
            AccountType.Rekeyed -> REKEYED
            AccountType.RekeyedAuth -> REKEYED_AUTH
            AccountType.NoAuth -> WATCH
            null -> UNDEFINED
        }
    }

    override suspend fun invoke(address: String): AccountIconResource {
        return invoke(getAccountDetail(address).accountType)
    }
}
