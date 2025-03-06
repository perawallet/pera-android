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

package com.algorand.wallet.account.local.domain.repository

import com.algorand.wallet.account.local.domain.model.HdSeed
import kotlinx.coroutines.flow.Flow

internal interface HdSeedRepository {

    fun getAllAsFlow(): Flow<List<HdSeed>>

    fun getHdSeedCountAsFlow(): Flow<Int>

    suspend fun getMaxSeedId(): Int?

    suspend fun getAllHdSeeds(): List<HdSeed>

    suspend fun getHdSeed(seedId: Int): HdSeed?

    suspend fun getEncryptedEntropy(seedId: Int): ByteArray?

    suspend fun addHdSeed(seedId: Int, entropy: ByteArray, seed: ByteArray): Long

    suspend fun deleteHdSeed(seedId: Int)

    suspend fun deleteAllHdSeeds()

    suspend fun getEntropy(seedId: Int): ByteArray?

    suspend fun getSeed(seedId: Int): ByteArray?
}
