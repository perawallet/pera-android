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

package com.algorand.android.ui.backup.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.components.BackupEncryptionKeyField
import com.algorand.android.ui.backup.components.BackupMnemonicGrid
import com.algorand.android.ui.backup.components.BackupSectionLabel
import com.algorand.android.ui.backup.components.CopyToClipboardButton
import com.algorand.android.ui.backup.viewmodel.CreateBackupViewModel
import com.algorand.android.ui.backup.viewmodel.CreateBackupViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.utils.copyToClipboard

@Composable
fun CreateBackupScreen(
    viewModel: CreateBackupViewModel,
    onBackClick: () -> Unit,
    onProceedClick: (mnemonic: String, encryptionKey: String, walletAddress: String) -> Unit
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        PeraToolbar(
            text = stringResource(R.string.set_up_a_new_cloud_backup),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onBackClick)
                )
            }
        )

        when (viewState) {
            is ViewState.Error -> ErrorContent(messageResId = viewState.messageResId)
            is ViewState.Content -> ReadyContent(
                mnemonic = viewState.mnemonic,
                encryptionKey = viewState.encryptionKey,
                onCopyMnemonicClick = { context.copyToClipboard(viewState.mnemonic) },
                onCopyEncryptionKeyClick = { context.copyToClipboard(viewState.encryptionKey) },
                onProceedClick = {
                    onProceedClick(viewState.mnemonic, viewState.encryptionKey, viewState.walletAddress)
                }
            )
        }
    }
}

@Composable
private fun ErrorContent(messageResId: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(messageResId),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.helper.negative,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReadyContent(
    mnemonic: String,
    encryptionKey: String,
    onCopyMnemonicClick: () -> Unit,
    onCopyEncryptionKeyClick: () -> Unit,
    onProceedClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.cloud_backup_setup_description),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )

            Spacer(modifier = Modifier.height(28.dp))

            BackupSectionLabel(textRes = R.string.passphrase)
            Spacer(modifier = Modifier.height(8.dp))
            BackupMnemonicGrid(mnemonic = mnemonic)
            Spacer(modifier = Modifier.height(12.dp))
            CopyToClipboardButton(onClick = onCopyMnemonicClick)

            Spacer(modifier = Modifier.height(24.dp))

            BackupSectionLabel(textRes = R.string.encryption_key)
            Spacer(modifier = Modifier.height(8.dp))
            BackupEncryptionKeyField(value = encryptionKey, onCopyClick = onCopyEncryptionKeyClick)

            Spacer(modifier = Modifier.height(24.dp))

            CredentialsWarningCard()

            Spacer(modifier = Modifier.height(24.dp))
        }

        PeraPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            onClick = onProceedClick,
            text = stringResource(R.string.proceed)
        )
    }
}

@Composable
private fun CredentialsWarningCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLighter, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_info),
                tint = PeraTheme.colors.text.main,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.required_for_local_encryption),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main
            )
        }
        Text(
            text = stringResource(R.string.backup_credentials_unrecoverable_warning),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}
