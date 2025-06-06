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

import com.algorand.wallet.account.local.data.database.dao.Algo25NoAuthDao
import com.algorand.wallet.account.local.data.database.model.Algo25Entity
import com.algorand.wallet.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.wallet.account.local.domain.repository.Algo25NoAuthRepository
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import javax.inject.Inject

internal class DefaultAlgo25NoAuthRepository @Inject constructor(
    private val algo25NoAuthDao: Algo25NoAuthDao,
    private val noAuthEntityMapper: NoAuthEntityMapper,
    private val aesPlatformManager: AESPlatformManager,
) : Algo25NoAuthRepository {

    override suspend fun updateInvalidAlgo25AccountsToNoAuth() {
        val algo25Entities = algo25NoAuthDao.getAllAlgo25Entities()
        val invalidAlgo25Addresses = algo25Entities.mapNotNull { entity ->
            entity.algoAddress.takeIf { entity.isSecretKeyInvalid() }
        }
        val entities = invalidAlgo25Addresses.map { noAuthEntityMapper(it) }
        algo25NoAuthDao.updateAlgo25AccountsToNoAuthAccounts(entities)
    }

    private fun Algo25Entity.isSecretKeyInvalid(): Boolean {
        if (encryptedSecretKey.isEmpty() || encryptedSecretKey.contentEquals(byteArrayOf(0))) return true
        val decryptedSecretKey = aesPlatformManager.decryptByteArray(encryptedSecretKey)
        return decryptedSecretKey.isEmpty() || decryptedSecretKey.contentEquals(byteArrayOf(0))
    }
}
