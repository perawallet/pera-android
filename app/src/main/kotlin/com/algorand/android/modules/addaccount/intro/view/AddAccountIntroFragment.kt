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

package com.algorand.android.modules.addaccount.intro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.algorand.android.LoginNavigationDirections
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.TextButton
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.intro.viewmodel.AddAccountIntroViewModel
import com.algorand.android.modules.addaccount.joint.info.ui.JointAccountInfoDialogDelegate
import com.algorand.android.modules.addaccount.joint.tracking.JointAccountCreationEventTracker
import com.algorand.android.modules.tracking.core.PeraClickEvent
import javax.inject.Inject
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAccountIntroFragment : DaggerBaseFragment(0), AddAccountIntroScreenListener {

    private companion object {
        const val CREATE_ACC_ADD_NEW_ACCOUNT_PRESS = "createacc_addNewAccount_press"
        const val CREATE_ACC_ADD_NEW_WALLET_PRESS = "createacc_addNewWallet_press"
        const val CREATE_ACC_WATCH_ACCOUNT_PRESS = "createacc_watchAccount_press"
        const val CREATE_ACC_IMPORT_ACCOUNT_PRESS = "createacc_importAccount_press"
        const val CREATE_ACC_ADD_25_ACCOUNT_PRESS = "createacc_add25Account_press"
    }

    private val viewModel: AddAccountIntroViewModel by viewModels()

    @Inject
    lateinit var jointAccountCreationEventTracker: JointAccountCreationEventTracker

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val jointAccountInfoDialogDelegate by lazy {
        JointAccountInfoDialogDelegate(
            onContinueClick = ::navToJointAccountFlow,
            onGoBackClick = {
                lifecycleScope.launch { jointAccountCreationEventTracker.logOnbJointAccountInfoScreenGoBackPress() }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            AddAccountIntroScreen(
                listener = this@AddAccountIntroFragment,
                viewModel = viewModel
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            viewModel.state,
            ::handleStateChange
        )
        viewLifecycleOwner.collectLatestOnLifecycle(
            viewModel.viewEvent,
            ::handleViewEvent
        )
    }

    private suspend fun handleStateChange(state: AddAccountIntroViewModel.ViewState) {
        when (state) {
            is AddAccountIntroViewModel.ViewState.Loading -> Unit
            is AddAccountIntroViewModel.ViewState.Content -> {
                configureToolbar(state.isCloseButtonVisible, state.isSkipButtonVisible)
                (activity as? MainActivity)?.hideProgress()
            }
        }
    }

    override fun onAddAccountClick() {
        viewModel.logOnboardingWelcomeAccountCreateClickEvent()
        viewModel.logEvent(CREATE_ACC_ADD_NEW_ACCOUNT_PRESS)
        nav(
            AddAccountIntroFragmentDirections.actionRegisterIntroFragmentToHdWalletSelectionFragment()
        )
    }

    override fun onAddJointAccountClick() {
        viewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_CREATE_WALLET)
        context?.let { jointAccountInfoDialogDelegate.show(it) }
    }

    override fun onImportAccountClick() {
        viewModel.logOnboardingWelcomeAccountRecoverClickEvent()
        viewModel.logEvent(CREATE_ACC_IMPORT_ACCOUNT_PRESS)
        nav(AddAccountIntroFragmentDirections.actionRegisterIntroFragmentToRecoveryTypeSelectionNavigation())
    }

    override fun onWatchAddressClick() {
        viewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_WELCOME_WATCH)
        viewModel.logEvent(CREATE_ACC_WATCH_ACCOUNT_PRESS)
        nav(AddAccountIntroFragmentDirections.actionRegisterIntroFragmentToWatchAccountInfoFragment())
    }

    override fun onCreateUniversalWalletClick() {
        viewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_CREATE_WALLET)
        viewModel.logEvent(CREATE_ACC_ADD_NEW_WALLET_PRESS)
        when (val result = viewModel.createHdKeyAccount()) {
            is Result.Success -> {
                nav(
                    AddAccountIntroFragmentDirections
                        .actionRegisterIntroFragmentToCreateWalletNameRegistrationNavigation(result.data)
                )
            }

            is Result.Error -> {
                showGlobalError(getString(R.string.account_create_failed))
            }
        }
    }

    override fun onCreateAlgo25AccountClick() {
        viewModel.logEvent(CREATE_ACC_ADD_25_ACCOUNT_PRESS)
        viewModel.onCreateAlgo25AccountClicked()
    }

    override fun onCloseClick() {
        navBack()
    }

    private fun configureToolbar(isCloseButtonVisible: Boolean, isSkipButtonVisible: Boolean) {
        getAppToolbar()?.let { toolbar ->
            if (isCloseButtonVisible) {
                toolbar.configureStartButton(R.drawable.ic_close, ::navBack)
            }
            if (isSkipButtonVisible) {
                toolbar.setEndButton(button = TextButton(R.string.skip, onClick = ::onSkipClick))
            }
        }
    }

    private fun navToJointAccountFlow() {
        viewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_CREATE_WALLET)
        lifecycleScope.launch { jointAccountCreationEventTracker.logOnbJointAccountWelcomePress() }
        nav(
            AddAccountIntroFragmentDirections
                .actionRegisterIntroFragmentToCreateJointAccountFragment()
        )
    }

    private fun onSkipClick() {
        viewModel.onSkipClicked()
    }

    private fun handleViewEvent(event: AddAccountIntroViewModel.ViewEvent) {
        when (event) {
            is AddAccountIntroViewModel.ViewEvent.NavigateToHome -> {
                nav(LoginNavigationDirections.actionGlobalToHomeNavigation())
            }

            is AddAccountIntroViewModel.ViewEvent.NavigateToNameRegistration -> {
                nav(
                    AddAccountIntroFragmentDirections
                        .actionRegisterIntroFragmentToCreateWalletNameRegistrationNavigation(event.accountCreation)
                )
            }

            is AddAccountIntroViewModel.ViewEvent.ShowError -> {
                showGlobalError(getString(R.string.account_create_failed))
            }
        }
    }
}
