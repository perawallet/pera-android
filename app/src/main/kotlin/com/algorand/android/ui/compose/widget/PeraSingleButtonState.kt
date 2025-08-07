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

package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton

@Composable
fun PeraSingleButtonState(
    modifier: Modifier = Modifier,
    iconResId: Int?,
    titleResId: Int?,
    descriptionResId: Int?,
    buttonTextResId: Int?,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        iconResId?.let { iconId ->
            Icon(
                modifier = Modifier.size(96.dp),
                painter = painterResource(iconId),
                contentDescription = null
            )
        }
        titleResId?.let { titleId ->
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(titleId),
                style = PeraTheme.typography.body.large.sansMedium,
                color = PeraTheme.colors.text.main,
                textAlign = TextAlign.Center
            )
        }
        descriptionResId?.let { descriptionId ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(descriptionId),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.gray,
                textAlign = TextAlign.Center
            )
        }
        buttonTextResId?.let { buttonTextId ->
            Spacer(modifier = Modifier.height(32.dp))
            PeraPrimaryButton(
                text = stringResource(buttonTextId),
                onClick = { onClick?.invoke() }
            )
        }
    }
}
