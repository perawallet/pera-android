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

import com.algorand.common.account.local.data.database.dao.Bip39Dao
import com.algorand.common.account.local.data.mapper.entity.Bip39EntityMapper
import com.algorand.common.account.local.data.mapper.model.Bip39Mapper
import com.algorand.common.account.local.domain.model.LocalAccount.Bip39
import com.algorand.common.account.local.domain.repository.Bip39AccountRepository
import com.algorand.common.encryption.AddressEncryptionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class Bip39AccountRepositoryImpl(
    private val bip39Dao: Bip39Dao,
    private val bip39EntityMapper: Bip39EntityMapper,
    private val bip39Mapper: Bip39Mapper,
    private val addressEncryptionManager: AddressEncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Bip39AccountRepository {

    override fun getAllAsFlow(): Flow<List<Bip39>> {
        return bip39Dao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> bip39Mapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return bip39Dao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<Bip39> {
        return withContext(coroutineDispatcher) {
            val bip39Entities = bip39Dao.getAll()
            bip39Entities.map { bip39Mapper(it) }
        }
    }

    override suspend fun getAccount(address: String): Bip39? {
        return withContext(coroutineDispatcher) {
            bip39Dao.get(addressEncryptionManager.encrypt(address))?.let { bip39Mapper(it) }
        }
    }

    override suspend fun addAccount(account: Bip39) {
        withContext(coroutineDispatcher) {
            val bip39Entity = bip39EntityMapper(account)
            bip39Dao.insert(bip39Entity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            val encryptedAddress = addressEncryptionManager.encrypt(address)
            bip39Dao.delete(encryptedAddress)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            bip39Dao.clearAll()
        }
    }
}
