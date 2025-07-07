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

package com.algorand.android.ui.compose.widget.chart.view.preview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.widget.chart.view.PeraLineChart
import kotlin.random.Random

@Preview
@Composable
fun PeraLineChartPreview() {
    fun generateNumber(currentNumber: Float): Float {
        return Random.nextDouble(from = currentNumber - 5.0, until = currentNumber + 5.0).toFloat()
    }

    val initialNumber = Random.nextDouble(until = 20.0)
    val numbers = mutableListOf<Float>(initialNumber.toFloat())
    repeat(100) { index ->
        numbers.add(generateNumber(numbers[index]))
    }

    PeraLineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        data = numbers
    )
}
