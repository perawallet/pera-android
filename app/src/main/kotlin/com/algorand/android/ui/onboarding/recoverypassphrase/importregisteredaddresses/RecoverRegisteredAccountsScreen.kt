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

package com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AnimationLoader
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraCheckbox
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.text.PeraHeadlineText
import com.algorand.android.ui.compose.widget.text.PeraHighlightedText
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.text.PeraTitleText
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewEvent
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewState
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.ui.rekeyedaccounts.view.FetchingRekeyedAccountsLoadingDialog
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecoverRegisteredAccountsScreen(
    viewModel: RecoverRegisteredAccountsViewModel,
    onNavToHomeNavigation: () -> Unit,
    onNavBack: () -> Unit,
    onNavToRekeyedAccountSelection: (List<RekeyedAccountSelectionNavArg>) -> Unit
) {
    val viewState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.e("RecoverRegisteredAccountsScreen", "LaunchedEffect - Unit")
        viewModel.loadRegisteredAccounts()
    }

    LaunchedEffect(viewModel.viewEvent) {
        Log.e("RecoverRegisteredAccountsScreen", "LaunchedEffect - viewEvent")
        viewModel.viewEvent.collectLatest { event ->
            when (event) {
                is ViewEvent.NavigateToHome -> onNavToHomeNavigation()
                is ViewEvent.NavigateBack -> onNavBack()
                is ViewEvent.NavigateToRekeyedAccountSelection -> onNavToRekeyedAccountSelection(event.args)
            }
        }
    }

    when (val currentState = viewState) {
        is ViewState.Idle -> Unit
        is ViewState.Loading -> LoadingStateContent()
        is ViewState.Content -> ContentStateContent(viewModel, currentState)
    }
}

@Composable
private fun LoadingStateContent() {
    Box {
        AnimationLoader(
            modifier = Modifier.align(alignment = Alignment.Center),
            start = ImageVector.vectorResource(R.drawable.ic_ledger_old_export),
            end = ImageVector.vectorResource(R.drawable.ic_phone_new),
            lottie = LottieCompositionSpec.RawRes(resId = R.raw.loading_dots),
            description = stringResource(R.string.searching_your_accounts)
        )
    }
}

@Composable
private fun ContentStateContent(
    viewModel: RecoverRegisteredAccountsViewModel,
    state: ViewState.Content,
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .background(color = PeraTheme.colors.background.primary)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 24.dp, end = 24.dp)
            ) {
                TitleText()
                DescriptionText(state.registeredAccounts.size)
                ListHeaderContainer(
                    state,
                    onSelectAllAccounts = { viewModel.selectAllAccounts() },
                    onUnselectAllAccounts = { viewModel.unselectAllAccounts() }
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.registeredAccounts) { account ->
                        AddressItem(
                            selectedAddresses = state.selectedAddresses,
                            account = account,
                            onCheckedChange = { isChecked ->
                                viewModel.toggleAccountSelection(account.address, isChecked)
                            }
                        )
                    }
                }

                PeraPrimaryButton(
                    onClick = viewModel::importSelectedAccounts,
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.continue_text),
                    state = if (state.selectedAddresses.isNotEmpty()) {
                        PeraButtonState.ENABLED
                    } else {
                        PeraButtonState.DISABLED
                    }
                )
            }

            if (state.type == ViewState.Content.ContentType.LoadingRekeyedAddresses) {
                FetchingRekeyedAccountsLoadingDialog()
            }
        }
    }
}

@Composable
private fun TitleText() {
    PeraHeadlineText(
        text = stringResource(R.string.select_address_to_add)
    )
}

@Composable
private fun DescriptionText(registeredAccountSize: Int) {
    PeraBodyText(
        modifier = Modifier.padding(top = 10.dp),
        text = pluralStringResource(
            R.plurals.select_accounts_to_add_desc,
            registeredAccountSize,
            registeredAccountSize
        )
    )
}

@Composable
private fun ListHeaderContainer(
    state: ViewState.Content,
    onSelectAllAccounts: () -> Unit,
    onUnselectAllAccounts: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 34.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeraTitleText(
            text = pluralStringResource(
                R.plurals.search_address_count,
                state.registeredAccounts.size,
                state.registeredAccounts.size
            ),
            modifier = Modifier.weight(1f)
        )

        PeraTitleText(
            text = stringResource(R.string.select_all),
            color = MaterialTheme.colorScheme.outline,
        )

        val currentToggleState = if (state.selectedAddresses.isEmpty()) {
            ToggleableState.Off
        } else if (state.selectedAddresses.size == state.registeredAddressesNotImported.size) {
            ToggleableState.On
        } else {
            ToggleableState.Indeterminate
        }

        PeraCheckbox(
            checkedState = { currentToggleState },
            onClick = {
                if (currentToggleState == ToggleableState.On) {
                    onUnselectAllAccounts()
                } else {
                    onSelectAllAccounts()
                }
            }
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun AddressItem(
    selectedAddresses: Set<String>,
    account: RegisteredHdKey,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PeraTitleText(
            modifier = Modifier.fillMaxWidth(0.4f),
            text = account.address.toShortenedAddress().toUpperCase(Locale.current)
        )
        if (account.isImportedToDB) {
            PeraHighlightedText(
                text = stringResource(R.string.already_imported).toUpperCase(Locale.current)
            )
        } else {
            Row {
                Column {
                    PeraTitleText(text = "\u0086${account.algoValue}")
                    PeraBodyText(text = "$${account.usdValue}")
                }
                val current = ToggleableState(selectedAddresses.contains(account.address))
                PeraCheckbox(
                    checkedState = {
                        current
                    },
                    onClick = {
                        if (current == ToggleableState.On) {
                            onCheckedChange(false)
                        } else {
                            onCheckedChange(true)
                        }
                    }
                )
            }
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.surfaceVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
