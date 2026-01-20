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

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.algorand.android.models.AccountIconResource
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

/**
 * A composable that displays a contact icon.
 * Shows the contact's profile picture if imageUri is provided,
 * otherwise shows a placeholder icon.
 *
 * @param modifier Modifier for the composable
 * @param imageUri Optional URI of the contact's profile picture
 * @param size Size of the icon (default 40.dp)
 * @param backgroundColor Background color for the placeholder icon
 * @param iconTint Tint color for the placeholder icon
 * @param contentDescription Accessibility description for screen readers
 */
@OptIn(ExperimentalGlideComposeApi::class)
@PeraPreviewLightDark
@Composable
fun ContactIcon(
    modifier: Modifier = Modifier,
    imageUri: Uri? = null,
    size: Dp = 40.dp,
    backgroundColor: Color = colorResource(AccountIconResource.CONTACT.backgroundColorResId),
    iconTint: Color = colorResource(AccountIconResource.CONTACT.iconTintResId),
    contentDescription: String? = null
) {
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    val semanticsModifier = if (contentDescription != null) {
        modifier.semantics { this.contentDescription = contentDescription }
    } else {
        modifier
    }

    if (imageUri != null) {
        Box(
            modifier = semanticsModifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            ContactIconPlaceholder(
                size = size,
                backgroundColor = backgroundColor
            )
            GlideImage(
                model = imageUri,
                contentDescription = null, // contentDescription handled by parent Box semantics
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            ) {
                it.override(sizePx)
                    .centerCrop()
            }
        }
    } else {
        ContactIconPlaceholder(
            modifier = semanticsModifier,
            size = size,
            iconTint = iconTint,
            backgroundColor = backgroundColor,
            contentDescription = contentDescription
        )
    }
}

private const val ICON_PADDING_RATIO = 5

@PeraPreviewLightDark
@Composable
private fun ContactIconPlaceholder(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    backgroundColor: Color = colorResource(AccountIconResource.CONTACT.backgroundColorResId),
    iconTint: Color = colorResource(AccountIconResource.CONTACT.iconTintResId),
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(AccountIconResource.CONTACT.iconResId),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier
                .fillMaxSize()
                .padding(size / ICON_PADDING_RATIO)
        )
    }
}
