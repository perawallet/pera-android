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

package com.algorand.wallet.account.local.data.repository

import com.algorand.wallet.account.local.data.database.dao.HdKeyDao
import com.algorand.wallet.account.local.data.mapper.entity.HdKeyEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.HdKeyMapper
import com.algorand.wallet.account.local.domain.model.LocalAccount.HdKey
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class HdKeyAccountRepositoryImpl @Inject constructor(
    private val hdKeyDao: HdKeyDao,
    private val hdKeyEntityMapper: HdKeyEntityMapper,
    private val hdKeyMapper: HdKeyMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HdKeyAccountRepository {

    override fun getAllAsFlow(): Flow<List<HdKey>> {
        return hdKeyDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> hdKeyMapper(entity) }
        }
    }

    override fun getAccountCountAsFlow(): Flow<Int> {
        return hdKeyDao.getTableSizeAsFlow()
    }

    override suspend fun getAll(): List<HdKey> {
        return withContext(coroutineDispatcher) {
            val hdKeyEntities = hdKeyDao.getAll()
            hdKeyEntities.map { hdKeyMapper(it) }
        }
    }

    override suspend fun getAccount(address: String): HdKey? {
        return withContext(coroutineDispatcher) {
            hdKeyDao.get(address)?.let { hdKeyMapper(it) }
        }
    }

    override suspend fun addAccount(account: HdKey, privateKey: ByteArray) {
        withContext(coroutineDispatcher) {
            val hdKeyEntity = hdKeyEntityMapper(account, privateKey)
            hdKeyDao.insert(hdKeyEntity)
        }
    }

    override suspend fun deleteAccount(address: String) {
        withContext(coroutineDispatcher) {
            hdKeyDao.delete(address)
        }
    }

    override suspend fun deleteAllAccounts() {
        withContext(coroutineDispatcher) {
            hdKeyDao.clearAll()
        }
    }
}
