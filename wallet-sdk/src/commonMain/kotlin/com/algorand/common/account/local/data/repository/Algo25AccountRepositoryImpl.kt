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

import com.algorand.common.account.local.data.database.dao.Algo25Dao
import com.algorand.common.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.common.account.local.data.mapper.model.algo25.Algo25Mapper
import com.algorand.common.account.local.domain.model.LocalAccount.Algo25
import com.algorand.common.account.local.domain.repository.Algo25AccountRepository
import com.algorand.common.encryption.AddressEncryptionManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal class Algo25AccountRepositoryImpl(
    private val algo25Dao: Algo25Dao,
    private val algo25EntityMapper: Algo25EntityMapper,
    private val algo25Mapper: Algo25Mapper,
    private val addressEncryptionManager: AddressEncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Algo25AccountRepository {

    override fun getAllAsFlow(): Flow<List<Algo25>> {
        return algo25Dao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> algo25Mapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return algo25Dao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<Algo25> {
        return withContext(coroutineDispatcher) {
            val algo25Entities = algo25Dao.getAll()
            algo25Entities.map { algo25Mapper(it) }
        }
    }

    override suspend fun getAccount(address: String): Algo25? {
        return withContext(coroutineDispatcher) {
            algo25Dao.get(addressEncryptionManager.encrypt(address))?.let { algo25Mapper(it) }
        }
    }

    override suspend fun addAccount(account: Algo25) {
        withContext(coroutineDispatcher) {
            val algo25Entity = algo25EntityMapper(account)
            algo25Dao.insert(algo25Entity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = addressEncryptionManager.encrypt(address)
            algo25Dao.delete(encryptedAddress)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            algo25Dao.clearAll()
        }
    }
}
