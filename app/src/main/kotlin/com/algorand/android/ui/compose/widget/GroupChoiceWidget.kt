/*
 * Copyright 2022 Pera Wallet, LDA
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraTitleText

@SuppressWarnings("LongMethod")
@Composable
fun ItemChoiceWidget(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    iconContentDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.then(Modifier
            .padding(horizontal = 16.dp)
            .requiredHeight(72.dp)
            .clickable {
                onClick()
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(40.dp)
                    .clip(shape = CircleShape)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center),
                    imageVector = icon,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = iconContentDescription
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            PeraTitleText(
                modifier = Modifier
                    .requiredHeight(height = 24.dp),
                text = title
            )
            PeraBodyText(
                modifier = Modifier
                    .requiredHeight(height = 48.dp),
                text = description
            )
        }
    }
}

@Preview
@Composable
fun GroupChoiceWidgetPreview() {
    ItemChoiceWidget(
        title = stringResource(id = R.string.import_an_account),
        description = stringResource(id = R.string.import_an_existing),
        iconContentDescription = stringResource(id = R.string.import_an_existing),
        icon = ImageVector.vectorResource(R.drawable.ic_key),
        onClick = { },
    )
}
