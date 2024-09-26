package com.algorand.android.module.asset.detail.component.asset.data.mapper.model

import com.algorand.android.module.asset.detail.component.asset.data.model.VerificationTierResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import com.algorand.android.shared_db.assetdetail.model.VerificationTierEntity

interface VerificationTierMapper {
    operator fun invoke(response: VerificationTierResponse?): VerificationTier
    operator fun invoke(entity: VerificationTierEntity): VerificationTier
}
