package com.algorand.android.modules.accountcore.ui.usecase

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType

interface GetAccountDisplayName {
    suspend operator fun invoke(address: String): AccountDisplayName

    suspend operator fun invoke(address: String, name: String?, type: AccountType): AccountDisplayName

    suspend operator fun invoke(accountDetail: AccountDetail): AccountDisplayName
}
