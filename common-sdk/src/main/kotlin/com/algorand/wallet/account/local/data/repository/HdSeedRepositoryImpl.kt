/*
 * Copyright 2022-2025 Pera Wallet, LDA
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

import com.algorand.wallet.account.local.data.database.dao.HdSeedDao
import com.algorand.wallet.account.local.data.mapper.entity.HdSeedEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.HdSeedMapper
import com.algorand.wallet.account.local.domain.model.HdSeed
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class HdSeedRepositoryImpl @Inject constructor(
    private val hdSeedDao: HdSeedDao,
    private val hdSeedEntityMapper: HdSeedEntityMapper,
    private val hdSeedMapper: HdSeedMapper,
    private val aesPlatformManager: AESPlatformManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HdSeedRepository {

    override fun getAllAsFlow(): Flow<List<HdSeed>> {
        return hdSeedDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> hdSeedMapper(entity) }
        }
    }

    override fun getSeedCountAsFlow(): Flow<Int> {
        return hdSeedDao.getTableSizeAsFlow()
    }

    override suspend fun getHdSeedCount(): Int {
        return hdSeedDao.getTableSize()
    }

    override suspend fun getMaxSeedId(): Int? {
        return hdSeedDao.getMaxSeedId()
    }

    override suspend fun hasAnySeed(): Boolean {
        return hdSeedDao.hasAnySeed()
    }

    override suspend fun getSeedIdIfExistingEntropy(entropy: ByteArray): Int? {
        val entities = hdSeedDao.getAll()

        for (entity in entities) {
            val decryptedEntropy = aesPlatformManager.decryptByteArray(entity.encryptedEntropy)
            if (entropy.contentEquals(decryptedEntropy)) {
                return entity.seedId
            }
        }

        return null
    }

    override suspend fun getAllHdSeeds(): List<HdSeed> {
        return withContext(coroutineDispatcher) {
            val entities = hdSeedDao.getAll()
            entities.map { hdSeedMapper(it) }
        }
    }

    override suspend fun getHdSeed(seedId: Int): HdSeed? {
        return withContext(coroutineDispatcher) {
            hdSeedDao.get(seedId)?.let { hdSeedMapper(it) }
        }
    }

    override suspend fun addHdSeed(seedId: Int, entropy: ByteArray, seed: ByteArray): Long {
        return withContext(coroutineDispatcher) {
            val hdKeyEntity = hdSeedEntityMapper(seedId, entropy, seed)
            val seedId = hdSeedDao.insert(hdKeyEntity)
            seedId
        }
    }

    override suspend fun deleteHdSeed(seedId: Int) {
        withContext(coroutineDispatcher) {
            hdSeedDao.delete(seedId)
        }
    }

    override suspend fun deleteAllHdSeeds() {
        withContext(coroutineDispatcher) {
            hdSeedDao.clearAll()
        }
    }

    override suspend fun getEntropy(seedId: Int): ByteArray? {
        return withContext(coroutineDispatcher) {
            val encryptedSK = hdSeedDao.get(seedId)?.encryptedEntropy
            encryptedSK?.let { aesPlatformManager.decryptByteArray(it) }
        }
    }

    override suspend fun getSeed(seedId: Int): ByteArray? {
        return withContext(coroutineDispatcher) {
            val encryptedSK = hdSeedDao.get(seedId)?.encryptedSeed
            encryptedSK?.let { aesPlatformManager.decryptByteArray(it) }
        }
    }
}
