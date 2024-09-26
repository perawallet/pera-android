package com.algorand.android.module.account.info.data.mapper.entity

import com.algorand.android.module.account.info.data.model.AssetHoldingResponse
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.accountinformation.model.AssetHoldingEntity
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity

internal class AssetHoldingEntityMapperImpl(
    private val encryptionManager: EncryptionManager
) : AssetHoldingEntityMapper {

    override fun invoke(address: String, response: AssetHoldingResponse): AssetHoldingEntity? {
        return AssetHoldingEntity(
            encryptedAddress = encryptionManager.encrypt(address),
            assetId = response.assetId ?: return null,
            amount = response.amount ?: return null,
            isDeleted = response.isDeleted ?: false,
            isFrozen = response.isFrozen ?: false,
            optedInAtRound = response.optedInAtRound,
            optedOutAtRound = response.optedOutAtRound,
            assetStatusEntity = AssetStatusEntity.OWNED_BY_ACCOUNT
        )
    }

    override fun invoke(responses: List<Pair<String, AssetHoldingResponse>>): List<AssetHoldingEntity> {
        return responses.mapNotNull { (address, assetHoldingResponse) ->
            invoke(address, assetHoldingResponse)
        }
    }
}
