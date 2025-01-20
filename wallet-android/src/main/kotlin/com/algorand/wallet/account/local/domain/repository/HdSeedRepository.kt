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

    suspend fun getAll(): List<HdSeed>

    suspend fun getHdSeed(seedId: Int): HdSeed?

    suspend fun addHdSeed(seed: HdSeed)

    suspend fun deleteHdSeed(address: String)

    suspend fun deleteAllHdSeeds()
}