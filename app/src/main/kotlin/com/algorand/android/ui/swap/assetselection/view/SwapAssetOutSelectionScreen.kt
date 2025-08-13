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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.android.ui.compose.widget.asset.PeraAssetList
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import com.algorand.android.ui.swap.assetselection.viewmodel.SwapAssetOutSelectionViewModel

@Composable
fun SwapAssetOutSelectionScreen(
    viewModel: SwapAssetOutSelectionViewModel,
    listener: SwapAssetOutSelectionScreenListener
) {
    SwapAssetSelectionScreen(
        title = stringResource(R.string.swap_to),
        onBackClick = listener::onBackButtonClick,
        onQueryUpdated = viewModel::updateQuery,
    ) {
        val viewState = viewModel.state.collectAsStateWithLifecycle().value

        when (viewState) {
            is SwapAssetOutSelectionViewModel.ViewState.Idle -> Unit
            SwapAssetOutSelectionViewModel.ViewState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PeraCircularProgressIndicator()
                }
            }
            is SwapAssetOutSelectionViewModel.ViewState.Content -> {
                PeraAssetList(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    assetList = viewState.assetList,
                    onAssetClick = listener::onAssetClick
                )
            }
        }
    }
}

interface SwapAssetOutSelectionScreenListener {
    fun onAssetClick(assetListItem: AssetListItem)
    fun onBackButtonClick()
}
