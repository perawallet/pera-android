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

package com.algorand.android.ui.swap.widget.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.swap.widget.viewmodel.SwapConfigurationViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapConfigurationViewModel.ViewState.Content
import com.algorand.android.ui.swap.widget.viewmodel.SwapConfigurationViewModel.ViewState.Idle

@Composable
fun SwapConfigurationWidget(
    modifier: Modifier = Modifier,
    viewModel: SwapConfigurationViewModel
) {
    Row(modifier) {
        val viewState = viewModel.state.collectAsStateWithLifecycle()
        when (val state = viewState.value) {
            Idle -> Unit
            is Content -> ContentState(state)
        }
    }
}

@Composable
private fun RowScope.ContentState(content: Content) {
    val chipModifier = Modifier
        .background(color = PeraTheme.colors.background.primary, shape = RoundedCornerShape(40.dp))
        .border(
            width = 1.dp,
            color = PeraTheme.colors.button.strokeColor,
            shape = RoundedCornerShape(40.dp)
        )
        .padding(vertical = 8.dp, horizontal = 12.dp)
    SwitchChipButton(chipModifier, content.isSwitchButtonEnabled)
    Spacer(modifier = Modifier.weight(1f))
    ConfigChip(chipModifier)
}

@Composable
private fun SwitchChipButton(modifier: Modifier, isEnabled: Boolean) {
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_switch),
        contentDescription = null,
        tint = PeraTheme.colors.text.main
    )
}

@Composable
private fun ConfigChip(modifier: Modifier) {
    Row(modifier = modifier) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(R.drawable.ic_customize),
            contentDescription = null,
            tint = PeraTheme.colors.helper.positive
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .background(color = PeraTheme.colors.layer.grayLighter)
        )
        Text(
            text = stringResource(R.string.max),
            color = PeraTheme.colors.helper.positive,
            style = PeraTheme.typography.caption.sansBold,
        )
    }
}
