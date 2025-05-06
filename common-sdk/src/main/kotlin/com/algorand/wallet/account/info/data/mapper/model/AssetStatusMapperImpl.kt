package com.algorand.wallet.account.info.data.mapper.model

import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity
import com.algorand.wallet.account.info.domain.model.AssetStatus
import javax.inject.Inject

internal class AssetStatusMapperImpl @Inject constructor() : AssetStatusMapper {

    override fun invoke(entity: AssetStatusEntity): AssetStatus {
        return when (entity) {
            AssetStatusEntity.OWNED_BY_ACCOUNT -> AssetStatus.OWNED_BY_ACCOUNT
            AssetStatusEntity.PENDING_FOR_REMOVAL -> AssetStatus.PENDING_FOR_REMOVAL
            AssetStatusEntity.PENDING_FOR_ADDITION -> AssetStatus.PENDING_FOR_ADDITION
        }
    }
}
