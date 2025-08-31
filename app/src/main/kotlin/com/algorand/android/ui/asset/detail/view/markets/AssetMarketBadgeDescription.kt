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

package com.algorand.android.ui.asset.detail.view.markets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@Composable
fun AssetMarketBadgeDescription(description: AssetMarketsDetail.BadgeDescription, onLearnMoreClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .background(colorResource(description.backgroundColorResId), shape = RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(description.drawableResId),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(description.titleTextResId),
                    color = colorResource(description.textColorResId),
                    style = PeraTheme.typography.body.regular.sansMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(description.descriptionTextResId),
                    color = colorResource(description.textColorResId),
                    style = PeraTheme.typography.body.regular.sans
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.height(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_pera),
                tint = PeraTheme.colors.helper.positive,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                modifier = Modifier.clickableNoRipple { onLearnMoreClick() },
                text = stringResource(R.string.learn_more_about_asa_verification),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.helper.positive
            )
        }
    }
}
