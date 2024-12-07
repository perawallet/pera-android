/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.account.local.data.repository

import com.algorand.common.account.local.data.database.dao.LedgerBleDao
import com.algorand.common.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.common.account.local.data.mapper.model.LedgerBleMapper
import com.algorand.common.account.local.domain.model.LocalAccount.LedgerBle
import com.algorand.common.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.common.encryption.AddressEncryptionManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class LedgerBleAccountRepositoryImpl(
    private val ledgerBleDao: LedgerBleDao,
    private val ledgerBleEntityMapper: LedgerBleEntityMapper,
    private val ledgerBleMapper: LedgerBleMapper,
    private val addressEncryptionManager: AddressEncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LedgerBleAccountRepository {

    override fun getAllAsFlow(): Flow<List<LedgerBle>> {
        return ledgerBleDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> ledgerBleMapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return ledgerBleDao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<LedgerBle> {
        return withContext(coroutineDispatcher) {
            val ledgerBleEntities = ledgerBleDao.getAll()
            ledgerBleEntities.map { ledgerBleMapper(it) }
        }
    }

    override suspend fun getAccount(address: String): LedgerBle? {
        return withContext(coroutineDispatcher) {
            val ledgerBleEntity = ledgerBleDao.get(addressEncryptionManager.encrypt(address))
            ledgerBleEntity?.let { ledgerBleMapper(it) }
        }
    }

    override suspend fun addAccount(account: LedgerBle) {
        withContext(coroutineDispatcher) {
            val ledgerBleEntity = ledgerBleEntityMapper(account)
            ledgerBleDao.insert(ledgerBleEntity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = addressEncryptionManager.encrypt(address)
            ledgerBleDao.delete(encryptedAddress)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            ledgerBleDao.clearAll()
        }
    }
}
