package com.algorand.android.module.account.info.data.mapper.model

import com.algorand.android.module.account.info.data.model.AssetHoldingResponse
import com.algorand.android.module.account.info.domain.model.AssetHolding
import com.algorand.android.module.account.info.domain.model.AssetStatus
import com.algorand.android.shared_db.accountinformation.model.AssetHoldingEntity
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity.OWNED_BY_ACCOUNT
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity.PENDING_FOR_ADDITION
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity.PENDING_FOR_REMOVAL
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity.PENDING_FOR_SENDING
import java.math.BigInteger
import javax.inject.Inject

internal class AssetHoldingMapperImpl @Inject constructor() : AssetHoldingMapper {

    override fun invoke(response: AssetHoldingResponse): AssetHolding? {
        return AssetHolding(
            amount = response.amount ?: BigInteger.ZERO,
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
            PENDING_FOR_REMOVAL -> AssetStatus.PENDING_FOR_REMOVAL
            PENDING_FOR_ADDITION -> AssetStatus.PENDING_FOR_ADDITION
            PENDING_FOR_SENDING -> AssetStatus.PENDING_FOR_SENDING
            OWNED_BY_ACCOUNT -> AssetStatus.OWNED_BY_ACCOUNT
        }
    }
}
