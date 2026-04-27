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

package com.algorand.android.ui.backup.restore

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.ui.backup.viewmodel.RestoreBackupViewModel
import com.algorand.android.ui.backup.viewmodel.RestoreBackupViewModel.ViewEvent
import com.algorand.android.ui.backup.viewmodel.RestoreBackupViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.textfield.PeraTextField

@Composable
fun RestoreBackupScreen(
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit,
    viewModel: RestoreBackupViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.ShowError -> {
                    Toast.makeText(context, event.messageResId, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.restore_backup),
            style = PeraTheme.typography.title.regular.sansBold,
            color = PeraTheme.colors.text.main
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (viewState) {
            is ViewState.Idle -> {
                PeraTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = viewState.mnemonic,
                    onTextChanged = viewModel::updateMnemonic,
                    hint = stringResource(R.string.backup_mnemonic_hint),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                PeraTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = viewState.salt,
                    onTextChanged = viewModel::updateSalt,
                    hint = stringResource(R.string.backup_salt_hint)
                )

                Spacer(modifier = Modifier.height(24.dp))

                PeraPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::restoreBackup,
                    text = stringResource(R.string.restore_backup),
                    state = if (viewState.mnemonic.isBlank() || viewState.salt.isBlank()) {
                        PeraButtonState.DISABLED
                    } else {
                        PeraButtonState.ENABLED
                    }
                )
            }

            is ViewState.Loading -> {
                PeraTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = viewState.mnemonic,
                    onTextChanged = {},
                    hint = stringResource(R.string.backup_mnemonic_hint),
                    singleLine = false,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                PeraTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = viewState.salt,
                    onTextChanged = {},
                    hint = stringResource(R.string.backup_salt_hint),
                    enabled = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                PeraPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                    text = stringResource(R.string.backup_restoring),
                    state = PeraButtonState.PROGRESS
                )
            }

            is ViewState.Success -> {
                Text(
                    text = stringResource(R.string.backup_restored_successfully),
                    style = PeraTheme.typography.body.large.sansMedium,
                    color = PeraTheme.colors.text.main
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.backup_id_value, viewState.backupId),
                    style = PeraTheme.typography.body.regular.sans,
                    color = PeraTheme.colors.text.gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.backup_items_found, viewState.itemCount),
                    style = PeraTheme.typography.body.regular.sans,
                    color = PeraTheme.colors.text.gray
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (viewState is ViewState.Success) {
            PeraPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCompleteClick,
                text = stringResource(R.string.done)
            )
        } else {
            PeraSecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBackClick,
                text = stringResource(R.string.back)
            )
        }
    }
}
