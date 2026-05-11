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

package com.algorand.android.ui.backup.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

private const val MNEMONIC_COLUMN_SIZE = 6

@Composable
fun BackupSectionLabel(@StringRes textRes: Int) {
    Text(
        text = stringResource(textRes),
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
fun BackupMnemonicGrid(mnemonic: String) {
    val words = mnemonic.split(" ")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLightest, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        MnemonicColumn(
            modifier = Modifier.weight(1f),
            words = words.take(MNEMONIC_COLUMN_SIZE),
            startIndex = 1
        )
        MnemonicColumn(
            modifier = Modifier.weight(1f),
            words = words.drop(MNEMONIC_COLUMN_SIZE).take(MNEMONIC_COLUMN_SIZE),
            startIndex = MNEMONIC_COLUMN_SIZE + 1
        )
    }
}

@Composable
private fun MnemonicColumn(
    modifier: Modifier = Modifier,
    words: List<String>,
    startIndex: Int
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        words.forEachIndexed { index, word ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.width(20.dp),
                    text = (startIndex + index).toString(),
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.gray,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = word,
                    style = PeraTheme.typography.body.regular.sans,
                    color = PeraTheme.colors.text.main
                )
            }
        }
    }
}

@Composable
fun CopyToClipboardButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoRipple(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_copy),
            tint = PeraTheme.colors.helper.positive,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.copy_to_clipboard),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.helper.positive
        )
    }
}

@Composable
fun BackupEncryptionKeyField(value: String, onCopyClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLightest, shape = RoundedCornerShape(8.dp))
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
