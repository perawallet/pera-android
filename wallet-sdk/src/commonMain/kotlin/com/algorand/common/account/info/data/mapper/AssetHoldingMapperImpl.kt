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

package com.algorand.common.account.info.data.mapper

import com.algorand.common.account.info.data.database.model.AssetHoldingEntity
import com.algorand.common.account.info.data.database.model.AssetStatusEntity
import com.algorand.common.account.info.data.model.AssetHoldingResponse
import com.algorand.common.account.info.domain.model.AssetHolding
import com.algorand.common.account.info.domain.model.AssetStatus

internal class AssetHoldingMapperImpl : AssetHoldingMapper {

    override fun invoke(response: AssetHoldingResponse): AssetHolding? {
        return AssetHolding(
            amount = response.amount ?: "0",
            assetId = response.assetId ?: return null,
            isFrozen = response.isFrozen ?: false,
            isDeleted = response.isDeleted ?: false,
            optedInAtRound = response.optedInAtRound,
            optedOutAtRound = response.optedOutAtRound,
            status = AssetStatus.OWNED_BY_ACCOUNT
        )
    }

    override fun invoke(entity: AssetHoldingEntity): AssetHolding {
        return AssetHolding(
            amount = entity.amount,
            assetId = entity.assetId,
            isDeleted = entity.isDeleted,
            isFrozen = entity.isFrozen,
            optedInAtRound = entity.optedInAtRound,
            optedOutAtRound = entity.optedOutAtRound,
            status = mapToAssetStatus(entity.assetStatusEntity)
        )
    }

    override fun invoke(entities: List<AssetHoldingEntity>): List<AssetHolding> {
        return entities.map { invoke(it) }
    }

    private fun mapToAssetStatus(entity: AssetStatusEntity): AssetStatus {
        return when (entity) {
            AssetStatusEntity.PENDING_FOR_REMOVAL -> AssetStatus.PENDING_FOR_REMOVAL
            AssetStatusEntity.PENDING_FOR_ADDITION -> AssetStatus.PENDING_FOR_ADDITION
            AssetStatusEntity.PENDING_FOR_SENDING -> AssetStatus.PENDING_FOR_SENDING
            AssetStatusEntity.OWNED_BY_ACCOUNT -> AssetStatus.OWNED_BY_ACCOUNT
        }
    }
}
