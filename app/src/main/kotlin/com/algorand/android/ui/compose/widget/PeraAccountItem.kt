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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    displayConfig: AccountItemDisplayConfig = AccountItemDisplayConfig(),
    canCopyable: Boolean = true,
    onCopyAddress: (String) -> Unit = {},
    onAccountClick: (String) -> Unit = {}
) {
    PeraAccountItem(
        modifier = modifier,
        displayName = displayName,
        displayConfig = displayConfig,
        canCopyable = canCopyable,
        onCopyAddress = onCopyAddress,
        onAccountClick = onAccountClick,
        iconContent = {
            AccountIcon(
                modifier = Modifier.size(40.dp),
                iconDrawablePreview = iconDrawablePreview
            )
        }
    )
}

@Composable
fun PeraAccountItem(
    modifier: Modifier = Modifier,
    displayName: AccountDisplayName,
    displayConfig: AccountItemDisplayConfig = AccountItemDisplayConfig(),
    canCopyable: Boolean = true,
    onCopyAddress: (String) -> Unit = {},
    onAccountClick: (String) -> Unit = {},
    iconContent: @Composable () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val longClickModifier = if (canCopyable) {
        Modifier.pointerInput(onCopyAddress, onAccountClick, displayName.accountAddress) {
            detectTapGestures(
                onLongPress = { onCopyAddress(displayName.accountAddress) },
                onTap = { onAccountClick(displayName.accountAddress) }
            )
        }
    } else {
        Modifier.clickable { onAccountClick(displayName.accountAddress) }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(longClickModifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
            if (displayConfig.startSmallIconResId != null) {
                Icon(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd),
                    painter = painterResource(displayConfig.startSmallIconResId),
                    contentDescription = displayConfig.startSmallIconContentDescription,
                    tint = PeraTheme.colors.text.main
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        DisplayName(
            displayName = displayName,
            displayConfig = displayConfig,
            trailingContent = trailingContent
        )
    }
}

@Composable
fun AccountIcon(
    modifier: Modifier,
    iconDrawablePreview: AccountIconDrawablePreview,
    contentDescription: String? = null
) {
    Image(
        modifier = modifier,
        bitmap = AccountIconDrawable.create(
            context = LocalContext.current,
            accountIconDrawablePreview = iconDrawablePreview,
            sizeResId = R.dimen.spacing_xxxxlarge
        ).toBitmap().asImageBitmap(),
        contentDescription = contentDescription
    )
}

@Composable
private fun RowScope.DisplayName(
    displayName: AccountDisplayName,
    displayConfig: AccountItemDisplayConfig,
    trailingContent: (@Composable () -> Unit)? = null
) {
    with(displayName) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            if (displayConfig.primaryValueText != null || displayConfig.secondaryValueText != null) {
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    if (displayConfig.primaryValueText != null) {
                        Text(
                            text = displayConfig.primaryValueText,
                            style = PeraTheme.typography.body.regular.sans,
                            color = PeraTheme.colors.text.main
                        )
                    }
                    if (displayConfig.secondaryValueText != null) {
                        Text(
                            text = displayConfig.secondaryValueText,
                            style = PeraTheme.typography.footnote.sans,
                            color = PeraTheme.colors.text.gray
                        )
                    }
                }
            }
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(16.dp))
                trailingContent()
            }
        }
    }
}
