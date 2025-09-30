@file:Suppress("LongParameterList")
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel
import com.algorand.android.ui.swap.topfive.viewmodel.TopSwapPairsViewModel
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapButtonViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapConfigurationViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapProviderWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val ASSET_IN_CONTAINER_ID = "assetInContainer"
private const val ASSET_OUT_CONTAINER_ID = "assetOutContainer"
private const val SWAP_CONFIGURATION_CONTAINER_ID = "swapConfigurationContainer"
private const val MAX_PERCENTAGE = 100

@Composable
fun SwapWidget(
    modifier: Modifier = Modifier,
    swapViewModel: SwapViewModel,
    assetInViewModel: SwapAssetSelectionViewModel,
    assetOutViewModel: SwapAssetSelectionViewModel,
    widgetViewModel: SwapWidgetViewModel,
    providerWidgetViewModel: SwapProviderWidgetViewModel,
    buttonViewModel: SwapButtonViewModel,
    configViewModel: SwapConfigurationViewModel,
    topSwapPairsViewModel: TopSwapPairsViewModel,
    swapPairHistoryViewModel: SwapPairHistoryViewModel,
    listener: SwapWidgetListener,
    scope: CoroutineScope,
    onConfigureClick: () -> Unit
) {
    ConstraintLayout(modifier = modifier, constraintSet = createConstraints()) {
        AssetInContainer(scope, swapViewModel, widgetViewModel, assetInViewModel, listener)
        AssetOutContainer(scope, swapViewModel, widgetViewModel, assetOutViewModel, listener)
        SwapConfigurationContainer(
            configViewModel,
            swapViewModel::switchAssets,
            onConfigureClick,
            onMaxClick = { widgetViewModel.setAmountByPercentage(swapViewModel.getSwapDetails(), MAX_PERCENTAGE) }
        )

        LaunchedEffect(Unit) {
            assetInViewModel.initAssetDetail(swapViewModel.addressFlow, swapViewModel.assetInFlow)
            assetOutViewModel.initAssetDetail(swapViewModel.addressFlow, swapViewModel.assetOutFlow)
            configViewModel.initViewState(swapViewModel.addressFlow, assetInViewModel.state, assetOutViewModel.state)
            buttonViewModel.init(widgetViewModel.state)
            providerWidgetViewModel.init(widgetViewModel.state)
            topSwapPairsViewModel.init(widgetViewModel.state)
            swapPairHistoryViewModel.init(swapViewModel.addressFlow, widgetViewModel.state)
            widgetViewModel.initWidget(swapViewModel.swapDetailsFlow, assetInViewModel.state, assetOutViewModel.state)
        }
    }
}

@Composable
private fun AssetInContainer(
    scope: CoroutineScope,
    swapViewModel: SwapViewModel,
    widgetViewModel: SwapWidgetViewModel,
    assetInViewModel: SwapAssetSelectionViewModel,
    listener: SwapWidgetListener
) {
    Box(
        modifier = Modifier
            .layoutId(ASSET_IN_CONTAINER_ID)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 40.dp)
    ) {
        SwapAssetInWidget(swapViewModel, widgetViewModel, assetInViewModel) {
            scope.launch { swapViewModel.logAssetInSelectionClick() }
            listener.onAssetInChipClick()
        }
    }
}

@Composable
private fun AssetOutContainer(
    scope: CoroutineScope,
    swapViewModel: SwapViewModel,
    widgetViewModel: SwapWidgetViewModel,
    assetOutViewModel: SwapAssetSelectionViewModel,
    listener: SwapWidgetListener
) {
    Box(
        modifier = Modifier
            .layoutId(ASSET_OUT_CONTAINER_ID)
            .background(color = PeraTheme.colors.layer.grayLighter, shape = RoundedCornerShape(12.dp))
            .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 24.dp)
    ) {
        SwapAssetOutWidget(widgetViewModel, assetOutViewModel) {
            scope.launch { swapViewModel.logAssetOutSelectionClick() }
            listener.onAssetOutChipClick()
        }
    }
}

@Composable
private fun SwapConfigurationContainer(
    configurationViewModel: SwapConfigurationViewModel,
    onSwitchAssetsClick: () -> Unit,
    onConfigureClick: () -> Unit,
    onMaxClick: () -> Unit
) {
    Box(modifier = Modifier.layoutId(SWAP_CONFIGURATION_CONTAINER_ID)) {
        SwapConfigurationWidget(
            modifier = Modifier.padding(horizontal = 16.dp),
            configurationViewModel,
            onSwitchAssetsClick,
            onConfigureClick,
            onMaxClick
        )
    }
}

private fun createConstraints(): ConstraintSet {
    return ConstraintSet {
        val assetInContainer = createRefFor(ASSET_IN_CONTAINER_ID)
        val assetOutContainer = createRefFor(ASSET_OUT_CONTAINER_ID)
        val swapConfigurationContainer = createRefFor(SWAP_CONFIGURATION_CONTAINER_ID)

        constrain(assetInContainer) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
        }

        constrain(assetOutContainer) {
            start.linkTo(parent.start)
            top.linkTo(assetInContainer.bottom)
            end.linkTo(parent.end)
        }

        constrain(swapConfigurationContainer) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(assetInContainer.bottom)
            bottom.linkTo(assetOutContainer.top)
        }
    }
}

interface SwapWidgetListener {
    fun onAssetInChipClick()
    fun onAssetOutChipClick()
}
