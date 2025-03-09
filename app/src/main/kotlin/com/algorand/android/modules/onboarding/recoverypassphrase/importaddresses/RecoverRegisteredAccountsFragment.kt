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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AnimationLoader
import com.algorand.wallet.algosdk.model.RegisteredAlgorandAccount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecoverRegisteredAccountsFragment : DaggerBaseFragment(0) {

    private val args: RecoverRegisteredAccountsFragmentArgs by navArgs()

    private val viewModel: RecoverRegisteredAccountsViewModel by viewModels()

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
                    RecoverRegisteredAccountsScreen(
                        onIntent = viewModel::processIntent
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar(true)
        observeEffects()
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collectLatest { effect ->
                    when (effect) {
                        is RecoverRegisteredAccountsEffect.NavigateToHome -> navToHomeNavigation()
                    }
                }
            }
        }
    }

    @Suppress("LongMethod")
    @Composable
    fun RecoverRegisteredAccountsScreen(
        onIntent: (RecoverRegisteredAccountsIntent) -> Unit
    ) {
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(state.error) {
            state.error?.let { error ->
                scope.launch {
                    snackbarHostState.showSnackbar(error)
                }
            }
        }

        if (state.isLoading) {
            Box {
                AnimationLoader(
                    modifier = Modifier.align(alignment = Alignment.Center),
                    start = ImageVector.vectorResource(R.drawable.ic_ledger_old_export),
                    end = ImageVector.vectorResource(R.drawable.ic_phone_new),
                    lottie = LottieCompositionSpec.RawRes(resId = R.raw.loading_dots),
                    description = "Searching your accounts"
                )
            }
        } else {

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {

                    Text(
                        text = "We found that there are ${state.registeredAccounts.size} addresses " +
                                "registered to this wallet.",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${state.registeredAccounts.size} addresses",
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Select all",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                if (state.selectedAddresses.size ==
                                    state.registeredAccounts.filter { !it.isImportedToDB }.size
                                ) {
                                    onIntent(RecoverRegisteredAccountsIntent.UnselectAllAccounts)
                                } else {
                                    onIntent(RecoverRegisteredAccountsIntent.SelectAllAccounts)
                                }
                            }
                        )
                        Checkbox(
                            checked = state.selectedAddresses.size ==
                                    state.registeredAccounts.filter { !it.isImportedToDB }.size &&
                                    state.registeredAccounts.isNotEmpty(),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    onIntent(RecoverRegisteredAccountsIntent.SelectAllAccounts)
                                } else {
                                    onIntent(RecoverRegisteredAccountsIntent.UnselectAllAccounts)
                                }
                            }
                        )
                    }

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(state.registeredAccounts) { account ->
                            AddressItem(
                                account = account,
                                isChecked = state.selectedAddresses.contains(account.address),
                                onCheckedChange = { isChecked ->
                                    onIntent(
                                        RecoverRegisteredAccountsIntent.ToggleAccountSelection(
                                            address = account.address,
                                            isSelected = isChecked
                                        )
                                    )
                                }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onIntent(RecoverRegisteredAccountsIntent.ImportSelectedAccounts)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.canImport
                    ) {
                        Text(text = "Import")
                    }
                }
            }
        }
    }

    @Composable
    fun AddressItem(
        account: RegisteredAlgorandAccount,
        isChecked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = account.address, fontWeight = FontWeight.SemiBold)
                if (account.isImportedToDB) {
                    Text(
                        text = "ALREADY IMPORTED",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                } else {
                    Text(text = account.algoValue)
                    if (account.usdValue.isNotEmpty()) {
                        Text(text = account.usdValue, fontSize = 12.sp)
                    }
                }
            }
            if (!account.isImportedToDB) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }

    private fun navToHomeNavigation() {
        nav(RecoverRegisteredAccountsFragmentDirections.actionRecoverRegisteredAccountsFragmentToHomeNavigation())
    }

    private fun configureToolbar(isCloseButtonVisible: Boolean) {
        getAppToolbar()?.let { toolbar ->
            if (isCloseButtonVisible) {
                toolbar.configureStartButton(R.drawable.ic_left_arrow, ::navBack)
            }
        }
    }
}
