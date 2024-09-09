package com.algorand.android.accountcore.ui.usecase

import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.detail.domain.model.AccountType

interface GetAccountDisplayName {
    suspend operator fun invoke(address: String): AccountDisplayName

    suspend operator fun invoke(address: String, name: String?, type: AccountType): AccountDisplayName

    suspend operator fun invoke(accountDetail: AccountDetail): AccountDisplayName
}
