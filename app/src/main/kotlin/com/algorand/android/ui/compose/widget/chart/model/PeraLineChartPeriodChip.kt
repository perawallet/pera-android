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

package com.algorand.android.ui.compose.widget.chart.model

import com.algorand.android.R

sealed interface PeraLineChartPeriodChip : PeraLineChartChip {

    data object OneDay : PeraLineChartPeriodChip {
        override val labelResId: Int = R.string.one_day_abbr
    }

    data object OneWeek : PeraLineChartPeriodChip {
        override val labelResId: Int = R.string.one_week_abbr
    }

    data object OneMonth : PeraLineChartPeriodChip {
        override val labelResId: Int = R.string.one_month_abbr
    }

    data object OneYear : PeraLineChartPeriodChip {
        override val labelResId: Int = R.string.one_year_abbr
    }
}
