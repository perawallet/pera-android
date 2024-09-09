package com.algorand.android.accountsorting.component.domain.usecase

import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier

fun interface GetAccountSortingTypeIdentifier {
    suspend operator fun invoke(): AccountSortingTypeIdentifier
}
