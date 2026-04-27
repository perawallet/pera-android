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

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.viewmodel.CreateBackupViewModel
import com.algorand.android.ui.backup.viewmodel.CreateBackupViewModel.ViewEvent
import com.algorand.android.ui.backup.viewmodel.CreateBackupViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton

@Composable
fun CreateBackupScreen(
    onBackClick: () -> Unit,
    viewModel: CreateBackupViewModel = hiltViewModel()
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
            text = stringResource(R.string.create_backup),
            style = PeraTheme.typography.title.regular.sansBold,
            color = PeraTheme.colors.text.main
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (viewState) {
            is ViewState.Error -> ErrorContent()
            is ViewState.MnemonicGenerated -> MnemonicContent(
                mnemonic = viewState.mnemonic,
                buttonText = stringResource(R.string.confirm_create_backup),
                buttonState = PeraButtonState.ENABLED,
                onButtonClick = viewModel::confirmBackup
            )
            is ViewState.Loading -> MnemonicContent(
                mnemonic = viewState.mnemonic,
                buttonText = stringResource(R.string.backup_creating),
                buttonState = PeraButtonState.PROGRESS,
                onButtonClick = {}
            )
            is ViewState.Syncing -> SyncingContent()
            is ViewState.Success -> SuccessContent(
                backupId = viewState.backupId,
                salt = viewState.salt
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PeraSecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBackClick,
            text = stringResource(R.string.back)
        )
    }
}

@Composable
private fun ErrorContent() {
    Text(
        text = stringResource(R.string.backup_recovery_phrase_error),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.helper.negative
    )
}

@Composable
private fun MnemonicContent(
    mnemonic: String,
    buttonText: String,
    buttonState: PeraButtonState,
    onButtonClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.backup_recovery_phrase_description),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )

    Spacer(modifier = Modifier.height(16.dp))

    MnemonicDisplay(mnemonic = mnemonic)

    Spacer(modifier = Modifier.height(24.dp))

    PeraPrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onButtonClick,
        text = buttonText,
        state = buttonState
    )
}

@Composable
private fun SyncingContent() {
    Text(
        text = stringResource(R.string.backup_syncing_description),
        style = PeraTheme.typography.body.large.sansMedium,
        color = PeraTheme.colors.text.main
    )

    Spacer(modifier = Modifier.height(16.dp))

    PeraPrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {},
        text = stringResource(R.string.backup_syncing),
        state = PeraButtonState.PROGRESS
    )
}

@Composable
private fun SuccessContent(backupId: String, salt: String) {
    Text(
        text = stringResource(R.string.backup_created_successfully),
        style = PeraTheme.typography.body.large.sansMedium,
        color = PeraTheme.colors.text.main
    )

    Spacer(modifier = Modifier.height(16.dp))

    LabeledText(
        label = stringResource(R.string.backup_id_label),
        value = backupId,
        labelColor = PeraTheme.colors.text.main
    )

    Spacer(modifier = Modifier.height(16.dp))

    LabeledText(
        label = stringResource(R.string.backup_salt_label),
        value = salt,
        labelColor = PeraTheme.colors.helper.negative
    )
}

@Composable
private fun LabeledText(label: String, value: String, labelColor: Color) {
    Text(
        text = label,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = labelColor
    )

    Text(
        text = value,
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun MnemonicDisplay(mnemonic: String) {
    val words = mnemonic.split(" ")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.layer.grayLightest,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        words.chunked(3).forEachIndexed { rowIndex, rowWords ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = rowWords.mapIndexed { colIndex, word ->
                    "${rowIndex * 3 + colIndex + 1}. $word"
                }.joinToString("    "),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main,
                textAlign = TextAlign.Start
            )
            if (rowIndex < (words.size / 3) - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
