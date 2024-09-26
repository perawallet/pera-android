package com.algorand.android.module.account.sorting.domain.usecase

import com.algorand.android.module.account.sorting.domain.model.AccountOrderIndex

interface GetSortedLocalAccounts {
    suspend operator fun invoke(): List<AccountOrderIndex>
}
