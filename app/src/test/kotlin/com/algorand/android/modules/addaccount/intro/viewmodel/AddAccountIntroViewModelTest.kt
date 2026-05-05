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

package com.algorand.android.modules.addaccount.intro.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.intro.domain.usecase.CreateAlgo25Account
import com.algorand.android.modules.addaccount.intro.domain.usecase.CreateHdKeyAccount
import com.algorand.android.modules.tracking.onboarding.register.registerintro.RegisterIntroFragmentEventTracker
import com.algorand.android.usecase.RegistrationUseCase
import com.algorand.wallet.account.local.domain.usecase.GetHasAnyHdSeedId
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class AddAccountIntroViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val hasAnyHdSeedId: GetHasAnyHdSeedId = mockk()
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount = mockk()
    private val registrationUseCase: RegistrationUseCase = mockk(relaxed = true)
    private val createHdKeyAccount: CreateHdKeyAccount = mockk()
    private val createAlgo25Account: CreateAlgo25Account = mockk()
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled = mockk()
    private val peraEventTracker: PeraEventTracker = mockk(relaxed = true)
    private val registerIntroFragmentEventTracker: RegisterIntroFragmentEventTracker = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun getContentState(
        viewModel: AddAccountIntroViewModel
    ): AddAccountIntroViewModel.ViewState.Content {
        return viewModel.state.value as AddAccountIntroViewModel.ViewState.Content
    }

    @Test
    fun `EXPECT skip button visible WHEN no local accounts exist`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns false

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(
            "State should be Content but was ${state::class.simpleName}",
            state is AddAccountIntroViewModel.ViewState.Content
        )
        assertTrue((state as AddAccountIntroViewModel.ViewState.Content).isSkipButtonVisible)
    }

    @Test
    fun `EXPECT skip button hidden WHEN local accounts exist`() = runTest {
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns true

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = getContentState(viewModel)
        assertFalse(state.isSkipButtonVisible)
    }

    @Test
    fun `EXPECT primaryAccountOption AddAccount WHEN hd seed exists`() = runTest {
        coEvery { hasAnyHdSeedId() } returns true
        coEvery { isThereAnyLocalAccount() } returns true

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = getContentState(viewModel)
        assertTrue(state.primaryAccountOption is AddAccountIntroViewModel.PrimaryAccountOption.AddAccount)
    }

    @Test
    fun `EXPECT close button visible WHEN isShowingCloseButton is true`() = runTest {
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns false

        val viewModel = createViewModel(isShowingCloseButton = true)
        advanceUntilIdle()

        val state = getContentState(viewModel)
        assertTrue(state.isCloseButtonVisible)
    }

    @Test
    fun `EXPECT createHdKeyAccount invokes use case`() = runTest {
        val expectedResult = Result.Success(mockk<AccountCreation>())
        coEvery { hasAnyHdSeedId() } returns true
        coEvery { isThereAnyLocalAccount() } returns true
        every { createHdKeyAccount() } returns expectedResult

        val viewModel = createViewModel()
        advanceUntilIdle()
        getContentState(viewModel)

        val result = viewModel.createHdKeyAccount()

        assertEquals(expectedResult, result)
        verify { createHdKeyAccount() }
    }

    @Test
    fun `EXPECT createAlgo25Account invoked and NavigateToNameRegistration WHEN onCreateAlgo25AccountClicked`() =
        runTest {
            val accountCreation = mockk<AccountCreation>()
            val expectedResult = Result.Success(accountCreation)
            coEvery { hasAnyHdSeedId() } returns true
            coEvery { isThereAnyLocalAccount() } returns true
            coEvery { createAlgo25Account() } returns expectedResult

            val eventDelegate = EventDelegate<AddAccountIntroViewModel.ViewEvent>()
            val viewModel = createViewModel(eventDelegate = eventDelegate)
            advanceUntilIdle()
            getContentState(viewModel)

            val events = mutableListOf<AddAccountIntroViewModel.ViewEvent>()
            val job = launch {
                eventDelegate.viewEvent.take(1).toList(events)
            }
            viewModel.onCreateAlgo25AccountClicked()
            advanceUntilIdle()

            coVerify { createAlgo25Account() }
            assertTrue(events.any { it is AddAccountIntroViewModel.ViewEvent.NavigateToNameRegistration })
            job.cancel()
        }

    @Test
    fun `EXPECT jointAccountOption Visible WHEN feature toggle enabled`() = runTest {
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns false
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = getContentState(viewModel)
        assertTrue(state.jointAccountOption is AddAccountIntroViewModel.JointAccountOption.Visible)
    }

    @Test
    fun `EXPECT registration skip set WHEN onSkipClicked`() = runTest {
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns false

        val viewModel = createViewModel()
        advanceUntilIdle()
        getContentState(viewModel)

        viewModel.onSkipClicked()
        advanceUntilIdle()

        verify { registrationUseCase.setRegistrationSkipPreferenceAsSkipped() }
    }

    @Test
    fun `EXPECT event logged WHEN logOnboardingWelcomeAccountCreateClickEvent called`() = runTest {
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns false

        val viewModel = createViewModel()
        advanceUntilIdle()
        getContentState(viewModel)

        viewModel.logOnboardingWelcomeAccountCreateClickEvent()
        advanceUntilIdle()

        coVerify { registerIntroFragmentEventTracker.logOnboardingCreateNewAccountEventTracker() }
    }

    @Test
    fun `EXPECT event logged WHEN logOnboardingWelcomeAccountRecoverClickEvent called`() = runTest {
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns false

        val viewModel = createViewModel()
        advanceUntilIdle()
        getContentState(viewModel)

        viewModel.logOnboardingWelcomeAccountRecoverClickEvent()
        advanceUntilIdle()

        coVerify { registerIntroFragmentEventTracker.logOnboardingWelcomeAccountRecoverEvent() }
    }

    private fun createViewModel(
        isShowingCloseButton: Boolean = false,
        eventDelegate: EventDelegate<AddAccountIntroViewModel.ViewEvent> = EventDelegate()
    ): AddAccountIntroViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("isShowingCloseButton" to isShowingCloseButton))
        return AddAccountIntroViewModel(
            stateDelegate = StateDelegate(),
            eventDelegate = eventDelegate,
            hasAnyHdSeedId = hasAnyHdSeedId,
            isThereAnyLocalAccount = isThereAnyLocalAccount,
            registrationUseCase = registrationUseCase,
            createHdKeyAccount = createHdKeyAccount,
            createAlgo25Account = createAlgo25Account,
            isFeatureToggleEnabled = isFeatureToggleEnabled,
            peraEventTracker = peraEventTracker,
            registerIntroFragmentEventTracker = registerIntroFragmentEventTracker,
            savedStateHandle = savedStateHandle
        )
    }
}
