package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.asset.detail.component.AssetConstants
import com.algorand.android.module.asset.detail.component.asset.domain.model.*
import com.algorand.android.module.asset.detail.component.asset.domain.model.Collection
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleMixedData
import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

internal class OwnedCollectibleMixedDataMapperImplTest {

    private val sut = OwnedCollectibleMixedDataMapperImpl()

    @Test
    fun `EXPECT owned collectible mixed data to be mapped`() {
        val result = sut(
            COLLECTIBLE_DETAIL,
            AMOUNT,
            FORMATTED_AMOUNT,
            FORMATTED_COMPACT_AMOUNT,
            PARITY_VALUE_IN_SELECTED_CURRENCY,
            PARITY_VALUE_IN_SECONDARY_CURRENCY,
            OPTED_IN_AT_ROUND
        )

        assertEquals(OWNED_COLLECTIBLE_MIXED_DATA, result)
    }

    @Test
    fun `EXPECT mixed asset detail data to be mapped`() {
        val result = sut(
            ASSET_DETAIL,
            AMOUNT,
            FORMATTED_AMOUNT,
            FORMATTED_COMPACT_AMOUNT,
            PARITY_VALUE_IN_SELECTED_CURRENCY,
            PARITY_VALUE_IN_SECONDARY_CURRENCY,
            OPTED_IN_AT_ROUND
        )

        assertEquals(OWNED_COLLECTIBLE_MIXED_DATA, result)
    }

    @Test
    fun `EXPECT asset decimal to be 0 WHEN collectible detail has no fraction decimals`() {
        val result = sut(
            COLLECTIBLE_DETAIL.copy(fractionDecimals = null),
            AMOUNT,
            FORMATTED_AMOUNT,
            FORMATTED_COMPACT_AMOUNT,
            PARITY_VALUE_IN_SELECTED_CURRENCY,
            PARITY_VALUE_IN_SECONDARY_CURRENCY,
            OPTED_IN_AT_ROUND
        )

        val expectedOwnedCollectibleMixedData = OWNED_COLLECTIBLE_MIXED_DATA.copy(decimals = 0)
        assertEquals(expectedOwnedCollectibleMixedData, result)
    }

    private companion object {
        const val FRACTION_DECIMALS = 6
        val COLLECTIBLE_DETAIL = fixtureOf<BaseCollectibleDetail.MixedCollectibleDetail>().copy(
            fractionDecimals = FRACTION_DECIMALS,
        )
        val COLLECTIBLE = fixtureOf<Collectible>().copy(
            collection = fixtureOf<Collection>().copy(
                collectionName = COLLECTIBLE_DETAIL.collectionName
            ),
            title = COLLECTIBLE_DETAIL.title,
            primaryImageUrl = COLLECTIBLE_DETAIL.prismUrl
        )
        val AMOUNT = fixtureOf<BigInteger>()
        val FORMATTED_AMOUNT = fixtureOf<String>()
        val FORMATTED_COMPACT_AMOUNT = fixtureOf<String>()
        val PARITY_VALUE_IN_SELECTED_CURRENCY = fixtureOf<ParityValue>()
        val PARITY_VALUE_IN_SECONDARY_CURRENCY = fixtureOf<ParityValue>()
        val OPTED_IN_AT_ROUND = fixtureOf<Long>()

        val OWNED_COLLECTIBLE_MIXED_DATA = OwnedCollectibleMixedData(
            id = COLLECTIBLE_DETAIL.assetId,
            name = COLLECTIBLE_DETAIL.fullName,
            shortName = COLLECTIBLE_DETAIL.shortName,
            amount = AMOUNT,
            formattedAmount = FORMATTED_AMOUNT,
            formattedCompactAmount = FORMATTED_COMPACT_AMOUNT,
            parityValueInSelectedCurrency = PARITY_VALUE_IN_SELECTED_CURRENCY,
            parityValueInSecondaryCurrency = PARITY_VALUE_IN_SECONDARY_CURRENCY,
            isAlgo = false,
            decimals = COLLECTIBLE_DETAIL.fractionDecimals ?: AssetConstants.DEFAULT_ASSET_DECIMAL,
            creatorPublicKey = COLLECTIBLE_DETAIL.assetCreator?.publicKey,
            usdValue = COLLECTIBLE_DETAIL.usdValue,
            isAmountInSelectedCurrencyVisible = COLLECTIBLE_DETAIL.hasUsdValue(),
            prismUrl = COLLECTIBLE_DETAIL.prismUrl,
            collectibleName = COLLECTIBLE_DETAIL.title,
            collectionName = COLLECTIBLE_DETAIL.collectionName,
            optedInAtRound = OPTED_IN_AT_ROUND
        )

        val ASSET_DETAIL = AssetDetail(
            assetId = COLLECTIBLE_DETAIL.assetId,
            fullName = COLLECTIBLE_DETAIL.fullName,
            shortName = COLLECTIBLE_DETAIL.shortName,
            usdValue = COLLECTIBLE_DETAIL.usdValue,
            fractionDecimals = FRACTION_DECIMALS,
            assetCreator = COLLECTIBLE_DETAIL.assetCreator,
            verificationTier = COLLECTIBLE_DETAIL.verificationTier,
            logoUri = COLLECTIBLE_DETAIL.logoUri,
            logoSvgUri = COLLECTIBLE_DETAIL.logoSvgUri,
            explorerUrl = COLLECTIBLE_DETAIL.explorerUrl,
            projectUrl = COLLECTIBLE_DETAIL.projectUrl,
            projectName = COLLECTIBLE_DETAIL.projectName,
            discordUrl = COLLECTIBLE_DETAIL.discordUrl,
            telegramUrl = COLLECTIBLE_DETAIL.telegramUrl,
            twitterUsername = COLLECTIBLE_DETAIL.twitterUsername,
            assetDescription = COLLECTIBLE_DETAIL.assetDescription,
            totalSupply = COLLECTIBLE_DETAIL.totalSupply,
            maxSupply = COLLECTIBLE_DETAIL.maxSupply,
            url = COLLECTIBLE_DETAIL.url,
            last24HoursAlgoPriceChangePercentage = COLLECTIBLE_DETAIL.last24HoursAlgoPriceChangePercentage,
            isAvailableOnDiscoverMobile = COLLECTIBLE_DETAIL.isAvailableOnDiscoverMobile,
            collectible = COLLECTIBLE
        )
    }
}
