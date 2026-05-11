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

package com.algorand.android.ui.backup.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.backup.list.model.BackupListTab
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@Composable
internal fun BackupTabSelector(
    selected: BackupListTab,
    onTabSelected: (BackupListTab) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        BackupTabPill(
            label = stringResource(R.string.synced),
            isSelected = selected == BackupListTab.SYNCED,
            onClick = { onTabSelected(BackupListTab.SYNCED) }
        )
        BackupTabPill(
            label = stringResource(R.string.not_synced),
            isSelected = selected == BackupListTab.NOT_SYNCED,
            onClick = { onTabSelected(BackupListTab.NOT_SYNCED) }
        )
    }
}

@Composable
private fun BackupTabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val backgroundColor = if (isSelected) PeraTheme.colors.layer.grayLighter else PeraTheme.colors.background.primary
    val textColor = if (isSelected) PeraTheme.colors.text.main else PeraTheme.colors.text.gray

    Row(
        modifier = Modifier
            .clip(shape)
            .background(color = backgroundColor, shape = shape)
            .border(
                width = 1.dp,
                color = PeraTheme.colors.layer.gray,
                shape = shape
            )
            .clickableNoRipple(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = PeraTheme.typography.footnote.sansMedium,
            color = textColor
        )
    }
}

@Composable
internal fun BackupCountHeader(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.main
    )
}
