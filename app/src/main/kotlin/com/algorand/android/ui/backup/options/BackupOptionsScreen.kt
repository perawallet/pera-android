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

package com.algorand.android.ui.backup.options

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.icon.PeraIcon

@Composable
fun BackupOptionsScreen(
    onSetUpBackupClick: () -> Unit,
    onRestoreBackupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(R.string.cloud_backup),
            style = PeraTheme.typography.title.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(R.string.cloud_backup_choose_option),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )

        Spacer(modifier = Modifier.height(40.dp))

        BackupOptionCard(
            iconRes = R.drawable.ic_cloud_upload,
            title = stringResource(R.string.set_up_new_cloud_backup),
            description = stringResource(R.string.set_up_new_cloud_backup_description),
            onClick = onSetUpBackupClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        BackupOptionCard(
            iconRes = R.drawable.ic_cloud_download,
            title = stringResource(R.string.restore_retrieve_backup),
            description = stringResource(R.string.restore_retrieve_backup_description),
            onClick = onRestoreBackupClick
        )
    }
}

@Composable
private fun BackupOptionCard(
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color = PeraTheme.colors.layer.grayLighter)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        PeraIcon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            tintColor = PeraTheme.colors.text.main
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = PeraTheme.typography.body.large.sansMedium,
                color = PeraTheme.colors.text.main
            )
            Text(
                text = description,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
    }
}
