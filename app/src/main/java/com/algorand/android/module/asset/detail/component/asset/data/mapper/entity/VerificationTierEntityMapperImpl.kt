package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.VerificationTierResponse
import com.algorand.android.shared_db.assetdetail.model.VerificationTierEntity
import javax.inject.Inject

internal class VerificationTierEntityMapperImpl @Inject constructor() : VerificationTierEntityMapper {

    override fun invoke(response: VerificationTierResponse?): VerificationTierEntity {
        return when (response) {
            VerificationTierResponse.VERIFIED -> VerificationTierEntity.VERIFIED
            VerificationTierResponse.UNVERIFIED -> VerificationTierEntity.UNVERIFIED
            VerificationTierResponse.TRUSTED -> VerificationTierEntity.TRUSTED
            VerificationTierResponse.SUSPICIOUS -> VerificationTierEntity.SUSPICIOUS
            VerificationTierResponse.UNKNOWN, null -> VerificationTierEntity.UNKNOWN
        }
    }
}
