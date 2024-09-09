package com.algorand.android.accountsorting.component.domain.usecase

import com.algorand.android.accountsorting.component.domain.model.AccountOrderIndex

interface GetSortedLocalAccounts {
    suspend operator fun invoke(): List<AccountOrderIndex>
}
