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

package com.algorand.android.ui.asset.detail.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.asset.detail.view.holdings.AssetHoldingScreen
import com.algorand.android.ui.asset.detail.view.holdings.AssetHoldingScreenListener
import com.algorand.android.ui.asset.detail.view.markets.AssetMarketsScreen
import com.algorand.android.ui.asset.detail.view.markets.AssetMarketsScreenListener
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewEvent.DisplayFailedToToggleFavoriteError
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewEvent.DisplayFailedToTogglePriceAlertError
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewState.Content
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewState.Idle
import com.algorand.android.ui.asset.detail.viewmodel.AssetHoldingViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetLineChartViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetMarketsViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetPriceLineChartViewModel
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.PeraSingleButtonState
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import com.algorand.android.ui.transaction.csv.viewmodel.CsvViewModel
import com.algorand.android.ui.transaction.history.viewmodel.TransactionHistoryViewModel
import kotlinx.coroutines.launch

private const val HOLDINGS_PAGE = 0
private const val MARKETS_PAGE = 1

@Composable
fun AssetDetailScreen(
    assetDetailV2ViewModel: AssetDetailV2ViewModel,
    chartViewModel: AssetLineChartViewModel,
    priceChartViewModel: AssetPriceLineChartViewModel,
    csvViewModel: CsvViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    listener: AssetDetailScreenListener,
    marketsViewModel: AssetMarketsViewModel = hiltViewModel(),
    holdingViewModel: AssetHoldingViewModel = hiltViewModel(),
    headerViewModel: AssetDetailHeaderViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = { 2 })
        val viewState = assetDetailV2ViewModel.state.collectAsStateWithLifecycle().value
        val scope = rememberCoroutineScope()
        when (viewState) {
            Idle -> Unit
            is AssetDetailV2ViewModel.ViewState.Error -> ErrorState(assetDetailV2ViewModel::reloadViewState)
            AssetDetailV2ViewModel.ViewState.Loading -> LoadingState()
            is Content -> {
                LaunchedEffect(Unit) {
                    transactionHistoryViewModel.initViewState(viewState.address, viewState.asset.id)
                    headerViewModel.init(viewState.asset)
                    holdingViewModel.init(viewState.address, viewState.asset)
                    marketsViewModel.initViewState(viewState.asset)
                }
                Toolbar(
                    accountDisplayName = viewState.accountDisplayName,
                    accountIconDrawablePreview = viewState.accountIconDrawable,
                    onBackClick = listener::onNavBackClick
                )
                AssetDetailPagerIndicator(pagerState) { selectedPage ->
                    scope.launch { pagerState.animateScrollToPage(selectedPage) }
                }
                HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { page ->
                    when (page) {
                        HOLDINGS_PAGE -> {
                            AssetHoldingScreen(
                                headerViewModel,
                                chartViewModel,
                                holdingViewModel,
                                assetDetailV2ViewModel,
                                transactionHistoryViewModel,
                                csvViewModel,
                                listener
                            )
                        }

                        MARKETS_PAGE -> {
                            AssetMarketsScreen(headerViewModel, marketsViewModel, priceChartViewModel, listener)
                        }
                    }
                }
                LaunchedEffect(headerViewModel.viewEvent) {
                    headerViewModel.viewEvent.collect { event ->
                        when (event) {
                            is DisplayFailedToToggleFavoriteError -> listener.onFailedToUpdateFavoriteStatus()
                            is DisplayFailedToTogglePriceAlertError -> listener.onFailedToUpdatePriceAlertStatus()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Toolbar(
    accountDisplayName: AccountDisplayName,
    accountIconDrawablePreview: AccountIconDrawablePreview,
    onBackClick: () -> Unit
) {
    PeraToolbar(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = accountDisplayName.primaryDisplayName,
        secondaryText = accountDisplayName.secondaryDisplayName,
        startContainer = {
            PeraToolbarIcon(
                iconResId = R.drawable.ic_left_arrow,
                modifier = Modifier.clickableNoRipple(onClick = onBackClick)
            )
        },
        endContainer = {
            AccountIcon(
                modifier = Modifier.size(28.dp),
                iconDrawablePreview = accountIconDrawablePreview
            )
        }
    )
}

@Composable
private fun ErrorState(onRetryClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PeraSingleButtonState(
            modifier = Modifier.padding(24.dp),
            iconResId = null,
            titleResId = null,
            descriptionResId = R.string.an_error_occurred,
            buttonTextResId = R.string.retry,
            onClick = onRetryClick
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PeraCircularProgressIndicator()
    }
}

interface AssetDetailScreenListener : AssetMarketsScreenListener, AssetHoldingScreenListener {
    fun onNavBackClick()
    fun onFailedToUpdateFavoriteStatus()
    fun onFailedToUpdatePriceAlertStatus()
}
