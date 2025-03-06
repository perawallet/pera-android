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

package com.algorand.wallet.account.custom.domain.repository

import com.algorand.wallet.account.custom.domain.model.CustomHdSeedInfo
import com.algorand.wallet.account.custom.domain.model.HdSeedOrderIndex

internal interface CustomHdSeedInfoRepository {

    suspend fun getCustomInfo(seedId: Int): CustomHdSeedInfo?

    suspend fun getCustomInfoOrNull(seedId: Int): CustomHdSeedInfo?

    suspend fun setCustomInfo(info: CustomHdSeedInfo)

    suspend fun setCustomName(seedId: Int, name: String)

    suspend fun getCustomName(seedId: Int): String?

    suspend fun setOrderIndex(seedId: Int, orderIndex: Int)

    suspend fun deleteCustomInfo(seedId: Int)

    suspend fun getNotBackedUpHdSeeds(): Set<Int>

    suspend fun getBackedUpHdSeeds(): Set<Int>

    suspend fun isHdSeedBackedUp(seedId: Int): Boolean

    suspend fun getAllHdSeedOrderIndexes(): List<HdSeedOrderIndex>
}
