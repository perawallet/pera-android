package com.algorand.android.accountcore.ui.mapper

import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier.*
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
