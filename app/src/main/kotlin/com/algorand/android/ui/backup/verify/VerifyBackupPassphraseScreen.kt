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

package com.algorand.android.ui.backup.verify

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.verify.VerifyBackupPassphraseViewModel.ViewEvent
import com.algorand.android.ui.backup.verify.VerifyBackupPassphraseViewModel.ViewState
import com.algorand.android.ui.backup.verify.model.BackupConfirmationResult
import com.algorand.android.ui.backup.verify.model.BackupPassphraseValidationOption
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.bottomsheet.rememberPeraSheetState
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.utils.copyToClipboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyBackupPassphraseScreen(
    viewModel: VerifyBackupPassphraseViewModel,
    onBackClick: () -> Unit,
    onBackupCreated: () -> Unit,
    onShowError: (errorMessageResId: Int) -> Unit
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val confirmationSheetState = rememberPeraSheetState(scope)

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                ViewEvent.ShowBackupConfirmationDialog -> confirmationSheetState.show()
                ViewEvent.BackupCreated -> onBackupCreated()
                ViewEvent.BackupCreationFailed -> onShowError(R.string.backup_creation_failed)
                ViewEvent.BackupAlreadyExists -> onShowError(R.string.backup_already_exists)
                ViewEvent.IncorrectSelection -> onShowError(R.string.selected_words_are)
            }
        }
    }

    val isSelectionEnabled = viewState.type is ViewState.Type.Selection

    Column(modifier = Modifier.fillMaxSize()) {
        PeraToolbar(
            text = stringResource(R.string.verify_passphrase),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onBackClick)
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.verify_passphrase_description),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            Spacer(modifier = Modifier.height(28.dp))

            viewState.options.forEachIndexed { index, option ->
                ValidationCard(
                    option = option,
                    isSelectionEnabled = isSelectionEnabled,
                    onOptionClick = { word -> viewModel.selectOption(option.id, word) }
                )
                if (index != viewState.options.lastIndex) {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        PeraPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            onClick = viewModel::verifySelections,
            text = stringResource(R.string.proceed),
            state = when {
                viewState.type is ViewState.Type.CreatingBackup -> PeraButtonState.PROGRESS
                viewState.isProceedEnabled -> PeraButtonState.ENABLED
                else -> PeraButtonState.DISABLED
            }
        )
    }

    confirmationSheetState.SheetContent {
        BackupConfirmationBottomSheet(
            sheetState = confirmationSheetState.sheetState,
            encryptionKey = viewState.encryptionKey,
            onCopyEncryptionKeyClick = { context.copyToClipboard(viewState.encryptionKey) },
            onResult = { result ->
                when (result) {
                    BackupConfirmationResult.EnableCloudBackup -> {
                        confirmationSheetState.hide()
                        viewModel.enableCloudBackup()
                    }
                    BackupConfirmationResult.ShowCredentialsAgain -> {
                        confirmationSheetState.hide()
                        onBackClick()
                    }
                    BackupConfirmationResult.Dismiss -> confirmationSheetState.hide()
                }
            }
        )
    }
}

@Composable
private fun ValidationCard(
    option: BackupPassphraseValidationOption,
    isSelectionEnabled: Boolean,
    onOptionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.select_word_number, option.wordPosition),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = PeraTheme.colors.layer.gray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            option.options.forEach { word ->
                OptionRow(
                    word = word,
                    isSelected = option.selectedOption == word,
                    isEnabled = isSelectionEnabled,
                    onClick = { onOptionClick(word) }
                )
            }
        }
    }
}

@Composable
private fun OptionRow(
    word: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        PeraTheme.colors.layer.grayLighter
    } else {
        PeraTheme.colors.background.primary
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickableNoRipple(enabled = isEnabled, onClick = onClick)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp),
        text = word,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.main,
        textAlign = TextAlign.Center
    )
}
