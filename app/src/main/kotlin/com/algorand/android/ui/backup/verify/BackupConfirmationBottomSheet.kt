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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.backup.verify.model.BackupConfirmationResult
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraCheckbox
import com.algorand.android.ui.compose.widget.bottomsheet.PeraModalBottomSheet
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupConfirmationBottomSheet(
    sheetState: SheetState,
    encryptionKey: String,
    onCopyEncryptionKeyClick: () -> Unit,
    onResult: (BackupConfirmationResult) -> Unit
) {
    var isEncryptionKeyStored by remember { mutableStateOf(false) }

    PeraModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onResult(BackupConfirmationResult.Dismiss) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoBadge()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.have_you_stored_encryption_key),
                style = PeraTheme.typography.title.small.sansMedium,
                color = PeraTheme.colors.text.main,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.encryption_key),
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                EncryptionKeyRow(value = encryptionKey, onCopyClick = onCopyEncryptionKeyClick)
            }

            Spacer(modifier = Modifier.height(20.dp))

            ConfirmationCheckbox(
                isChecked = isEncryptionKeyStored,
                onToggle = { isEncryptionKeyStored = !isEncryptionKeyStored }
            )

            Spacer(modifier = Modifier.height(20.dp))

            PeraPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onResult(BackupConfirmationResult.EnableCloudBackup) },
                text = stringResource(R.string.enable_cloud_backup_now),
                state = if (isEncryptionKeyStored) PeraButtonState.ENABLED else PeraButtonState.DISABLED
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableNoRipple(onClick = { onResult(BackupConfirmationResult.ShowCredentialsAgain) })
                    .padding(vertical = 12.dp),
                text = stringResource(R.string.show_my_credentials_again),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoBadge() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .border(
                width = 2.dp,
                color = PeraTheme.colors.helper.positive,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(36.dp),
            painter = painterResource(R.drawable.ic_info),
            tint = PeraTheme.colors.helper.positive,
            contentDescription = null
        )
    }
}

@Composable
private fun EncryptionKeyRow(value: String, onCopyClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.layer.grayLightest,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            modifier = Modifier
                .size(20.dp)
                .clickableNoRipple(onClick = onCopyClick),
            painter = painterResource(R.drawable.ic_copy),
            tint = PeraTheme.colors.helper.positive,
            contentDescription = null
        )
    }
}

@Composable
private fun ConfirmationCheckbox(isChecked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = PeraTheme.colors.layer.gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickableNoRipple(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PeraCheckbox(
            checked = isChecked,
            onCheckChanged = { onToggle() }
        )
        Text(
            text = stringResource(R.string.yes_i_stored_my_encryption_key),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
    }
}
