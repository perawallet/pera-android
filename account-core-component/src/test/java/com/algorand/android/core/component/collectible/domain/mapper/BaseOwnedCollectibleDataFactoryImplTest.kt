package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.account.info.domain.model.AssetHolding
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.*
import com.algorand.android.formatting.FormatAmountByCollectibleFractionalDigit
import com.algorand.android.parity.domain.model.ParityValue
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import java.math.*

internal class BaseOwnedCollectibleDataFactoryImplTest {

    private val ownedCollectibleImageDataMapper: OwnedCollectibleImageDataMapper = mock()
    private val ownedCollectibleVideoDataMapper: OwnedCollectibleVideoDataMapper = mock()
    private val ownedCollectibleMixedDataMapper: OwnedCollectibleMixedDataMapper = mock()
    private val ownedCollectibleAudioDataMapper: OwnedCollectibleAudioDataMapper = mock()
    private val ownedCollectibleNotSupportedDataMapper: OwnedCollectibleNotSupportedDataMapper = mock()
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue = mock {
        on { invoke(USD_VALUE, FRACTION_DECIMALS, AMOUNT) } doReturn PARITY_VALUE_IN_SELECTED_CURRENCY
    }
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue = mock {
        on { invoke(USD_VALUE, FRACTION_DECIMALS, AMOUNT) } doReturn PARITY_VALUE_IN_SECONDARY_CURRENCY
    }
    private val formatAmountByCollectibleFractionalDigit: com.algorand.android.formatting.FormatAmountByCollectibleFractionalDigit =
        mock {
            on { invoke(AMOUNT, FRACTION_DECIMALS) } doReturn FORMATTED_AMOUNT
            on { invoke(AMOUNT, FRACTION_DECIMALS, isDecimalFixed = true) } doReturn FORMATTED_COMPACT_AMOUNT
        }

    private val sut = BaseOwnedCollectibleDataFactoryImpl(
        ownedCollectibleImageDataMapper,
        ownedCollectibleVideoDataMapper,
        ownedCollectibleMixedDataMapper,
        ownedCollectibleAudioDataMapper,
        ownedCollectibleNotSupportedDataMapper,
        getPrimaryCurrencyAssetParityValue,
        getSecondaryCurrencyAssetParityValue,
        formatAmountByCollectibleFractionalDigit
    )

    @Test
    fun `EXPECT OwnedCollectibleImageData WHEN base collectible detail is ImageCollectibleDetail`() {
        whenever(
            ownedCollectibleImageDataMapper(
                collectibleDetail = IMAGE_COLLECTIBLE_DETAIL,
                amount = AMOUNT,
                formattedAmount = FORMATTED_AMOUNT,
                formattedCompactAmount = FORMATTED_COMPACT_AMOUNT,
                parityValueInSelectedCurrency = PARITY_VALUE_IN_SELECTED_CURRENCY,
                parityValueInSecondaryCurrency = PARITY_VALUE_IN_SECONDARY_CURRENCY,
                optedInAtRound = OPTED_IN_AT_ROUND
            )
        ).thenReturn(OWNED_COLLECTIBLE_IMAGE_DATA)

        val result = sut(ASSET_HOLDING, IMAGE_COLLECTIBLE_DETAIL)

        assertEquals(OWNED_COLLECTIBLE_IMAGE_DATA, result)
    }

    @Test
    fun `EXPECT OwnedCollectibleVideoData WHEN base collectible detail is VideoCollectibleDetail`() {
        whenever(
            ownedCollectibleVideoDataMapper(
                collectibleDetail = VIDEO_COLLECTIBLE_DETAIL,
                amount = AMOUNT,
                formattedAmount = FORMATTED_AMOUNT,
                formattedCompactAmount = FORMATTED_COMPACT_AMOUNT,
                parityValueInSelectedCurrency = PARITY_VALUE_IN_SELECTED_CURRENCY,
                parityValueInSecondaryCurrency = PARITY_VALUE_IN_SECONDARY_CURRENCY,
                optedInAtRound = OPTED_IN_AT_ROUND
            )
        ).thenReturn(OWNED_COLLECTIBLE_VIDEO_DATA)

        val result = sut(ASSET_HOLDING, VIDEO_COLLECTIBLE_DETAIL)

        assertEquals(OWNED_COLLECTIBLE_VIDEO_DATA, result)
    }

    @Test
    fun `EXPECT OwnedCollectibleMixedData WHEN base collectible detail is MixedCollectibleDetail`() {
        whenever(
            ownedCollectibleMixedDataMapper(
                collectibleDetail = MIXED_COLLECTIBLE_DETAIL,
                amount = AMOUNT,
                formattedAmount = FORMATTED_AMOUNT,
                formattedCompactAmount = FORMATTED_COMPACT_AMOUNT,
                parityValueInSelectedCurrency = PARITY_VALUE_IN_SELECTED_CURRENCY,
                parityValueInSecondaryCurrency = PARITY_VALUE_IN_SECONDARY_CURRENCY,
                optedInAtRound = OPTED_IN_AT_ROUND
            )
        ).thenReturn(OWNED_COLLECTIBLE_MIXED_DATA)

        val result = sut(ASSET_HOLDING, MIXED_COLLECTIBLE_DETAIL)

        assertEquals(OWNED_COLLECTIBLE_MIXED_DATA, result)
    }

    @Test
    fun `EXPECT OwnedCollectibleAudioData WHEN base collectible detail is AudioCollectibleDetail`() {
        whenever(
            ownedCollectibleAudioDataMapper(
                collectibleDetail = AUDIO_COLLECTIBLE_DETAIL,
                amount = AMOUNT,
                formattedAmount = FORMATTED_AMOUNT,
                formattedCompactAmount = FORMATTED_COMPACT_AMOUNT,
                parityValueInSelectedCurrency = PARITY_VALUE_IN_SELECTED_CURRENCY,
                parityValueInSecondaryCurrency = PARITY_VALUE_IN_SECONDARY_CURRENCY,
                optedInAtRound = OPTED_IN_AT_ROUND
            )
        ).thenReturn(OWNED_COLLECTIBLE_AUDIO_DATA)

        val result = sut(ASSET_HOLDING, AUDIO_COLLECTIBLE_DETAIL)

        assertEquals(OWNED_COLLECTIBLE_AUDIO_DATA, result)
    }

    @Test
    fun `EXPECT OwnedCollectibleUnsupportedData WHEN base collectible detail is NotSupportedCollectibleDetail`() {
        whenever(
            ownedCollectibleNotSupportedDataMapper(
                collectibleDetail = NOT_SUPPORTED_COLLECTIBLE_DETAIL,
                amount = AMOUNT,
                formattedAmount = FORMATTED_AMOUNT,
                formattedCompactAmount = FORMATTED_COMPACT_AMOUNT,
                parityValueInSelectedCurrency = PARITY_VALUE_IN_SELECTED_CURRENCY,
                parityValueInSecondaryCurrency = PARITY_VALUE_IN_SECONDARY_CURRENCY,
                optedInAtRound = OPTED_IN_AT_ROUND
            )
        ).thenReturn(OWNED_COLLECTIBLE_NOT_SUPPORTED_DATA)

        val result = sut(ASSET_HOLDING, NOT_SUPPORTED_COLLECTIBLE_DETAIL)

        assertEquals(OWNED_COLLECTIBLE_NOT_SUPPORTED_DATA, result)
    }

    @Test
    fun `EXPECT 0 as fraction decimals WHEN collectible decimals is null`() {
        val collectibleDetail = fixtureOf<ImageCollectibleDetail>().copy(
            fractionDecimals = null,
            usdValue = USD_VALUE
        )
        whenever(getPrimaryCurrencyAssetParityValue(USD_VALUE, 0, AMOUNT)).thenReturn(PARITY_VALUE_IN_SELECTED_CURRENCY)
        whenever(getSecondaryCurrencyAssetParityValue(USD_VALUE, 0, AMOUNT))
            .thenReturn(PARITY_VALUE_IN_SECONDARY_CURRENCY)
        whenever(formatAmountByCollectibleFractionalDigit(AMOUNT, 0)).thenReturn(FORMATTED_AMOUNT)
        whenever(formatAmountByCollectibleFractionalDigit(AMOUNT, 0, true)).thenReturn(FORMATTED_COMPACT_AMOUNT)
        whenever(
            ownedCollectibleImageDataMapper(
                collectibleDetail = collectibleDetail,
                amount = AMOUNT,
                formattedAmount = FORMATTED_AMOUNT,
                formattedCompactAmount = FORMATTED_COMPACT_AMOUNT,
                parityValueInSelectedCurrency = PARITY_VALUE_IN_SELECTED_CURRENCY,
                parityValueInSecondaryCurrency = PARITY_VALUE_IN_SECONDARY_CURRENCY,
                optedInAtRound = OPTED_IN_AT_ROUND
            )
        ).thenReturn(OWNED_COLLECTIBLE_IMAGE_DATA)

        val result = sut(ASSET_HOLDING, collectibleDetail)

        assertEquals(OWNED_COLLECTIBLE_IMAGE_DATA, result)
    }

    companion object {
        private val AMOUNT = BigInteger.TEN
        private val USD_VALUE = BigDecimal.TEN
        private const val FRACTION_DECIMALS = 6
        private const val FORMATTED_AMOUNT = "10"
        private const val FORMATTED_COMPACT_AMOUNT = "10"
        private val PARITY_VALUE_IN_SELECTED_CURRENCY = fixtureOf<ParityValue>()
        private val PARITY_VALUE_IN_SECONDARY_CURRENCY = fixtureOf<ParityValue>()
        private val OPTED_IN_AT_ROUND = fixtureOf<Long>()

        private val ASSET_HOLDING = fixtureOf<AssetHolding>().copy(
            amount = AMOUNT,
            optedInAtRound = OPTED_IN_AT_ROUND
        )

        private val IMAGE_COLLECTIBLE_DETAIL = fixtureOf<ImageCollectibleDetail>().copy(
            fractionDecimals = FRACTION_DECIMALS,
            usdValue = USD_VALUE
        )
        private val VIDEO_COLLECTIBLE_DETAIL = fixtureOf<VideoCollectibleDetail>().copy(
            fractionDecimals = FRACTION_DECIMALS,
            usdValue = USD_VALUE
        )
        private val MIXED_COLLECTIBLE_DETAIL = fixtureOf<MixedCollectibleDetail>().copy(
            fractionDecimals = FRACTION_DECIMALS,
            usdValue = USD_VALUE
        )
        private val AUDIO_COLLECTIBLE_DETAIL = fixtureOf<AudioCollectibleDetail>().copy(
            fractionDecimals = FRACTION_DECIMALS,
            usdValue = USD_VALUE
        )
        private val NOT_SUPPORTED_COLLECTIBLE_DETAIL = fixtureOf<NotSupportedCollectibleDetail>().copy(
            fractionDecimals = FRACTION_DECIMALS,
            usdValue = USD_VALUE
        )

        private val OWNED_COLLECTIBLE_IMAGE_DATA = fixtureOf<OwnedCollectibleImageData>()
        private val OWNED_COLLECTIBLE_VIDEO_DATA = fixtureOf<OwnedCollectibleVideoData>()
        private val OWNED_COLLECTIBLE_MIXED_DATA = fixtureOf<OwnedCollectibleMixedData>()
        private val OWNED_COLLECTIBLE_AUDIO_DATA = fixtureOf<OwnedCollectibleAudioData>()
        private val OWNED_COLLECTIBLE_NOT_SUPPORTED_DATA = fixtureOf<OwnedCollectibleUnsupportedData>()

    }
}
