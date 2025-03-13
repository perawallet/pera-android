/*
 * Copyright 2022 Pera Wallet, LDA
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

package com.algorand.wallet.ui.accountdetail.viewpassphrase

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.account.local.domain.model.AccountMnemonic
import com.algorand.wallet.account.local.domain.usecase.GetAccountMnemonic
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewEvent
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewState
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class ViewPassphraseViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getAccountMnemonic: GetAccountMnemonic = mockk()

    private val sut = ViewPassphraseViewModel(getAccountMnemonic, StateDelegate(), EventDelegate())

    private val stateObserver = sut.state.test()
    private val eventObserver = sut.viewEvent.test()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `EXPECT loading WHEN preparing the ui`() = runTest {
        coEvery { getAccountMnemonic(ADDRESS) } coAnswers { awaitCancellation() }

        sut.initViewState(ADDRESS)

        stateObserver.assertValueHistory(
            ViewState.Idle,
            ViewState.Loading
        )
    }

    @Test
    fun `EXPECT content state with mnemonics WHEN get mnemonic is successful`() = runTest {
        coEvery { getAccountMnemonic(ADDRESS) } returns PeraResult.Success(ACCOUNT_MNEMONIC)

        sut.initViewState(ADDRESS)

        stateObserver.assertValueHistory(
            ViewState.Idle,
            ViewState.Loading,
            ViewState.Content(ACCOUNT_MNEMONIC.words)
        )
    }

    @Test
    fun `EXPECT generic error and navigate back WHEN get mnemonic fails`() = runTest {
        coEvery { getAccountMnemonic(ADDRESS) } returns PeraResult.Error(Exception())

        sut.initViewState(ADDRESS)

        stateObserver.assertValueHistory(
            ViewState.Idle,
            ViewState.Loading
        )
        eventObserver.assertValueHistory(
            ViewEvent.ShowGenericError,
            ViewEvent.NavigateBack
        )
    }

    private companion object {
        const val ADDRESS = "address"
        val ACCOUNT_MNEMONIC = peraFixture<AccountMnemonic>()
    }
}
