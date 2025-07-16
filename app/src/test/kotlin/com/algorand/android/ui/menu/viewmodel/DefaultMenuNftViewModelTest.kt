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

import com.algorand.android.ui.menu.tracker.MenuEventTracker
import com.algorand.android.ui.menu.viewmodel.MenuNftViewModel.ViewState
import com.algorand.test.test
import com.algorand.wallet.asset.domain.usecase.GetRecentlyAddedCollectibleUrls
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class DefaultMenuNftViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getRecentlyAddedCollectibleUrls: GetRecentlyAddedCollectibleUrls = mockk()
    private val menuEventTracker: MenuEventTracker = mockk()

    private val sut = DefaultMenuNftViewModel(getRecentlyAddedCollectibleUrls, StateDelegate(), menuEventTracker)

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
    fun `EXPECT content state with 3 recently added nft urls`() = runTest {
        val collectibleUrls = listOf("url1", "url2", "url3")
        coEvery { getRecentlyAddedCollectibleUrls(3) } returns collectibleUrls

        sut.initNftState()

        stateObserver.assertValueHistory(ViewState.Idle, ViewState.Content(collectibleUrls))
    }
}
