package com.algorand.android.module.asset.detail.component.data.mapper.entity

import com.algorand.android.module.asset.detail.component.data.model.*
import com.algorand.android.shared_db.assetdetail.model.*
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*
import java.math.*

internal class AssetAssetInfoEntityMapperImplTest {

    private val verificationTierEntityMapper: VerificationTierEntityMapper = mock()

    private val sut = com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.AssetDetailEntityMapperImpl(
        verificationTierEntityMapper
    )

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        whenever(verificationTierEntityMapper(null)).thenReturn(VerificationTierEntity.UNKNOWN)
        val result = sut(ASSET_DETAIL_RESPONSE)

        assertEquals(ASSET_DETAIL_ENTITY, result)
    }

    @Test
    fun `EXPECT null WHEN assetId is null`() {
        val assetDetailResponse = fixtureOf<AssetDetailResponse>().copy(assetId = null)

        val result = sut(assetDetailResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT default values when optional fields are null`() {
        whenever(verificationTierEntityMapper(null)).thenReturn(VerificationTierEntity.UNKNOWN)
        val assetDetailResponse = fixtureOf<AssetDetailResponse>().copy(
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
        private val ASSET_DETAIL_RESPONSE = AssetDetailResponse(
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
