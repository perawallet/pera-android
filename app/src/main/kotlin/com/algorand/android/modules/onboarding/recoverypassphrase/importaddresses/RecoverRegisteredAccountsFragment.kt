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

package com.algorand.android.modules.onboarding.recoverypassphrase.importaddresses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraCheckbox
import com.algorand.android.ui.compose.widget.PeraHeadlineText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraScrimText
import com.algorand.android.ui.compose.widget.PeraTitleText
import com.algorand.wallet.algosdk.model.RegisteredAlgorandAccount
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverRegisteredAccountsFragment : DaggerBaseFragment(0) {

    private val args: RecoverRegisteredAccountsFragmentArgs by navArgs()

    private val recoverRegisteredAccountsViewModel: RecoverRegisteredAccountsViewModel by viewModels()

    private val statusBarConfiguration =
        StatusBarConfiguration(backgroundColor = R.color.tertiary_background)

    private val toolbarConfiguration =
        ToolbarConfiguration(backgroundColor = R.color.primary_background)

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration,
        statusBarConfiguration = statusBarConfiguration
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PeraTheme {
                    SelectAccountsToAddScreen()
                }
            }
        }
    }

    @Suppress("LongMethod")
    @Composable
    fun SelectAccountsToAddScreen() {
        val registeredAccounts by recoverRegisteredAccountsViewModel.registeredAccountsFlow.collectAsState()
        val selectedAddresses = remember { mutableStateOf(setOf<String>()) }
            Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 24.dp, end = 24.dp)
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val selectAllCheckedState = remember {
                    mutableStateOf(
                        if (selectedAddresses.value.size == 0) {
                            ToggleableState.Off
                        } else if (selectedAddresses.value.size == registeredAccounts.filter {
                                it.isImportedToDB.not()
                            }.size) {
                            ToggleableState.Indeterminate
                        } else {
                            ToggleableState.On
                        }
                    )
                }
                PeraHeadlineText(text = stringResource(R.string.select_address_to_add))
                PeraBodyText(
                    modifier = Modifier.padding(top = 10.dp),
                    text = pluralStringResource(
                        R.plurals.select_accounts_to_add_desc,
                        registeredAccounts.size
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 34.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PeraTitleText(
                        text = pluralStringResource(
                            R.plurals.search_address_count,
                            registeredAccounts.size
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    PeraScrimText(
                        modifier = Modifier.clickable {
                            selectedAddresses.value =
                                if (selectedAddresses.value.size == registeredAccounts.size) {
                                    mutableSetOf()
                                } else {
                                    registeredAccounts.filter { it.isImportedToDB.not() }
                                        .map { it.address }.toMutableSet()
                                }
                        },
                        text = stringResource(R.string.select_all)
                    )

                    PeraCheckbox(
                        interactionSource = interactionSource,
                        checkedState = { selectAllCheckedState.value },
                        onClick = {
                            selectedAddresses.value =
                                if (selectedAddresses.value.size < registeredAccounts.size) {
                                    selectAllCheckedState.value = ToggleableState.On
                                    registeredAccounts.filter { !it.isImportedToDB }
                                        .map { it.address }
                                        .toSet()
                                } else {
                                    selectAllCheckedState.value = ToggleableState.Off
                                    emptySet()
                                }
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp)
                ) {
                    items(registeredAccounts) { address ->
                        AddressItem(
                            account = address,
                            interactionSource = interactionSource,
                            startCheckState = selectedAddresses.value.contains(address.address)
                        ) { isChecked ->
                            selectedAddresses.value = if (isChecked) {
                                selectedAddresses.value + address.address
                            } else {
                                selectedAddresses.value - address.address
                            }
                            if (selectedAddresses.value.size == registeredAccounts.size) {
                                selectAllCheckedState.value = ToggleableState.On
                            } else if (selectedAddresses.value.isEmpty()) {
                                selectAllCheckedState.value = ToggleableState.Off
                            } else {
                                selectAllCheckedState.value = ToggleableState.Indeterminate
                            }
                        }
                    }
                }
                PeraPrimaryButton(
                    onClick = {
                        /* Handle continue */
                    },
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.continue_text),
                    enabled = {
                        selectedAddresses.value.isNotEmpty()
                    }
                )
            }
        }
    }

    @Suppress("MagicNumber")
    @Composable
    fun AddressItem(
        account: RegisteredAlgorandAccount,
        interactionSource: MutableInteractionSource,
        startCheckState: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        val checkedState = remember {
            mutableStateOf(
                ToggleableState(startCheckState)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PeraTitleText(
                modifier = Modifier.fillMaxWidth(0.4f),
                text = account.address.toUpperCase(Locale.current)
            )
            if (account.isImportedToDB) {
                PeraHeadlineText(
                    text = stringResource(R.string.already_imported)
                        .toUpperCase(Locale.current)
                )
            } else {
                Row {
                    Column {
                        PeraTitleText(text = "\u0086${account.algoValue}")
                        if (account.usdValue.isNotEmpty()) {
                            PeraBodyText(text = "$${account.usdValue}")
                        }
                    }

                    PeraCheckbox(
                        interactionSource = interactionSource,
                        checkedState = { checkedState.value },
                        onClick = {
                            checkedState.value = if (checkedState.value == ToggleableState.On) {
                                onCheckedChange(false)
                                ToggleableState.Off
                            } else {
                                onCheckedChange(true)
                                ToggleableState.On
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    fun initObservers() {
//        viewLifecycleOwner.collectLatestOnLifecycle(
//            registerIntroViewModel.registerIntroPreviewFlow.filterNotNull(),
//            registerIntroPreviewCollector
//        )
    }

    //    private fun navToAccountRecoveryTypeSelectionFragment() {
//        registerIntroViewModel.logOnboardingWelcomeAccountRecoverClickEvent()
//        nav(RegisterIntroFragmentDirections.actionRegisterIntroFragmentToAccountRecoveryTypeSelectionFragment())
//    }
    private fun configureToolbar(isCloseButtonVisible: Boolean) {
        getAppToolbar()?.let { toolbar ->
            if (isCloseButtonVisible) {
                toolbar.configureStartButton(R.drawable.ic_left_arrow, ::navBack)
            }
        }
    }
}
