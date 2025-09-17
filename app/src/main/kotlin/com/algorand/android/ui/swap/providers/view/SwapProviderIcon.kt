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

package com.algorand.android.ui.swap.providers.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

private const val PADDING_SIZE_RATIO = 5f

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SwapProviderIcon(modifier: Modifier = Modifier, url: String?) {
    GlideImage(
        modifier = modifier,
        model = url,
        contentDescription = null,
        loading = placeholder { PlaceholderIcon() },
        failure = placeholder { PlaceholderIcon() }
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PlaceholderIcon() {
    BoxWithConstraints {
        val padding = maxWidth / PADDING_SIZE_RATIO
        Icon(
            modifier = Modifier
                .background(color = PeraTheme.colors.layer.grayLighter, shape = CircleShape)
                .padding(padding)
                .fillMaxSize(),
            painter = painterResource(R.drawable.ic_provider_placeholder),
            contentDescription = null,
            tint = PeraTheme.colors.text.grayLighter
        )
    }
}
