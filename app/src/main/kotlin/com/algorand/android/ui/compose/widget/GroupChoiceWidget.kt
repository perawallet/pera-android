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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import java.util.Locale

@Composable
fun GroupChoiceWidget(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    iconContentDescription: String,
    badge: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .background(
                color = PeraTheme.colors.layer.grayLighter,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = iconContentDescription,
            tint = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = PeraTheme.typography.body.large.sansMedium,
                    color = PeraTheme.colors.text.main,
                    text = title
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    badge()
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray,
                text = description
            )
        }
    }
}

@Composable
fun GroupChoiceNewBadge() {
    Text(
        modifier = Modifier
            .background(
                color = PeraTheme.colors.wallet.wallet4.background,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        text = stringResource(R.string.new_text).uppercase(Locale.ENGLISH),
        style = PeraTheme.typography.caption.sansMedium,
        color = PeraTheme.colors.wallet.wallet4.icon
    )
}

@PeraPreviewLightDark
@Composable
private fun GroupChoiceWidgetPreview() {
    GroupChoiceWidget(
        title = stringResource(id = R.string.import_an_account),
        description = stringResource(id = R.string.import_an_existing),
        iconContentDescription = stringResource(id = R.string.import_an_existing),
        icon = ImageVector.vectorResource(R.drawable.ic_key),
        badge = { GroupChoiceNewBadge() },
        onClick = {},
    )
}
