package com.algorand.android.module.account.core.ui.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.ui.model.AccountDisplayName

interface GetAccountDisplayName {
    suspend operator fun invoke(address: String): AccountDisplayName

    suspend operator fun invoke(address: String, name: String?, type: AccountType): AccountDisplayName

    suspend operator fun invoke(accountDetail: AccountDetail): AccountDisplayName
}
