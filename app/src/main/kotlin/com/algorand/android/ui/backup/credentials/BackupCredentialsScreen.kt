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

package com.algorand.android.ui.backup.credentials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.components.BackupEncryptionKeyField
import com.algorand.android.ui.backup.components.BackupMnemonicGrid
import com.algorand.android.ui.backup.components.BackupSectionLabel
import com.algorand.android.ui.backup.components.CopyToClipboardButton
import com.algorand.android.ui.backup.credentials.BackupCredentialsViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.utils.copyToClipboard

@Composable
fun BackupCredentialsScreen(
    viewModel: BackupCredentialsViewModel,
    onCloseClick: () -> Unit,
    onStoreCredentialsClick: () -> Unit
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary)
    ) {
        PeraToolbar(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.backup_credentials),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_close,
                    modifier = Modifier.clickableNoRipple(onClick = onCloseClick)
                )
            }
        )

        when (viewState) {
            is ViewState.Error -> ErrorContent()
            is ViewState.Content -> ContentBody(
                viewState = viewState,
                onStoreCredentialsClick = onStoreCredentialsClick
            )
        }
    }
}

@Composable
private fun ErrorContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.backup_credentials_load_error),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.helper.negative,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ContentBody(
    viewState: ViewState.Content,
    onStoreCredentialsClick: () -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            BackupSectionLabel(textRes = R.string.credential_address)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = viewState.credentialAddress,
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )

            Spacer(modifier = Modifier.height(32.dp))

            BackupSectionLabel(textRes = R.string.passphrase)
            Spacer(modifier = Modifier.height(8.dp))
            BackupMnemonicGrid(mnemonic = viewState.mnemonic)
            Spacer(modifier = Modifier.height(12.dp))
            CopyToClipboardButton(onClick = { context.copyToClipboard(viewState.mnemonic) })

            Spacer(modifier = Modifier.height(32.dp))

            BackupSectionLabel(textRes = R.string.encryption_key)
            Spacer(modifier = Modifier.height(8.dp))
            BackupEncryptionKeyField(
                value = viewState.encryptionKey,
                onCopyClick = { context.copyToClipboard(viewState.encryptionKey) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        PeraPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            onClick = onStoreCredentialsClick,
            text = stringResource(R.string.store_your_credentials)
        )
    }
}
