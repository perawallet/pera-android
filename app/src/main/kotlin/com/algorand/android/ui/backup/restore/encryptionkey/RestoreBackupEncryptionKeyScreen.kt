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

package com.algorand.android.ui.backup.restore.encryptionkey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.restore.encryptionkey.RestoreBackupEncryptionKeyViewModel.ViewEvent
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.textfield.PeraTextField
import com.algorand.android.ui.compose.widget.textfield.PeraTextFieldLabel

@Composable
fun RestoreBackupEncryptionKeyScreen(
    viewModel: RestoreBackupEncryptionKeyViewModel,
    onBackClick: () -> Unit,
    onBackupRestored: () -> Unit,
    onShowError: (errorMessageResId: Int) -> Unit
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                ViewEvent.BackupRestored -> onBackupRestored()
                is ViewEvent.ShowError -> onShowError(event.messageResId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary)
    ) {
        PeraToolbar(
            text = stringResource(R.string.enter_encryption_key),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onBackClick)
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.enter_encryption_key_description),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )

            Spacer(modifier = Modifier.height(24.dp))

            PeraTextFieldLabel(text = stringResource(R.string.encryption_key))

            PeraTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                text = viewState.encryptionKey,
                onTextChanged = viewModel::updateEncryptionKey,
                singleLine = true,
                enabled = !viewState.isRestoring
            )

            Spacer(modifier = Modifier.height(32.dp))

            PeraPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    viewModel.restore()
                },
                text = stringResource(R.string.enable_cloud_backup_now),
                state = when {
                    viewState.isRestoring -> PeraButtonState.PROGRESS
                    viewState.isProceedEnabled -> PeraButtonState.ENABLED
                    else -> PeraButtonState.DISABLED
                }
            )
        }
    }
}
