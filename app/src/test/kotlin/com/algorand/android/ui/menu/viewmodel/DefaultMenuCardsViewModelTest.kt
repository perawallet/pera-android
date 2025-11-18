/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.algorand.android.ui.menu.viewmodel

import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState
import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.cards.domain.model.CardNftRewardState
import com.algorand.wallet.cards.domain.model.FundAddress
import com.algorand.wallet.cards.domain.usecase.GetCardFundAddresses
import com.algorand.wallet.cards.domain.usecase.IsCountryWaitlistedForCards
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.coJustAwait
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class DefaultMenuCardsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getCardFundAddresses: GetCardFundAddresses = mockk()
    private val isCountryWaitlistedForCards: IsCountryWaitlistedForCards = mockk()

    private val sut = DefaultMenuCardsViewModel(getCardFundAddresses, isCountryWaitlistedForCards, StateDelegate())

    private val stateObserver = sut.state.test()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `EXPECT loading state WHEN making api request`(): TestResult = runTest {
        coJustAwait { getCardFundAddresses() }
        coJustAwait { isCountryWaitlistedForCards() }

        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading)
    }

    @Test
    fun `EXPECT error state WHEN get card fund addresses returns Error`(): TestResult = runTest {
        coEvery { getCardFundAddresses() } returns PeraResult.Error(Exception())
        coEvery { isCountryWaitlistedForCards() } returns PeraResult.Success(true)

        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading, ViewState.Error)
    }

    @Test
    fun `EXPECT error state WHEN country waitlist returns Error`(): TestResult = runTest {
        coEvery { getCardFundAddresses() } returns PeraResult.Success(peraFixture<List<FundAddress>>())
        coEvery { isCountryWaitlistedForCards() } returns PeraResult.Error(Exception())

        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading, ViewState.Error)
    }

    @Test
    fun `EXPECT waitlisted state WHEN country is waitlisted and there are no fund address`(): TestResult = runTest {
        coEvery { getCardFundAddresses() } returns PeraResult.Success(listOf(FUND_ADDRESS.copy(fundAddress = null)))
        coEvery { isCountryWaitlistedForCards() } returns PeraResult.Success(true)

        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading, ViewState.Waitlisted)
    }

    @Test
    fun `EXPECT card created state WHEN there are fund addresses and nft reward is processed`(): TestResult = runTest {
        val fundAddresses = listOf(
            FUND_ADDRESS.copy(fundAddress = "address1", nftRewardState = CardNftRewardState.PROCESSED)
        )
        coEvery { getCardFundAddresses() } returns PeraResult.Success(fundAddresses)
        coEvery { isCountryWaitlistedForCards() } returns PeraResult.Success(true)

        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading, ViewState.CardCreated)
    }

    @Test
    fun `EXPECT card created state WHEN there are fund addresses and nft reward is is_processing`(): TestResult = runTest {
        val fundAddresses = listOf(
            FUND_ADDRESS.copy(fundAddress = "address1", nftRewardState = CardNftRewardState.IS_PROCESSING)
        )
        coEvery { getCardFundAddresses() } returns PeraResult.Success(fundAddresses)
        coEvery { isCountryWaitlistedForCards() } returns PeraResult.Success(true)

        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading, ViewState.CardCreated)
    }

    @Test
    fun `EXPECT new user state WHEN there are fund addresses and no card is created`(): TestResult = runTest {
        val fundAddresses = listOf(
            FUND_ADDRESS.copy(fundAddress = "address1", nftRewardState = CardNftRewardState.NOT_PROCESSED)
        )
        coEvery { getCardFundAddresses() } returns PeraResult.Success(fundAddresses)
        coEvery { isCountryWaitlistedForCards() } returns PeraResult.Success(false)

        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading, ViewState.NewUser)
    }

    @Test
    fun `EXPECT new user state WHEN there are no fund addresses and no card is created`(): TestResult = runTest {
        val fundAddresses = listOf(
            FUND_ADDRESS.copy(fundAddress = null, nftRewardState = CardNftRewardState.NOT_PROCESSED)
        )
        coEvery { getCardFundAddresses() } returns PeraResult.Success(fundAddresses)
        coEvery { isCountryWaitlistedForCards() } returns PeraResult.Success(false)

        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading, ViewState.NewUser)
    }

    @Test
    fun `EXPECT state to be initialized once WHEN init called multiple times`(): TestResult = runTest {
        coEvery { getCardFundAddresses() } returns PeraResult.Error(Exception())
        coEvery { isCountryWaitlistedForCards() } returns PeraResult.Success(true)

        sut.initCardState()
        sut.initCardState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Loading, ViewState.Error)
    }

    private companion object {
        val FUND_ADDRESS = peraFixture<FundAddress>()
    }
}
