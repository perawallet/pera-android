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

package com.algorand.android.ui.compose.widget.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.ColorPalette
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
fun FavoriteIcon(modifier: Modifier, isFavorite: Boolean?) {
    val (iconRes, tintColor) = when (isFavorite) {
        true -> R.drawable.ic_favorite_enabled to ColorPalette.Yellow.V500
        false -> R.drawable.ic_favorite_disabled to PeraTheme.colors.text.main
        null -> R.drawable.ic_favorite_disabled to PeraTheme.colors.text.grayLighter
    }
    Icon(
        modifier = modifier,
        painter = painterResource(iconRes),
        tint = tintColor,
        contentDescription = null
    )
}
