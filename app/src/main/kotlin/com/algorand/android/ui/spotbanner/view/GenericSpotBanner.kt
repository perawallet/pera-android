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

package com.algorand.android.ui.spotbanner.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.wallet.spotbanner.domain.model.SpotBanner
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun GenericSpotBanner(
    modifier: Modifier,
    banner: SpotBanner.Generic,
    onDismissClick: (SpotBanner.Generic) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        if (!banner.image.isNullOrBlank()) {
            GlideImage(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(48.dp),
                model = banner.image,
                contentDescription = null,
            )
        }
        SpotBannerText(
            modifier = Modifier.padding(vertical = 12.dp),
            text = banner.text
        )
        Icon(
            modifier = Modifier
                .size(36.dp)
                .clickable(remember { MutableInteractionSource() }, indication = null) { onDismissClick(banner) }
                .padding(8.dp)
                .background(PeraTheme.colors.layer.grayLighter, shape = CircleShape)
                .padding(2.dp)
                .align(Alignment.Top),
            painter = painterResource(R.drawable.ic_close),
            tint = PeraTheme.colors.text.gray,
            contentDescription = null
        )
    }
}
