package com.algorand.android.account.localaccount.domain.repository

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import kotlinx.coroutines.flow.Flow

internal interface NoAuthAccountRepository {

    fun getAllAsFlow(): Flow<List<LocalAccount.NoAuth>>

    fun getAccountCountAsFlow(): Flow<Int>

    suspend fun getAll(): List<LocalAccount.NoAuth>

    suspend fun getAccount(address: String): LocalAccount.NoAuth?

    suspend fun addAccount(account: LocalAccount.NoAuth)

    suspend fun deleteAccount(address: String)

    suspend fun deleteAllAccounts()
}
