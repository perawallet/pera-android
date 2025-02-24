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

import com.algorand.wallet.account.local.data.database.dao.HdSeedDao
import com.algorand.wallet.account.local.data.mapper.entity.HdSeedEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.HdSeedMapper
import com.algorand.wallet.account.local.domain.model.HdSeed
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import com.algorand.wallet.encryption.SecretKeyEncryptionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class HdSeedRepositoryImpl @Inject constructor(
    private val hdSeedDao: HdSeedDao,
    private val hdSeedEntityMapper: HdSeedEntityMapper,
    private val hdSeedMapper: HdSeedMapper,
    private val secretKeyEncryptionManager: SecretKeyEncryptionManager,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HdSeedRepository {

    override fun getAllAsFlow(): Flow<List<HdSeed>> {
        return hdSeedDao.getAllAsFlow().map { entityList ->
            entityList.map { entity -> hdSeedMapper(entity) }
        }
    }

    override fun getHdSeedCountAsFlow(): Flow<Int> {
        return hdSeedDao.getTableSizeAsFlow()
    }

    override suspend fun getAllHdSeeds(): List<HdSeed> {
        return withContext(coroutineDispatcher) {
            val hdSeedEntities = hdSeedDao.getAll()
            hdSeedEntities.map { hdSeedMapper(it) }
        }
    }

    override suspend fun getHdSeed(seedId: Int): HdSeed? {
        return withContext(coroutineDispatcher) {
            hdSeedDao.get(seedId)?.let { hdSeedMapper(it) }
        }
    }

    override suspend fun getHdSeed(encryptedEntropy: ByteArray): HdSeed? {
        return withContext(coroutineDispatcher) {
            hdSeedDao.get(encryptedEntropy)?.let { hdSeedMapper(it) }
        }
    }

    override suspend fun getEncryptedEntropy(seedId: Int): ByteArray? {
        return withContext(coroutineDispatcher) {
            hdSeedDao.getEncryptedEntropy(seedId)
        }
    }

    override suspend fun getAllHdSeed(entropyCustomName: String): List<HdSeed> {
        return withContext(coroutineDispatcher) {
            val hdSeedEntities = hdSeedDao.getAll(entropyCustomName)
            hdSeedEntities.map { hdSeedMapper(it) }
        }
    }

    override fun addHdSeedAsFlow(hdSeed: HdSeed, encryptedEntropy: ByteArray, encryptedSeed: ByteArray): Flow<Unit> = flow {
        val hdKeyEntity = hdSeedEntityMapper(hdSeed, encryptedEntropy, encryptedSeed)
        val rowsAffected = hdSeedDao.insert(hdKeyEntity)
        emit(rowsAffected)
    }.flowOn(Dispatchers.IO)

    override fun updateHdSeedCustomNameAsFlow(hdSeed: HdSeed): Flow<Unit> = flow {
        val rowsAffected = hdSeedDao.update(hdSeed.seedId, hdSeed.seedCustomName)
        emit(rowsAffected)
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteHdSeed(seedId: Int) {
        withContext(coroutineDispatcher) {
            hdSeedDao.delete(seedId)
        }
    }

    override suspend fun deleteHdSeed(encrypted_entropy: ByteArray) {
        return withContext(coroutineDispatcher) {
            hdSeedDao.delete(encrypted_entropy)
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
            encryptedSK?.let { secretKeyEncryptionManager.decryptByteArray(it) }
        }
    }
}
