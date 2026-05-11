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

package com.algorand.android.ui.backup.restore.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.restore.pin.RestoreBackupPinViewModel.ViewEvent
import com.algorand.android.ui.backup.restore.pin.RestoreBackupPinViewModel.ViewState.ContentState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.pin.DialPadContainer
import com.algorand.android.ui.compose.widget.pin.SixDigitPasswordContainer
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator

@Composable
fun RestoreBackupPinScreen(
    viewModel: RestoreBackupPinViewModel,
    onBackupRestored: () -> Unit,
    onShowError: (Int) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                ViewEvent.BackupRestored -> onBackupRestored()
                is ViewEvent.ShowError -> onShowError(event.messageResId)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.restore_backup_pin_title),
                    style = PeraTheme.typography.title.regular.sansMedium,
                    color = PeraTheme.colors.text.main
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.restore_backup_pin_description),
                    style = PeraTheme.typography.body.regular.sans,
                    color = PeraTheme.colors.text.gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            SixDigitPasswordContainer(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enteredDigitCount = state.enteredDigits.length
            )
            Spacer(modifier = Modifier.weight(1f))
            DialPadContainer(
                modifier = Modifier.padding(horizontal = 40.dp),
                onDigitClick = viewModel::appendDigit,
                onBackspaceClick = viewModel::removeLastDigit
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        if (state.contentState == ContentState.Loading) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun LoadingOverlay() {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = PeraTheme.colors.background.backdropModal)
            .clickable(interactionSource = interactionSource, indication = null) {},
        contentAlignment = Alignment.Center
    ) {
        PeraCircularProgressIndicator(modifier = Modifier.size(48.dp))
    }
}
