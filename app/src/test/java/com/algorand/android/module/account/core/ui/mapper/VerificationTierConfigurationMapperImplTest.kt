package com.algorand.android.module.account.core.ui.mapper

import com.algorand.android.module.account.core.ui.model.VerificationTierConfiguration.SUSPICIOUS
import com.algorand.android.module.account.core.ui.model.VerificationTierConfiguration.TRUSTED
import com.algorand.android.module.account.core.ui.model.VerificationTierConfiguration.UNVERIFIED
import com.algorand.android.module.account.core.ui.model.VerificationTierConfiguration.VERIFIED
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import org.junit.Assert
import org.junit.Test

internal class VerificationTierConfigurationMapperImplTest {

    private val sut = VerificationTierConfigurationMapperImpl()

    @Test
    fun `EXPECT verification tier to be mapped`() {
        Assert.assertEquals(VERIFIED, sut(VerificationTier.VERIFIED))
        Assert.assertEquals(TRUSTED, sut(VerificationTier.TRUSTED))
        Assert.assertEquals(SUSPICIOUS, sut(VerificationTier.SUSPICIOUS))
        Assert.assertEquals(UNVERIFIED, sut(VerificationTier.UNVERIFIED))
        Assert.assertEquals(UNVERIFIED, sut(VerificationTier.UNKNOWN))
        Assert.assertEquals(UNVERIFIED, sut(null))
    }
}
