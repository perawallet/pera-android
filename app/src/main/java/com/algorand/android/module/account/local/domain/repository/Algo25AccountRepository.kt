package com.algorand.android.module.account.local.domain.repository

import com.algorand.android.module.account.local.domain.model.LocalAccount
import kotlinx.coroutines.flow.Flow

internal interface Algo25AccountRepository {

    fun getAllAsFlow(): Flow<List<LocalAccount.Algo25>>

    fun getAccountCountAsFlow(): Flow<Int>

    suspend fun getAll(): List<LocalAccount.Algo25>

    suspend fun getAccount(address: String): LocalAccount.Algo25?

    suspend fun addAccount(account: LocalAccount.Algo25)

    suspend fun deleteAccount(address: String)

    suspend fun deleteAllAccounts()
}
