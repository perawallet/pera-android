package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.VerificationTierResponse
import com.algorand.android.shared_db.assetdetail.model.VerificationTierEntity

internal interface VerificationTierEntityMapper {
    operator fun invoke(response: VerificationTierResponse?): VerificationTierEntity
}
