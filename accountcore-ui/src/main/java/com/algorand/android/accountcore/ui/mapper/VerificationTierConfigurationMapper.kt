package com.algorand.android.accountcore.ui.mapper

import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier

interface VerificationTierConfigurationMapper {
    operator fun invoke(verificationTier: VerificationTier?): VerificationTierConfiguration
}
