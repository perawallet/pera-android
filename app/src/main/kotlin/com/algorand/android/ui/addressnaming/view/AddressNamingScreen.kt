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

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.addressnaming.model.AddressNamingScreenConfig
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel.ViewEvent.NavToNextScreen
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel.ViewState.Content
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel.ViewState.Idle
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraButtonState.DISABLED
import com.algorand.android.ui.compose.widget.button.PeraButtonState.ENABLED
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.textfield.PeraTextField
import com.algorand.android.ui.compose.widget.textfield.PeraTextFieldIcon
import com.algorand.android.ui.compose.widget.textfield.PeraTextFieldLabel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddressNamingScreen(
    config: AddressNamingScreenConfig,
    listener: AddressNamingScreenListener,
    viewModel: AddressNamingViewModel
) {
    Surface(
        modifier = Modifier
            .background(PeraTheme.colors.background.primary)
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        val state = viewModel.state.collectAsStateWithLifecycle()
        when (val currentState = state.value) {
            Idle -> Unit
            is Content -> {
                Column(
                    modifier = Modifier
                        .background(PeraTheme.colors.background.primary)
                ) {
                    val nameInput = remember { mutableStateOf(currentState.currentName) }
                    TitleText()
                    Spacer(modifier = Modifier.height(14.dp))
                    DescriptionText()
                    Spacer(modifier = Modifier.height(32.dp))
                    NameInput(nameInput)
                    Spacer(modifier = Modifier.weight(1f))
                    ConfirmButton(nameInput, config.buttonResId) {
                        viewModel.saveCustomName(nameInput.value)
                    }
                }
            }
        }

        LaunchedEffect(viewModel.viewEvent) {
            viewModel.viewEvent.collectLatest {
                when (it) {
                    NavToNextScreen -> listener.onNamingCompleted()
                }
            }
        }
    }
}

@Composable
private fun TitleText() {
    Text(
        text = stringResource(R.string.name_your_account),
        style = PeraTheme.typography.title.regular.sansMedium,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun DescriptionText() {
    Text(
        text = stringResource(R.string.name_your_account_to),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun NameInput(nameInput: MutableState<String>) {
    PeraTextField(
        modifier = Modifier.fillMaxWidth(),
        text = nameInput.value,
        onTextChanged = { nameInput.value = it },
        label = { PeraTextFieldLabel(text = stringResource(R.string.account_name)) },
        trailingIcon = if (nameInput.value.isNotBlank()) {
            {
                PeraTextFieldIcon(iconResId = R.drawable.ic_close) {
                    nameInput.value = ""
                }
            }
        } else {
            null
        }
    )
}

@Composable
private fun ConfirmButton(
    nameInput: MutableState<String>,
    @StringRes buttonResId: Int,
    onConfirmClick: () -> Unit
) {
    PeraPrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(buttonResId),
        state = if (nameInput.value.isNotBlank()) ENABLED else DISABLED,
        onClick = onConfirmClick
    )
}

interface AddressNamingScreenListener {
    fun onNamingCompleted()
}

@Preview
@Composable
private fun PreviewAddressNamingScreen() {
    AddressNamingScreenPreview()
}
