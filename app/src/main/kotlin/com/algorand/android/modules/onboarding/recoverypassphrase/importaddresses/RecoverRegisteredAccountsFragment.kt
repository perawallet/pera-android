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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.models.ToolbarConfiguration
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
                MaterialTheme { // Use MaterialTheme from material3
                    SelectAccountsToAddScreen()
                }
            }
        }
    }

    @Suppress("LongMethod")
    @Composable
    fun SelectAccountsToAddScreen() {

        val registeredAccounts by recoverRegisteredAccountsViewModel.registeredAccountsFlow.collectAsState()

        var selectedAddresses by remember { mutableStateOf(setOf<String>()) }

        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Select accounts to add") },
//                    navigationIcon = {
//                        IconButton(onClick = { /* back button press */ }) {
//                            Icon(imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
        //                            contentDescription = "Back")
//                        }
//                    }
//                )
//            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Use innerPadding from Scaffold
                    .padding(16.dp)
            ) {

                // Description
                Text(text = "We found that there are ${registeredAccounts.size} addresses registered to this wallet.",
                    modifier = Modifier.padding(bottom = 16.dp))

                // Address List Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${registeredAccounts.size} addresses", modifier = Modifier.weight(1f))
                    Text(text = "Select all", fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable {
                        selectedAddresses = if (selectedAddresses.size == registeredAccounts.size) {
                            emptySet()
                        } else {
                            registeredAccounts.filter { !it.isImportedToDB }.map { it.address }.toSet()
                        }
                    })
                    Checkbox(checked = selectedAddresses.size == registeredAccounts.filter {
                        !it.isImportedToDB }.size,
                        onCheckedChange = {
                        selectedAddresses = if (it) {
                            registeredAccounts.filter { !it.isImportedToDB }.map { it.address }.toSet()
                        } else {
                            emptySet()
                        }
                    })
                }

                // Address List
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(registeredAccounts) { address ->
                        AddressItem(address, selectedAddresses.contains(address.address)) { isChecked ->
                            selectedAddresses = if (isChecked) {
                                selectedAddresses + address.address
                            } else {
                                selectedAddresses - address.address
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        recoverRegisteredAccountsViewModel
                            .importRegisteredAccounts(selectedAddresses, registeredAccounts)
                        navToHomeNavigation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAddresses.isNotEmpty()
                ) {
                    Text(text = "Import")
                }
            }
        }
    }

    @Composable
    fun AddressItem(account: RegisteredAlgorandAccount, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = account.address, fontWeight = FontWeight.SemiBold)
                if (account.isImportedToDB) {
                    Text(text = "ALREADY IMPORTED", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                } else {
                    Text(text = account.algoValue)
                    if (account.usdValue.isNotEmpty()) {
                        Text(text = account.usdValue, fontSize = 12.sp)
                    }
                }
            }
            if (!account.isImportedToDB) {
                Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
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
