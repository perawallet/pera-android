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

package com.algorand.android.ui.swap.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.swap.viewmodel.SwapViewModel

@Composable
fun SwapToolbar(
    swapViewModel: SwapViewModel,
    listener: SwapToolbarListener
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 60.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.swap),
            style = PeraTheme.typography.body.large.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Icon(
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
                .clickable { listener.onInfoIconClick() },
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null,
            tint = PeraTheme.colors.text.gray
        )
        Spacer(modifier = Modifier.weight(1f))
        val viewState = swapViewModel.state.collectAsStateWithLifecycle()
        if (viewState.value is SwapViewModel.ViewState.Content) {
            SelectedAccountChip(
                modifier = Modifier.clickable { listener.onAccountChipClick() },
                content = viewState.value as SwapViewModel.ViewState.Content
            )
        }
    }
}

@Composable
private fun SelectedAccountChip(
    modifier: Modifier,
    content: SwapViewModel.ViewState.Content
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = PeraTheme.colors.button.strokeColor,
                shape = RoundedCornerShape(60.dp)
            )
            .padding(start = 4.dp, top = 6.dp, bottom = 6.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AccountIcon(
            modifier = Modifier.size(28.dp),
            iconDrawablePreview = content.accountIconDrawable
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = content.accountDisplayName.primaryDisplayName,
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.text.gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.ic_arrow_down),
            tint = PeraTheme.colors.text.gray,
            contentDescription = null
        )
    }
}

interface SwapToolbarListener {
    fun onAccountChipClick()
    fun onInfoIconClick()
}
