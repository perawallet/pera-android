package com.algorand.android.assetdetail.component.asset.data.mapper.model

import com.algorand.android.assetdetail.component.asset.data.model.VerificationTierResponse
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.shared_db.assetdetail.model.VerificationTierEntity

interface VerificationTierMapper {
    operator fun invoke(response: VerificationTierResponse?): VerificationTier
    operator fun invoke(entity: VerificationTierEntity): VerificationTier
}
