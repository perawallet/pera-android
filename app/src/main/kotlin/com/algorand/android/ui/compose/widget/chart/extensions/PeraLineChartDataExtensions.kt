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

package com.algorand.android.ui.compose.widget.chart.extensions

import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartData

private const val PERCENTAGE_MULTIPLIER = 100

fun List<PeraLineChartData>.getChangePercentage(): Float? {
    return if (size > 2) {
        val newValue = last().value
        val oldValue = first().value
        if (oldValue == 0f || newValue == 0f) {
            null
        } else {
            (newValue - oldValue) / oldValue * PERCENTAGE_MULTIPLIER
        }
    } else {
        null
    }
}
