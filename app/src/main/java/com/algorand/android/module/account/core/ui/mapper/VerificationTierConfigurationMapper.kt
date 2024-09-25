package com.algorand.android.module.account.core.ui.mapper

import com.algorand.android.module.account.core.ui.model.VerificationTierConfiguration
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier

interface VerificationTierConfigurationMapper {
    operator fun invoke(verificationTier: VerificationTier?): VerificationTierConfiguration
}
