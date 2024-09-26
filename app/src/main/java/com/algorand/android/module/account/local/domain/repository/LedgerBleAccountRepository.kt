package com.algorand.android.module.account.local.domain.repository

import com.algorand.android.module.account.local.domain.model.LocalAccount
import kotlinx.coroutines.flow.Flow

internal interface LedgerBleAccountRepository {

    fun getAllAsFlow(): Flow<List<LocalAccount.LedgerBle>>

    fun getAccountCountAsFlow(): Flow<Int>

    suspend fun getAll(): List<LocalAccount.LedgerBle>

    suspend fun getAccount(address: String): LocalAccount.LedgerBle?

    suspend fun addAccount(account: LocalAccount.LedgerBle)

    suspend fun deleteAccount(address: String)

    suspend fun deleteAllAccounts()
}
