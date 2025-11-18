package com.algorand.wallet.asset.domain.usecase

import com.algorand.wallet.node.domain.usecase.IsSelectedNodeTestnet
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetUsdcAssetIdUseCaseTest {

    private val isSelectedNodeTestnet: IsSelectedNodeTestnet = mockk()

    private val sut = GetUsdcAssetIdUseCase(isSelectedNodeTestnet)

    @Test
    fun `EXPECT testnet usdc asset id WHEN selected node is testnet`(): TestResult = runTest {
        coEvery { isSelectedNodeTestnet() } returns true

        val result = sut()

        assertEquals(USDC_TESTNET_ID, result)
    }

    @Test
    fun `EXPECT mainnet usdc asset id WHEN selected node is mainnet`(): TestResult = runTest {
        coEvery { isSelectedNodeTestnet() } returns false

        val result = sut()

        assertEquals(USDC_MAINNET_ID, result)
    }

    private companion object {
        const val USDC_TESTNET_ID = 10458941L
        const val USDC_MAINNET_ID = 31566704L
    }
}
