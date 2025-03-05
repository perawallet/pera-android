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

package com.algorand.wallet.account.custom.data.repository

import com.algorand.wallet.account.custom.data.database.dao.CustomInfoDao
import com.algorand.wallet.account.custom.data.mapper.entity.CustomInfoEntityMapper
import com.algorand.wallet.account.custom.data.mapper.model.CustomInfoMapper
import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.model.CustomInfo
import com.algorand.wallet.account.custom.domain.repository.CustomInfoRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class CustomInfoRepositoryImpl @Inject constructor(
    private val customInfoDao: CustomInfoDao,
    private val customInfoMapper: CustomInfoMapper,
    private val customInfoEntityMapper: CustomInfoEntityMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CustomInfoRepository {

    override suspend fun getCustomInfo(address: String): CustomInfo {
        return withContext(coroutineDispatcher) {
            val customInfoEntity = customInfoDao.getOrNull(address)
            customInfoMapper(address, customInfoEntity)
        }
    }

    override suspend fun getCustomInfoOrNull(address: String): CustomInfo? {
        return withContext(coroutineDispatcher) {
            val customInfoEntity = customInfoDao.getOrNull(address) ?: return@withContext null
            customInfoMapper(address, customInfoEntity)
        }
    }

    override suspend fun setCustomInfo(customInfo: CustomInfo) {
        withContext(coroutineDispatcher) {
            val entity = customInfoEntityMapper(customInfo)
            customInfoDao.insert(entity)
        }
    }

    override suspend fun setCustomName(address: String, name: String) {
        withContext(coroutineDispatcher) {
            customInfoDao.updateCustomName(address, name)
        }
    }

    override suspend fun getCustomName(address: String): String? {
        return withContext(coroutineDispatcher) {
            customInfoDao.getCustomName(address)
        }
    }

    override suspend fun deleteCustomInfo(address: String) {
        withContext(coroutineDispatcher) {
            customInfoDao.delete(address)
        }
    }

    override suspend fun getNotBackedUpAccounts(): Set<String> {
        return withContext(coroutineDispatcher) {
            customInfoDao.getNotBackedUpAddresses().toSet()
        }
    }

    override suspend fun getBackedUpAccounts(): Set<String> {
        return withContext(coroutineDispatcher) {
            customInfoDao.getBackedUpAddresses().toSet()
        }
    }

    override suspend fun isAccountBackedUp(accountAddress: String): Boolean {
        return withContext(coroutineDispatcher) {
            customInfoDao.isAccountBackedUp(accountAddress)
        }
    }

    override suspend fun getAllAccountOrderIndexes(): List<AccountOrderIndex> {
        return withContext(coroutineDispatcher) {
            customInfoDao.getAll().map {
                AccountOrderIndex(it.algoAddress, it.orderIndex)
            }
        }
    }

    override suspend fun setOrderIndex(address: String, orderIndex: Int) {
        withContext(coroutineDispatcher) {
            customInfoDao.updateOrderIndex(address, orderIndex)
        }
    }
}
