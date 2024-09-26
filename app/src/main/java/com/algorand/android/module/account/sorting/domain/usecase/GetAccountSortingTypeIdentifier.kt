package com.algorand.android.module.account.sorting.domain.usecase

import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier

fun interface GetAccountSortingTypeIdentifier {
    suspend operator fun invoke(): AccountSortingTypeIdentifier
}
