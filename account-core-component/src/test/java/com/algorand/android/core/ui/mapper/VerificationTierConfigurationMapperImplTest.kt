package com.algorand.android.core.ui.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration.*
import org.junit.*

internal class VerificationTierConfigurationMapperImplTest {

    private val sut = com.algorand.android.accountcore.ui.mapper.VerificationTierConfigurationMapperImpl()

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
