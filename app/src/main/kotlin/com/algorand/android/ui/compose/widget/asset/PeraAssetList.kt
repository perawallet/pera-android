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

package com.algorand.android.ui.compose.widget.asset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
fun PeraAssetList(
    modifier: Modifier = Modifier,
    assetList: List<AssetListItem>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onAssetClick: ((AssetListItem) -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(assetList.size) { index ->
            assetList.getOrNull(index)?.let { assetListItem ->
                PeraAssetListItem(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = { onAssetClick?.invoke(assetListItem) }
                        ),
                    item = assetListItem
                )
                if (index in 0 until assetList.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        thickness = 1.dp,
                        color = PeraTheme.colors.layer.grayLighter
                    )
                }
            }
        }
    }
}
