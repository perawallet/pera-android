package com.algorand.android.module.asset.detail.component.data.mapper.model

import com.algorand.android.module.asset.detail.component.data.mapper.model.collectible.CollectibleMapper
import com.algorand.android.module.asset.detail.component.data.model.*
import com.algorand.android.module.asset.detail.component.data.model.collectible.CollectibleResponse
import com.algorand.android.module.asset.detail.component.domain.model.*
import com.algorand.android.module.shareddb.assetdetail.model.*
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*
import java.math.*

internal class AssetAssetAssetInfoMapperImplTest {

    private val assetCreatorMapper: AssetCreatorMapper = mock {
        on { invoke(ASSET_CREATOR_ID, ASSET_CREATOR_ADDRESS, IS_VERIFIED_ASSET_CREATOR) } doReturn ASSET_CREATOR
        on { invoke(ASSET_CREATOR_RESPONSE) } doReturn ASSET_CREATOR
    }
    private val verificationTierMapper: VerificationTierMapper = mock {
        on { invoke(VERIFICATION_TIER_ENTITY) } doReturn VERIFICATION_TIER
        on { invoke(VERIFICATION_TIER_RESPONSE) } doReturn VERIFICATION_TIER
    }
    private val collectibleMapper: CollectibleMapper = mock {
        on { invoke(COLLECTIBLE_ENTITY, COLLECTIBLE_MEDIA_ENTITIES, COLLECTIBLE_TRAIT_ENTITIES) } doReturn COLLECTIBLE
        on { invoke(COLLECTIBLE_RESPONSE) } doReturn COLLECTIBLE
    }

    private val sut = AssetDetailMapperImpl(
        assetCreatorMapper,
        verificationTierMapper,
        collectibleMapper
    )

    @Test
    fun `EXPECT asset detail WHEN response fields are valid`() {
        val result = sut(ASSET_DETAIL_RESPONSE)

        assertEquals(ASSET_DETAIL, result)
    }

    @Test
    fun `EXPECT null WHEN asset id is null`() {
        val assetDetailResponse = ASSET_DETAIL_RESPONSE.copy(assetId = null)

        val result = sut(assetDetailResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT default values WHEN response fields are null`() {
        val response = NULL_ASSET_DETAIL_RESPONSE.copy(assetId = 123L)

        val result = sut(response)

        assertEquals(NULL_ASSET_DETAIL_WITH_DEFAULT_VALUES, result)
    }

    @Test
    fun `EXPECT entity to be mapped to asset detail WHEN all fields are valid`() {
        val result = sut(
            ASSET_DETAIL_ENTITY,
            COLLECTIBLE_ENTITY,
            COLLECTIBLE_MEDIA_ENTITIES,
            COLLECTIBLE_TRAIT_ENTITIES
        )

        assertEquals(ASSET_DETAIL, result)
    }

    @Test
    fun `EXPECT entity collectible to be null WHEN collectible entity is null`() {
        val result = sut(
            ASSET_DETAIL_ENTITY,
            null,
            COLLECTIBLE_MEDIA_ENTITIES,
            COLLECTIBLE_TRAIT_ENTITIES
        )

        assertEquals(ASSET_DETAIL.copy(collectible = null), result)
    }

    companion object {
        private val ASSET_CREATOR_RESPONSE = fixtureOf<AssetCreatorResponse>()
        private val ASSET_CREATOR = fixtureOf<AssetCreator>()
        private const val ASSET_CREATOR_ID = 123L
        private const val ASSET_CREATOR_ADDRESS = "assetCreatorAddress"
        private const val IS_VERIFIED_ASSET_CREATOR = true
        private val COLLECTIBLE_RESPONSE = fixtureOf<CollectibleResponse>()
        private val COLLECTIBLE = fixtureOf<Collectible>()
        private val VERIFICATION_TIER_RESPONSE = fixtureOf<VerificationTierResponse>()
        private val VERIFICATION_TIER = fixtureOf<VerificationTier>()
        private val VERIFICATION_TIER_ENTITY = fixtureOf<VerificationTierEntity>()

        private val ASSET_DETAIL_RESPONSE = AssetDetailResponse(
            assetId = 123L,
            fullName = "assetName",
            shortName = "shortName",
            logoUri = "logoUri",
            fractionDecimals = 2,
            usdValue = BigDecimal.TEN,
            assetCreator = ASSET_CREATOR_RESPONSE,
            collectible = COLLECTIBLE_RESPONSE,
            maxSupply = BigInteger.TEN,
            explorerUrl = "explorerUrl",
            verificationTier = VERIFICATION_TIER_RESPONSE,
            projectUrl = "projectUrl",
            projectName = "projectName",
            logoSvgUri = "logoSvgUri",
            discordUrl = "discordUrl",
            telegramUrl = "telegramUrl",
            twitterUsername = "twitterUsername",
            description = "description",
            url = "url",
            totalSupply = BigDecimal.TEN,
            last24HoursAlgoPriceChangePercentage = BigDecimal.TEN,
            isAvailableOnDiscoverMobile = true
        )

        private val ASSET_DETAIL_ENTITY = AssetDetailEntity(
            assetId = 123L,
            name = "assetName",
            unitName = "shortName",
            decimals = 2,
            usdValue = BigDecimal.TEN,
            assetCreatorId = ASSET_CREATOR_ID,
            assetCreatorAddress = ASSET_CREATOR_ADDRESS,
            isVerifiedAssetCreator = IS_VERIFIED_ASSET_CREATOR,
            verificationTier = VERIFICATION_TIER_ENTITY,
            logoUrl = "logoUri",
            logoSvgUrl = "logoSvgUri",
            explorerUrl = "explorerUrl",
            projectUrl = "projectUrl",
            projectName = "projectName",
            discordUrl = "discordUrl",
            telegramUrl = "telegramUrl",
            twitterUsername = "twitterUsername",
            description = "description",
            totalSupply = BigDecimal.TEN,
            maxSupply = BigInteger.TEN,
            url = "url",
            availableOnDiscoverMobile = true,
            last24HoursAlgoPriceChangePercentage = BigDecimal.TEN
        )

        private val COLLECTIBLE_ENTITY = fixtureOf<CollectibleEntity>()
        private val COLLECTIBLE_MEDIA_ENTITIES = fixtureOf<List<CollectibleMediaEntity>>()
        private val COLLECTIBLE_TRAIT_ENTITIES = fixtureOf<List<CollectibleTraitEntity>>()

        private val ASSET_DETAIL = AssetDetail(
            assetId = 123L,
            fullName = "assetName",
            shortName = "shortName",
            logoUri = "logoUri",
            fractionDecimals = 2,
            usdValue = BigDecimal.TEN,
            assetCreator = ASSET_CREATOR,
            collectible = COLLECTIBLE,
            maxSupply = BigInteger.TEN,
            explorerUrl = "explorerUrl",
            verificationTier = VERIFICATION_TIER,
            projectUrl = "projectUrl",
            projectName = "projectName",
            logoSvgUri = "logoSvgUri",
            discordUrl = "discordUrl",
            telegramUrl = "telegramUrl",
            twitterUsername = "twitterUsername",
            assetDescription = "description",
            url = "url",
            totalSupply = BigDecimal.TEN,
            last24HoursAlgoPriceChangePercentage = BigDecimal.TEN,
            isAvailableOnDiscoverMobile = true
        )

        private val NULL_ASSET_DETAIL_RESPONSE = AssetDetailResponse(
            assetId = null,
            fullName = null,
            shortName = null,
            logoUri = null,
            fractionDecimals = null,
            usdValue = null,
            assetCreator = null,
            collectible = null,
            maxSupply = null,
            explorerUrl = null,
            verificationTier = null,
            projectUrl = null,
            projectName = null,
            logoSvgUri = null,
            discordUrl = null,
            telegramUrl = null,
            twitterUsername = null,
            description = null,
            url = null,
            totalSupply = null,
            last24HoursAlgoPriceChangePercentage = null,
            isAvailableOnDiscoverMobile = null
        )

        private val NULL_ASSET_DETAIL_WITH_DEFAULT_VALUES =
            AssetDetail(
                assetId = 123L,
                fullName = null,
                shortName = null,
                logoUri = null,
                fractionDecimals = 0,
                usdValue = null,
                assetCreator = null,
                collectible = null,
                maxSupply = null,
                explorerUrl = null,
                verificationTier = VerificationTier.UNKNOWN,
                projectUrl = null,
                projectName = null,
                logoSvgUri = null,
                discordUrl = null,
                telegramUrl = null,
                twitterUsername = null,
                assetDescription = null,
                url = null,
                totalSupply = null,
                last24HoursAlgoPriceChangePercentage = null,
                isAvailableOnDiscoverMobile = null
            )
    }
}
