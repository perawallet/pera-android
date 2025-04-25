/*
 * Copyright 2025 Pera Wallet, LDA
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

import com.algorand.wallet.account.custom.data.database.dao.CustomHdSeedInfoDao
import com.algorand.wallet.account.custom.data.mapper.entity.CustomHdSeedInfoEntityMapper
import com.algorand.wallet.account.custom.data.mapper.model.CustomHdSeedInfoMapper
import com.algorand.wallet.account.custom.domain.model.CustomHdSeedInfo
import com.algorand.wallet.account.custom.domain.model.HdSeedOrderIndex
import com.algorand.wallet.account.custom.domain.repository.CustomHdSeedInfoRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class CustomHdSeedInfoRepositoryImpl @Inject constructor(
    private val customHdSeedInfoDao: CustomHdSeedInfoDao,
    private val customHdSeedInfoMapper: CustomHdSeedInfoMapper,
    private val customHdSeedInfoEntityMapper: CustomHdSeedInfoEntityMapper,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CustomHdSeedInfoRepository {

    override suspend fun getCustomInfo(seedId: Int): CustomHdSeedInfo? {
        return withContext(coroutineDispatcher) {
            val customInfoEntity = customHdSeedInfoDao.getOrNull(seedId)
            customInfoEntity?.let { customHdSeedInfoMapper(it) }
        }
    }

    override suspend fun getCustomInfoOrNull(seedId: Int): CustomHdSeedInfo? {
        return withContext(coroutineDispatcher) {
            val customHdSeedInfoEntity = customHdSeedInfoDao.getOrNull(seedId) ?: return@withContext null
            customHdSeedInfoMapper(customHdSeedInfoEntity)
        }
    }

    override suspend fun setCustomInfo(info: CustomHdSeedInfo) {
        withContext(coroutineDispatcher) {
            val entity = customHdSeedInfoEntityMapper(info)
            customHdSeedInfoDao.insert(entity)
        }
    }

    override suspend fun setCustomName(seedId: Int, name: String) {
        withContext(coroutineDispatcher) {
            customHdSeedInfoDao.updateCustomName(seedId, name)
        }
    }

    override suspend fun getCustomName(seedId: Int): String? {
        return withContext(coroutineDispatcher) {
            customHdSeedInfoDao.getCustomName(seedId)
        }
    }

    override suspend fun deleteCustomInfo(seedId: Int) {
        withContext(coroutineDispatcher) {
            customHdSeedInfoDao.delete(seedId)
        }
    }

    override suspend fun getNotBackedUpHdSeeds(): Set<Int> {
        return withContext(coroutineDispatcher) {
            customHdSeedInfoDao.getNotBackedUpSeedIds().toSet()
        }
    }

    override suspend fun getBackedUpHdSeeds(): Set<Int> {
        return withContext(coroutineDispatcher) {
            customHdSeedInfoDao.getBackedUpSeedIds().toSet()
        }
    }

    override suspend fun isHdSeedBackedUp(seedId: Int): Boolean {
        return withContext(coroutineDispatcher) {
            customHdSeedInfoDao.isAccountBackedUp(seedId)
        }
    }

    override suspend fun getAllHdSeedOrderIndexes(): List<HdSeedOrderIndex> {
        return withContext(coroutineDispatcher) {
            customHdSeedInfoDao.getAll().map {
                HdSeedOrderIndex(it.seedId, it.orderIndex)
            }
        }
    }

    override suspend fun setOrderIndex(seedId: Int, orderIndex: Int) {
        withContext(coroutineDispatcher) {
            customHdSeedInfoDao.updateOrderIndex(seedId, orderIndex)
        }
    }

    override suspend fun clearAllInformation() {
        withContext(coroutineDispatcher) {
            customHdSeedInfoDao.clearAll()
        }
    }
}
