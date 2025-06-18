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

package com.algorand.android.ui.menu.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.menu.viewmodel.MenuNftViewModel
import com.algorand.android.ui.menu.viewmodel.MenuNftViewModel.ViewState.Content
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
internal fun MenuListNftItem(
    viewModel: MenuNftViewModel,
    onClick: () -> Unit
) {
    MenuListItemContainer(
        modifier = Modifier.clickable { onClick() }
    ) {
        val imageUrls = (viewModel.state.collectAsStateWithLifecycle().value as? Content)?.urls.orEmpty()
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuListItemIcon(resId = R.drawable.ic_collectibles)
            MenuListItemTitleText(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.nfts)
            )
            CollectibleImagePreviews(imageUrls)
            MenuListItemTrailingIcon()
        }
        LaunchedEffect(Unit) {
            viewModel.init()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CollectibleImagePreviews(urls: List<String>) {
    Box {
        urls.forEachIndexed { index, url ->
            GlideImage(
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = (24 * index).dp)
                    .border(width = 2.dp, color = PeraTheme.colors.layer.grayLighter, shape = RoundedCornerShape(8.dp)),
                model = url,
                contentDescription = null
            )
        }
    }
}
