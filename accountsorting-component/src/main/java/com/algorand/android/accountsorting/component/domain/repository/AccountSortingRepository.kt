package com.algorand.android.accountsorting.component.domain.repository

import com.algorand.android.accountsorting.component.domain.model.*

internal interface AccountSortingRepository {

    suspend fun getAllAccountOrderIndexes(): List<AccountOrderIndex>

    suspend fun setAccountOrderIndex(address: String, orderIndex: Int)

    suspend fun removeAccountOrderIndex(address: String)

    suspend fun saveAccountSortPreference(identifier: AccountSortingTypeIdentifier)

    suspend fun getAccountSortPreference(): AccountSortingTypeIdentifier
}
