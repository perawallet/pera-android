@file:Suppress("EmptyFunctionBlock")
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

package com.algorand.android.ui.addressnaming.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.algorand.android.R
import com.algorand.android.ui.addressnaming.model.AddressNamingScreenConfig
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Preview
@Composable
fun AddressNamingScreenPreview() {
    val listener = object : AddressNamingScreenListener {
        override fun onNamingCompleted() {}
    }

    val viewModel = object : AddressNamingViewModel {
        override fun init(address: String) {}

        override fun saveCustomName(name: String) {}

        override val state: StateFlow<AddressNamingViewModel.ViewState>
            get() = MutableStateFlow(AddressNamingViewModel.ViewState.Content(
                "address",
                currentName = "ADDDRRR...DDDSSS"
            ))
        override val viewEvent: Flow<AddressNamingViewModel.ViewEvent>
            get() = MutableSharedFlow()
    }

    AddressNamingScreen(
        config = AddressNamingScreenConfig(R.string.finish_account_creation),
        listener = listener,
        viewModel = viewModel
    )
}
