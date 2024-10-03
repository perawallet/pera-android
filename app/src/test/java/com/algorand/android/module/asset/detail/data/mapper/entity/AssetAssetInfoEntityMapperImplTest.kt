package com.algorand.android.module.asset.detail.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.AssetDetailEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.VerificationTierEntityMapper
import com.algorand.android.module.asset.detail.component.asset.data.model.AssetCreatorResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.shareddb.assetdetail.model.AssetDetailEntity
import com.algorand.android.module.shareddb.assetdetail.model.VerificationTierEntity
import com.algorand.android.testutil.fixtureOf
import java.math.BigDecimal
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class AssetAssetInfoEntityMapperImplTest {

    private val verificationTierEntityMapper: VerificationTierEntityMapper = mock()

    private val sut = AssetDetailEntityMapperImpl(verificationTierEntityMapper)

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        whenever(verificationTierEntityMapper(null)).thenReturn(VerificationTierEntity.UNKNOWN)
        val result = sut(ASSET_DETAIL_RESPONSE)

        assertEquals(ASSET_DETAIL_ENTITY, result)
    }

    @Test
    fun `EXPECT null WHEN assetId is null`() {
        val assetDetailResponse = fixtureOf<AssetResponse>().copy(assetId = null)

        val result = sut(assetDetailResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT default values when optional fields are null`() {
        whenever(verificationTierEntityMapper(null)).thenReturn(VerificationTierEntity.UNKNOWN)
        val assetDetailResponse = fixtureOf<AssetResponse>().copy(
            assetId = 1L,
            isAvailableOnDiscoverMobile = null,
            maxSupply = null,
            totalSupply = null,
            fractionDecimals = null,
            verificationTier = null
        )

        val result = sut(assetDetailResponse)

        assertEquals(0, result?.decimals)
        assertEquals(BigInteger.ZERO, result?.maxSupply)
        assertEquals(BigDecimal.ZERO, result?.totalSupply)
        assertFalse(result?.availableOnDiscoverMobile!!)
    }

    companion object {
        private val ASSET_DETAIL_RESPONSE = AssetResponse(
            assetId = 1L,
            fullName = "fullName",
            shortName = "shortName",
            fractionDecimals = 2,
            usdValue = BigDecimal.TEN,
            maxSupply = BigInteger.TEN,
            explorerUrl = "explorerUrl",
            projectUrl = "projectUrl",
            projectName = "projectName",
            logoSvgUri = "logoSvgUri",
            logoUri = "logoUri",
            description = "description",
            totalSupply = BigDecimal.TEN,
            url = "url",
            telegramUrl = "telegramUrl",
            twitterUsername = "twitterUsername",
            discordUrl = "discordUrl",
            isAvailableOnDiscoverMobile = true,
            last24HoursAlgoPriceChangePercentage = BigDecimal.TEN,
            verificationTier = null,
            assetCreator = AssetCreatorResponse(
                publicKey = "publicKey",
                id = 1L,
                isVerifiedAssetCreator = true
            ),
            collectible = null
        )

        private val ASSET_DETAIL_ENTITY = AssetDetailEntity(
            assetId = 1L,
            name = "fullName",
            unitName = "shortName",
            decimals = 2,
            usdValue = BigDecimal.TEN,
            maxSupply = BigInteger.TEN,
            explorerUrl = "explorerUrl",
            projectUrl = "projectUrl",
            projectName = "projectName",
            logoSvgUrl = "logoSvgUri",
            logoUrl = "logoUri",
            description = "description",
            totalSupply = BigDecimal.TEN,
            url = "url",
            telegramUrl = "telegramUrl",
            twitterUsername = "twitterUsername",
            discordUrl = "discordUrl",
            availableOnDiscoverMobile = true,
            last24HoursAlgoPriceChangePercentage = BigDecimal.TEN,
            verificationTier = VerificationTierEntity.UNKNOWN,
            assetCreatorAddress = "publicKey",
            assetCreatorId = 1L,
            isVerifiedAssetCreator = true
        )
    }
}
