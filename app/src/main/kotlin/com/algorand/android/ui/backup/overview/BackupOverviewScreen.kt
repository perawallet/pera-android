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

package com.algorand.android.ui.backup.overview

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewEvent
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton

@Composable
fun BackupOverviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: BackupOverviewViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                ViewEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    BackupOverviewContent(
        backupId = viewState.backupId,
        onDisableClick = viewModel::disableBackup
    )
}

@Composable
private fun BackupOverviewContent(
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
