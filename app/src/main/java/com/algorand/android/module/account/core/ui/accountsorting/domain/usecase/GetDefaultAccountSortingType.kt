package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase

import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountSortingType

interface GetDefaultAccountSortingType {
    operator fun invoke(): AccountSortingType
}
