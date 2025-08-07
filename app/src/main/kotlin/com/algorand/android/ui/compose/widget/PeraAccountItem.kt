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

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.utils.AccountIconDrawable

@Composable
fun PeraAccountItem(
    modifier: Modifier = Modifier,
    iconDrawablePreview: AccountIconDrawablePreview,
    displayName: AccountDisplayName,
    onCopyAddress: (String) -> Unit,
) {
    val longClickModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onLongPress = {
                onCopyAddress(displayName.primaryDisplayName)
            }
        )
    }
    Row(
        modifier = modifier.then(longClickModifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccountIcon(
            modifier = Modifier.size(40.dp),
            iconDrawablePreview
        )
        Spacer(modifier = Modifier.width(16.dp))
        DisplayName(displayName)
    }
}

@Composable
fun AccountIcon(
    modifier: Modifier,
    iconDrawablePreview: AccountIconDrawablePreview
) {
    Image(
        modifier = modifier,
        bitmap = AccountIconDrawable.create(
            context = LocalContext.current,
            accountIconDrawablePreview = iconDrawablePreview,
            sizeResId = R.dimen.spacing_xxxxlarge
        ).toBitmap().asImageBitmap(),
        contentDescription = null
    )
}

@Composable
private fun RowScope.DisplayName(displayName: AccountDisplayName) {
    with(displayName) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = primaryDisplayName,
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            if (secondaryDisplayName != null && primaryDisplayName != secondaryDisplayName) {
                Text(
                    text = secondaryDisplayName,
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.grayLighter
                )
            }
        }
    }
}
