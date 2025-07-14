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

package com.algorand.android.ui.compose.widget.chart.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

class PeraLineChartView(context: Context, attrs: AttributeSet? = null) : AbstractComposeView(context, attrs) {

    private var viewState by mutableStateOf<ViewState>(ViewState.Idle)

    @Composable
    override fun Content() {
        PeraTheme {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .defaultMinSize(minHeight = 136.dp)
            ) {
                when (val state = viewState) {
                    ViewState.Idle -> Unit
                    is ViewState.Data -> PeraLineChart(data = state.chartData)
                    is ViewState.Error -> PeraLineChartErrorState()
                    ViewState.Loading -> PeraLineChartLoadingState()
                }
            }
        }
    }

    fun showError() {
        viewState = ViewState.Error
    }

    fun showLoading() {
        viewState = ViewState.Loading
    }

    fun setData(chartData: List<Float>) {
        viewState = ViewState.Data(chartData)
    }

    private sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data object Error : ViewState
        data class Data(val chartData: List<Float>) : ViewState
    }
}
