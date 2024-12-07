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

import com.algorand.common.account.local.data.database.dao.NoAuthDao
import com.algorand.common.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.common.account.local.data.mapper.model.NoAuthMapper
import com.algorand.common.account.local.domain.model.LocalAccount.NoAuth
import com.algorand.common.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.common.encryption.AddressEncryptionManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class NoAuthAccountRepositoryImpl(
    private val noAuthDao: NoAuthDao,
    private val noAuthEntityMapper: NoAuthEntityMapper,
    private val noAuthMapper: NoAuthMapper,
    private val addressEncryptionManager: AddressEncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoAuthAccountRepository {

    override fun getAllAsFlow(): Flow<List<NoAuth>> {
        return noAuthDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> noAuthMapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return noAuthDao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<NoAuth> {
        return withContext(coroutineDispatcher) {
            val noAuthEntities = noAuthDao.getAll()
            noAuthEntities.map { noAuthMapper(it) }
        }
    }

    override suspend fun getAccount(address: String): NoAuth? {
        return withContext(coroutineDispatcher) {
            val noAuthEntity = noAuthDao.get(addressEncryptionManager.encrypt(address))
            noAuthEntity?.let { noAuthMapper(it) }
        }
    }

    override suspend fun addAccount(account: NoAuth) {
        withContext(coroutineDispatcher) {
            val noAuthEntity = noAuthEntityMapper(account)
            noAuthDao.insert(noAuthEntity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = addressEncryptionManager.encrypt(address)
            noAuthDao.delete(encryptedAddress)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            noAuthDao.clearAll()
        }
    }
}
