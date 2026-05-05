/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register.recoveraccounttypeselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountRecoveryTypeSelectionFragment : DaggerBaseFragment(0),
    AccountRecoveryTypeSelectionScreenListener {

    private val viewStateCollector: suspend (AccountRecoveryTypeSelectionViewModel.ViewState) -> Unit = { state ->
        when (state) {
            AccountRecoveryTypeSelectionViewModel.ViewState.Idle -> Unit
            is AccountRecoveryTypeSelectionViewModel.ViewState.NoLocalAccountState -> Unit
        }
    }

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack,
        backgroundColor = R.color.primary_background
    )

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    private val accountRecoveryTypeSelectionViewModel: AccountRecoveryTypeSelectionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = accountRecoveryTypeSelectionViewModel.state,
            collection = viewStateCollector
        )
    }

    private fun navToAlgorandSecureRestoreNavigation() {
        nav(
            AccountRecoveryTypeSelectionFragmentDirections
                .actionAccountRecoveryTypeSelectionFragmentToAsbImportNavigation()
        )
    }

    private fun navToRecoverWithPassphraseQrScannerFragment() {
        nav(
            AccountRecoveryTypeSelectionFragmentDirections
                .actionAccountRecoveryTypeSelectionFragmentToRecoverWithPassphraseQrScannerFragment()
        )
    }

    private fun navToPairLedgerNavigation() {
        nav(
            AccountRecoveryTypeSelectionFragmentDirections
                .actionAccountRecoveryTypeSelectionFragmentToPairLedgerNavigation()
        )
    }

    private fun navToImportFromWeb() {
        nav(
            AccountRecoveryTypeSelectionFragmentDirections
                .actionAccountRecoveryTypeSelectionFragmentToWebImportNavigation()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PeraTheme {
                    AccountRecoveryTypeSelectionScreen(
                        listener = this@AccountRecoveryTypeSelectionFragment
                    )
                }
            }
        }
    }

    override fun onNavigateToRecoverAccountInfo(onboardingAccountType: OnboardingAccountType) {
        accountRecoveryTypeSelectionViewModel.logRecoverAccountTypeClickEvent(onboardingAccountType)
        nav(
            AccountRecoveryTypeSelectionFragmentDirections
                .actionAccountRecoveryTypeSelectionFragmentToRecoverAccountInfoFragment(
                    onboardingAccountType = onboardingAccountType
                )
        )
    }

    override fun onRecoverWithQRClick() {
        navToRecoverWithPassphraseQrScannerFragment()
    }

    override fun onPairLedgerClick() {
        navToPairLedgerNavigation()
    }

    override fun onImportFromWebClick() {
        navToImportFromWeb()
    }

    override fun onAlgorandSecureBackupClick() {
        navToAlgorandSecureRestoreNavigation()
    }
}
