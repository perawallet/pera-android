package com.algorand.android.accountcore.ui.accountsorting.domain.usecase

import com.algorand.android.accountcore.ui.accountsorting.domain.model.AccountSortingType

interface GetDefaultAccountSortingType {
    operator fun invoke(): AccountSortingType
}
