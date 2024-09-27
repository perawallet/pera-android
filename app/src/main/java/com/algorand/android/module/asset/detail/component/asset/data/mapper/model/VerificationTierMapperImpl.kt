package com.algorand.android.module.asset.detail.component.asset.data.mapper.model

import com.algorand.android.module.asset.detail.component.asset.data.model.VerificationTierResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import com.algorand.android.module.shareddb.assetdetail.model.VerificationTierEntity
import javax.inject.Inject

internal class VerificationTierMapperImpl @Inject constructor() : VerificationTierMapper {

    override fun invoke(response: VerificationTierResponse?): VerificationTier {
        return when (response) {
            VerificationTierResponse.VERIFIED -> VerificationTier.VERIFIED
            VerificationTierResponse.UNVERIFIED -> VerificationTier.UNVERIFIED
            VerificationTierResponse.TRUSTED -> VerificationTier.TRUSTED
            VerificationTierResponse.SUSPICIOUS -> VerificationTier.SUSPICIOUS
            VerificationTierResponse.UNKNOWN, null -> VerificationTier.UNKNOWN
        }
    }

    override fun invoke(entity: VerificationTierEntity): VerificationTier {
        return when (entity) {
            VerificationTierEntity.VERIFIED -> VerificationTier.VERIFIED
            VerificationTierEntity.UNVERIFIED -> VerificationTier.UNVERIFIED
            VerificationTierEntity.TRUSTED -> VerificationTier.TRUSTED
            VerificationTierEntity.SUSPICIOUS -> VerificationTier.SUSPICIOUS
            VerificationTierEntity.UNKNOWN -> VerificationTier.UNKNOWN
        }
    }
}
