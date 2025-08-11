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

package com.algorand.android.ui.swap.assetselection.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.textfield.PeraSlimTextField

@Composable
fun SwapAssetSelectionScreen(
    title: String,
    onBackClick: () -> Unit,
    onQueryUpdated: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        PeraToolbar(
            text = title,
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onBackClick)
                )
            }
        )

        var searchKeyword by remember { mutableStateOf("") }

        LaunchedEffect(searchKeyword) {
            onQueryUpdated(searchKeyword)
        }

        PeraSlimTextField(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .background(color = PeraTheme.colors.layer.grayLighter, RoundedCornerShape(4.dp)),
            text = searchKeyword,
            hint = stringResource(R.string.search_assets_id),
            onTextChanged = { searchKeyword = it },
            startIconContainer = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_search),
                    tint = PeraTheme.colors.text.gray,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            },
            endIconContainer = {
                if (searchKeyword.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickableNoRipple { searchKeyword = "" },
                        painter = painterResource(R.drawable.ic_close),
                        tint = PeraTheme.colors.text.gray,
                        contentDescription = null
                    )
                }
            }
        )
        content()
    }
}

