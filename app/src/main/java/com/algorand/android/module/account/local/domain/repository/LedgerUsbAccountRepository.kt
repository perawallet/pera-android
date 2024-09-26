package com.algorand.android.module.account.local.domain.repository

import com.algorand.android.module.account.local.domain.model.LocalAccount
import kotlinx.coroutines.flow.Flow

internal interface LedgerUsbAccountRepository {

    fun getAllAsFlow(): Flow<List<LocalAccount.LedgerUsb>>

    fun getAccountCountAsFlow(): Flow<Int>

    suspend fun getAll(): List<LocalAccount.LedgerUsb>

    suspend fun getAccount(address: String): LocalAccount.LedgerUsb?

    suspend fun addAccount(account: LocalAccount.LedgerUsb)

    suspend fun deleteAccount(address: String)

    suspend fun deleteAllAccounts()
}
