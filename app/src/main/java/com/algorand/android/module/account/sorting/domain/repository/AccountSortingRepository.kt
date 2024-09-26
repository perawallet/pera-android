package com.algorand.android.module.account.sorting.domain.repository

import com.algorand.android.module.account.sorting.domain.model.AccountOrderIndex
import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier

internal interface AccountSortingRepository {

    suspend fun getAllAccountOrderIndexes(): List<AccountOrderIndex>

    suspend fun setAccountOrderIndex(address: String, orderIndex: Int)

    suspend fun removeAccountOrderIndex(address: String)

    suspend fun saveAccountSortPreference(identifier: AccountSortingTypeIdentifier)

    suspend fun getAccountSortPreference(): AccountSortingTypeIdentifier
}
