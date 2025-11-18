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

package com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.viewmodel.RecoverRegisteredAccountsViewModel
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverRegisteredAccountsFragment : BaseFragment(0), RecoverRegisteredAccountsScreenListener {

    private val viewModel: RecoverRegisteredAccountsViewModel by viewModels()

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = ToolbarConfiguration(backgroundColor = R.color.primary_background),
        statusBarConfiguration = StatusBarConfiguration(backgroundColor = R.color.tertiary_background)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PeraTheme {
                    RecoverRegisteredAccountsScreen(viewModel, listener = this@RecoverRegisteredAccountsFragment)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar()
    }

    private fun configureToolbar() {
        getAppToolbar()?.configureStartButton(R.drawable.ic_left_arrow, ::navBack)
    }

    override fun onNavToHomeNavigation(isNewAccountAdded: Boolean) {
        nav(
            RecoverRegisteredAccountsFragmentDirections.actionRecoverRegisteredAccountsFragmentToHomeNavigation(
                showConfetti = isNewAccountAdded
            )
        )
    }

    override fun onNavBack() {
        navBack()
    }

    override fun onNavToRekeyedAccountSelection(navArgs: List<RekeyedAccountSelectionNavArg>) {
        nav(
            RecoverRegisteredAccountsFragmentDirections
                .actionRecoverRegisteredAccountsFragmentToRecoverHdKeyRekeyedAccountSelectionFragment(
                    navArgs.toTypedArray()
                )
        )
    }

    override fun onNavToNewAddressNamingFragment(address: String) {
        nav(
            RecoverRegisteredAccountsFragmentDirections
                .actionRecoverRegisteredAccountsFragmentToNewAddressNamingFragment(address)
        )
    }
}
