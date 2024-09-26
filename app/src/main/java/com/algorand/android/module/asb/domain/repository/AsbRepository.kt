package com.algorand.android.module.asb.domain.repository

import kotlinx.coroutines.flow.Flow

internal interface AsbRepository {

    fun getBackedUpAccountAddressesFlow(): Flow<Set<String>>

    suspend fun setBackedUpBulk(backedUpAccounts: Set<String>)

    suspend fun setBackedUp(accountAddress: String, isBackedUp: Boolean)

    suspend fun getBackedUpStatus(accountAddress: String): Boolean

    suspend fun getBackedUpAccounts(): Set<String>

    suspend fun removeAccount(accountAddress: String)
}
