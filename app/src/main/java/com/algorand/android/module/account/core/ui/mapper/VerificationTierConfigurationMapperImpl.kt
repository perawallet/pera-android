package com.algorand.android.module.account.core.ui.mapper

import com.algorand.android.module.account.core.ui.model.VerificationTierConfiguration
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier.*
import javax.inject.Inject

internal class VerificationTierConfigurationMapperImpl @Inject constructor() : VerificationTierConfigurationMapper {

    override fun invoke(verificationTier: VerificationTier?): VerificationTierConfiguration {
        return when (verificationTier) {
            VERIFIED -> VerificationTierConfiguration.VERIFIED
            TRUSTED -> VerificationTierConfiguration.TRUSTED
            SUSPICIOUS -> VerificationTierConfiguration.SUSPICIOUS
            UNVERIFIED, UNKNOWN, null -> VerificationTierConfiguration.UNVERIFIED
        }
    }
}
