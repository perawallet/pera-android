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

import com.algorand.wallet.account.custom.data.database.dao.CustomAccountInfoDao
import com.algorand.wallet.account.custom.data.mapper.entity.CustomAccountInfoEntityMapper
import com.algorand.wallet.account.custom.data.mapper.model.CustomAccountInfoMapper
import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import com.algorand.wallet.account.custom.domain.repository.CustomAccountInfoRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class CustomAccountInfoRepositoryImpl @Inject constructor(
    private val customAccountInfoDao: CustomAccountInfoDao,
    private val customAccountInfoMapper: CustomAccountInfoMapper,
    private val customAccountInfoEntityMapper: CustomAccountInfoEntityMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CustomAccountInfoRepository {

    override suspend fun getCustomInfo(address: String): CustomAccountInfo {
        return withContext(coroutineDispatcher) {
            val customInfoEntity = customAccountInfoDao.getOrNull(address)
            customAccountInfoMapper(address, customInfoEntity)
        }
    }

    override suspend fun getCustomInfoOrNull(address: String): CustomAccountInfo? {
        return withContext(coroutineDispatcher) {
            val customInfoEntity = customAccountInfoDao.getOrNull(address) ?: return@withContext null
            customAccountInfoMapper(address, customInfoEntity)
        }
    }

    override suspend fun setCustomInfo(customAccountInfo: CustomAccountInfo) {
        withContext(coroutineDispatcher) {
            val entity = customAccountInfoEntityMapper(customAccountInfo)
            customAccountInfoDao.insert(entity)
        }
    }

    override suspend fun setCustomName(address: String, name: String) {
        withContext(coroutineDispatcher) {
            customAccountInfoDao.updateCustomName(address, name)
        }
    }

    override suspend fun getCustomName(address: String): String? {
        return withContext(coroutineDispatcher) {
            customAccountInfoDao.getCustomName(address)
        }
    }

    override suspend fun deleteCustomInfo(address: String) {
        withContext(coroutineDispatcher) {
            customAccountInfoDao.delete(address)
        }
    }

    override suspend fun getNotBackedUpAccounts(): Set<String> {
        return withContext(coroutineDispatcher) {
            customAccountInfoDao.getNotBackedUpAddresses().toSet()
        }
    }

    override suspend fun getBackedUpAccounts(): Set<String> {
        return withContext(coroutineDispatcher) {
            customAccountInfoDao.getBackedUpAddresses().toSet()
        }
    }

    override suspend fun setAddressesBackedUp(addresses: Set<String>) {
        return withContext(coroutineDispatcher) {
            customAccountInfoDao.setAddressesBackedUp(addresses.toList())
        }
    }

    override suspend fun isAccountBackedUp(accountAddress: String): Boolean {
        return withContext(coroutineDispatcher) {
            customAccountInfoDao.isAccountBackedUp(accountAddress)
        }
    }

    override suspend fun getAllAccountOrderIndexes(): List<AccountOrderIndex> {
        return withContext(coroutineDispatcher) {
            customAccountInfoDao.getAll().map {
                AccountOrderIndex(it.algoAddress, it.orderIndex)
            }
        }
    }

    override suspend fun setOrderIndex(address: String, orderIndex: Int) {
        withContext(coroutineDispatcher) {
            customAccountInfoDao.updateOrderIndex(address, orderIndex)
        }
    }
}
