/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraTitleText

@SuppressWarnings("LongMethod")
@Composable
fun GroupChoiceWidget(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    iconContentDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PeraTheme.colors.layer.grayLighter)
                .padding(8.dp),
            imageVector = icon,
            contentDescription = iconContentDescription,
            tint = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            PeraTitleText(
                text = title
            )
            Spacer(modifier = Modifier.height(4.dp))
            PeraBodyText(
                text = description
            )
        }
    }
}

@PreviewLightDark
@Composable
fun GroupChoiceWidgetPreview() {
    GroupChoiceWidget(
        title = stringResource(id = R.string.import_an_account),
        description = stringResource(id = R.string.import_an_existing),
        iconContentDescription = stringResource(id = R.string.import_an_existing),
        icon = ImageVector.vectorResource(R.drawable.ic_key),
        onClick = { },
    )
}
