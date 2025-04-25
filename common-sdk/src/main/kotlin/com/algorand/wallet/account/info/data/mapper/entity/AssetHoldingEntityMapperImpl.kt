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

package com.algorand.wallet.account.info.data.mapper.entity

import com.algorand.wallet.account.info.data.database.model.AssetHoldingEntity
import com.algorand.wallet.account.info.data.model.AssetHoldingResponse
import com.algorand.wallet.account.info.domain.model.AssetStatus
import java.math.BigInteger
import javax.inject.Inject

internal class AssetHoldingEntityMapperImpl @Inject constructor(
    private val assetStatusEntityMapper: AssetStatusEntityMapper
) : AssetHoldingEntityMapper {

    override fun invoke(address: String, response: AssetHoldingResponse, status: AssetStatus): AssetHoldingEntity? {
        return AssetHoldingEntity(
            algoAddress = address,
            assetId = response.assetId ?: return null,
            amount = response.amount?.toBigIntegerOrNull() ?: return null,
            isDeleted = response.isDeleted ?: false,
            isFrozen = response.isFrozen ?: false,
            optedInAtRound = response.optedInAtRound,
            optedOutAtRound = response.optedOutAtRound,
            assetStatusEntity = assetStatusEntityMapper(status)
        )
    }

    override fun invoke(address: String, assetId: Long, status: AssetStatus): AssetHoldingEntity {
        return AssetHoldingEntity(
            algoAddress = address,
            assetId = assetId,
            amount = BigInteger.ZERO,
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = null,
            optedOutAtRound = null,
            assetStatusEntity = assetStatusEntityMapper(status)
        )
    }
}
