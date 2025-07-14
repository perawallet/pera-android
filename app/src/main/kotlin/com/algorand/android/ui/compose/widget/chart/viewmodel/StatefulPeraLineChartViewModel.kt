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

package com.algorand.android.ui.compose.widget.chart.viewmodel

import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartData
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState
import com.algorand.wallet.viewmodel.StateViewModel

interface StatefulPeraLineChartViewModel : StateViewModel<ViewState> {

    fun displaySelectedPeriodValues(period: PeraLineChartPeriodChip)

    fun getSelectedChartData(index: Int): PeraLineChartData?

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data object Error : ViewState
        data class Content(
            val contentState: ContentState,
            val selectedPeriod: PeraLineChartPeriodChip,
            val periods: List<PeraLineChartPeriodChip>
        ) : ViewState {

            sealed interface ContentState {
                data object Loading : ContentState
                data class Data(val chartData: List<PeraLineChartData>) : ContentState
            }
        }
    }
}
