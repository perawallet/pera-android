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

package com.algorand.android.ui.swap.accountselection.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraAccountItem
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.swap.accountselection.viewmodel.SwapAddressSelectionViewModel
import com.algorand.android.ui.swap.accountselection.viewmodel.SwapAddressSelectionViewModel.ViewState

@Composable
fun SwapAddressSelectionScreen(
    viewModel: SwapAddressSelectionViewModel = hiltViewModel(),
    onAddressClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        val viewState = viewModel.state.collectAsStateWithLifecycle().value
        PeraToolbar(
            text = "",
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onBackClick)
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(R.string.select_account),
            style = PeraTheme.typography.title.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(R.string.select_an_account_to_make),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (viewState is ViewState.Content) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
            ) {
                items(viewState.addresses) { (displayName, iconDrawable) ->
                    PeraAccountItem(
                        iconDrawablePreview = iconDrawable,
                        displayName = displayName,
                        onAccountClick = onAddressClick
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            viewModel.initViewState()
        }
    }
}
