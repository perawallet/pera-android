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

package com.algorand.android.ui.backup.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.algorand.android.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.ui.backup.viewmodel.BackupViewModel
import com.algorand.android.ui.backup.viewmodel.BackupViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton

@Composable
fun BackupScreen(
    viewModel: BackupViewModel = hiltViewModel()
) {
    when (val viewState = viewModel.state.collectAsStateWithLifecycle().value) {
        is ViewState.Loading -> Unit
        is ViewState.Selection -> BackupSelectionContent(
            onCreateClick = viewModel::createBackup,
            onRestoreClick = viewModel::restoreBackup
        )
        is ViewState.Active -> BackupActiveContent(
            backupId = viewState.backupId,
            onDisableClick = viewModel::disableBackup
        )
        is ViewState.Create -> CreateBackupScreen(
            onBackClick = viewModel::navigateBack
        )
        is ViewState.Restore -> RestoreBackupScreen(
            onBackClick = viewModel::navigateBack
        )
    }
}

@Composable
private fun BackupSelectionContent(
    onCreateClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.cloud_backup),
            style = PeraTheme.typography.title.regular.sansBold,
            color = PeraTheme.colors.text.main
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.cloud_backup_description),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        PeraPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCreateClick,
            text = stringResource(R.string.create_new_backup)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PeraSecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRestoreClick,
            text = stringResource(R.string.restore_existing_backup)
        )
    }
}

@Composable
private fun BackupActiveContent(
    backupId: String,
    onDisableClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.cloud_backup),
            style = PeraTheme.typography.title.regular.sansBold,
            color = PeraTheme.colors.text.main
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.cloud_backup_active_description),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = PeraTheme.colors.layer.grayLightest,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.backup_id),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = backupId,
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PeraSecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDisableClick,
            text = stringResource(R.string.disable_backup)
        )
    }
}
