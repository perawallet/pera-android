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
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.RegisterIntroPreview
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.models.ToolbarConfiguration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverImportHdAddressesFragment : DaggerBaseFragment(0) {

    private val recoverImportHdAddressesViewModel: RecoverImportHdAddressesViewModel by viewModels()

    private val statusBarConfiguration =
        StatusBarConfiguration(backgroundColor = R.color.tertiary_background)

    private val toolbarConfiguration =
        ToolbarConfiguration(backgroundColor = R.color.primary_background)

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration,
        statusBarConfiguration = statusBarConfiguration
    )

    private val registerIntroPreviewCollector: suspend (RegisterIntroPreview) -> Unit = {
        configureToolbar(it.isCloseButtonVisible)
        (activity as MainActivity).hideProgress()
    }

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

    data class AddressData(
        val address: String,
        val amount1: String,
        val amount2: String,
        val alreadyImported: Boolean
    )

    @Suppress("LongMethod")
    @Composable
    fun SelectAccountsToAddScreen() {

        // Dummy data for addresses (replace with your actual data)
        val addressesList = listOf(
            AddressData("G2SAB7...5sEDNAB", "A62,045.00", "$20,006.15", false),
            AddressData("45HFS4...SGSPAF", "ALREADY IMPORTED", "", true),
            AddressData("MUSAB7...KIEDNA", "A16,234.32", "$6,236.32", false),
            AddressData("S454SG...APFORH", "A5,075.00", "$1,406.00", false),
            AddressData("FS454S...45HGTY", "A545.02", "$6,006.15", false)
        )

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
                Text(text = "We found that there are 21 addresses registered to this wallet.",
                    modifier = Modifier.padding(bottom = 16.dp))

                // Address List Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "21 addresses", modifier = Modifier.weight(1f))
                    Text(text = "Select all", fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable {
                        selectedAddresses = if (selectedAddresses.size == addressesList.size) {
                            emptySet()
                        } else {
                            addressesList.filter { !it.alreadyImported }.map { it.address }.toSet()
                        }
                    })
                    Checkbox(checked = selectedAddresses.size == addressesList.filter {
                        !it.alreadyImported }.size,
                        onCheckedChange = {
                        selectedAddresses = if (it) {
                            addressesList.filter { !it.alreadyImported }.map { it.address }.toSet()
                        } else {
                            emptySet()
                        }
                    })
                }

                // Address List
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(addressesList) { address ->
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
                    onClick = { /* Handle continue */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAddresses.isNotEmpty()
                ) {
                    Text(text = "Continue")
                }
            }
        }
    }

    @Composable
    fun AddressItem(address: AddressData, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = address.address, fontWeight = FontWeight.SemiBold)
                if (address.alreadyImported) {
                    Text(text = "ALREADY IMPORTED", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                } else {
                    Text(text = address.amount1)
                    if (address.amount2.isNotEmpty()) {
                        Text(text = address.amount2, fontSize = 12.sp)
                    }
                }
            }
            if (!address.alreadyImported) {
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
