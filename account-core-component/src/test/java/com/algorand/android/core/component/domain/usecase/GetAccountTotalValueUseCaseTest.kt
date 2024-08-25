package com.algorand.android.core.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.*
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.core.component.domain.model.AccountTotalValue
import com.algorand.android.parity.domain.model.*
import com.algorand.android.parity.domain.usecase.GetAlgoAmountValue
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigDecimal.*
import java.math.BigInteger

internal class GetAccountTotalValueUseCaseTest {

    private val getAccountInformation: GetAccountInformation = mock()
    private val getAssetDetail: GetAssetDetail = mock()
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue = mock()
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue = mock()
    private val getAlgoAmountValue: GetAlgoAmountValue = mock()

    private val sut = GetAccountTotalValueUseCase(
        getAccountInformation,
        getAssetDetail,
        getPrimaryCurrencyAssetParityValue,
        getSecondaryCurrencyAssetParityValue,
        getAlgoAmountValue
    )

    @Test
    fun `EXPECT zero account value WHEN account information is null`() = runTest {
        whenever(getAccountInformation(ADDRESS)).thenReturn(null)

        val result = sut(ADDRESS, any())

        assertEquals(ZERO_ACCOUNT_TOTAL_VALUE, result)
    }

    @Test
    fun `EXPECT account total value without ALGO WHEN assets are cached and include ALGO is false`() = runTest {
        whenever(getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION)
        whenever(getAssetDetail(ASSET_ID_1)).thenReturn(ASSET_DETAIL_1)
        whenever(getAssetDetail(ASSET_ID_2)).thenReturn(ASSET_DETAIL_2)
        whenever(getAssetDetail(ASSET_ID_3)).thenReturn(null)
        whenever(getPrimaryCurrencyAssetParityValue(ONE, DECIMALS, ASSET_1_AMOUNT)).thenReturn(ParityValue(ONE, "A"))
        whenever(getSecondaryCurrencyAssetParityValue(ONE, DECIMALS, ASSET_1_AMOUNT)).thenReturn(ParityValue(ONE, "A"))
        whenever(getPrimaryCurrencyAssetParityValue(ONE, DECIMALS, ASSET_2_AMOUNT)).thenReturn(ParityValue(ONE, "A"))
        whenever(getSecondaryCurrencyAssetParityValue(ONE, DECIMALS, ASSET_2_AMOUNT)).thenReturn(ParityValue(ONE, "A"))

        val result = sut(ADDRESS, false)

        val expected = AccountTotalValue(
            primaryAccountValue = valueOf(2L),
            secondaryAccountValue = valueOf(2L),
            assetCount = 2
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT account total value with ALGO WHEN assets are cached and include ALGO is true`() = runTest {
        whenever(getAccountInformation(ADDRESS)).thenReturn(ACCOUNT_INFORMATION)
        whenever(getAssetDetail(ASSET_ID_1)).thenReturn(ASSET_DETAIL_1)
        whenever(getAssetDetail(ASSET_ID_2)).thenReturn(ASSET_DETAIL_2)
        whenever(getAssetDetail(ASSET_ID_3)).thenReturn(null)
        whenever(getPrimaryCurrencyAssetParityValue(ONE, DECIMALS, ASSET_1_AMOUNT)).thenReturn(ParityValue(ONE, "A"))
        whenever(getSecondaryCurrencyAssetParityValue(ONE, DECIMALS, ASSET_1_AMOUNT)).thenReturn(ParityValue(ONE, "A"))
        whenever(getPrimaryCurrencyAssetParityValue(ONE, DECIMALS, ASSET_2_AMOUNT)).thenReturn(ParityValue(ONE, "A"))
        whenever(getSecondaryCurrencyAssetParityValue(ONE, DECIMALS, ASSET_2_AMOUNT)).thenReturn(ParityValue(ONE, "A"))
        whenever(getAlgoAmountValue(ALGO_AMOUNT)).thenReturn(ALGO_AMOUNT_VALUE)

        val result = sut(ADDRESS, true)

        val expected = AccountTotalValue(
            primaryAccountValue = valueOf(3L),
            secondaryAccountValue = valueOf(3L),
            assetCount = 3
        )
        assertEquals(expected, result)
    }

    private companion object {
        private const val ADDRESS = "address"
        private val ZERO_ACCOUNT_TOTAL_VALUE = AccountTotalValue(ZERO, ZERO, 0)

        private val ASSET_ID_1 = 1L
        private val ASSET_1_AMOUNT = BigInteger.TEN
        private val ASSET_ID_2 = 2L
        private val ASSET_2_AMOUNT = BigInteger.ONE
        private val ASSET_ID_3 = 3L
        private val ASSET_3_AMOUNT = BigInteger.TWO

        private const val DECIMALS = 6

        private val ALGO_AMOUNT = BigInteger.TEN
        private val ALGO_AMOUNT_VALUE =
            AlgoAmountValue(BigInteger.ONE, ParityValue(ONE, "A"), ParityValue(ONE, "A"), ONE)

        private val ASSET_HOLDING_1 = fixtureOf<AssetHolding>().copy(
            assetId = ASSET_ID_1,
            amount = ASSET_1_AMOUNT
        )
        private val ASSET_HOLDING_2 = fixtureOf<AssetHolding>().copy(
            assetId = ASSET_ID_2,
            amount = ASSET_2_AMOUNT
        )
        private val ASSET_HOLDING_3 = fixtureOf<AssetHolding>().copy(
            assetId = ASSET_ID_3,
            amount = ASSET_3_AMOUNT
        )

        private val ASSET_DETAIL_1 = fixtureOf<AssetDetail>().copy(
            usdValue = ONE,
            fractionDecimals = DECIMALS
        )

        private val ASSET_DETAIL_2 = fixtureOf<AssetDetail>().copy(
            usdValue = ONE,
            fractionDecimals = DECIMALS
        )

        private val ASSET_HOLDINGS = listOf(
            ASSET_HOLDING_1,
            ASSET_HOLDING_2,
            ASSET_HOLDING_3
        )

        private val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>().copy(
            assetHoldings = ASSET_HOLDINGS,
            amount = ALGO_AMOUNT
        )
    }
}
