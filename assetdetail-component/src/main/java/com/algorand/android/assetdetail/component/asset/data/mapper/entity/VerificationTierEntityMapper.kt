package com.algorand.android.assetdetail.component.asset.data.mapper.entity

import com.algorand.android.assetdetail.component.asset.data.model.VerificationTierResponse
import com.algorand.android.shared_db.assetdetail.model.VerificationTierEntity

internal interface VerificationTierEntityMapper {
    operator fun invoke(response: VerificationTierResponse?): VerificationTierEntity
}
