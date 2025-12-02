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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.utils.extensions.show

class PeraLineChartDeltaTextView(context: Context, attrs: AttributeSet?) : AbstractComposeView(context, attrs) {

    private var content by mutableStateOf<Content?>(null)

    @Composable
    override fun Content() {
        content?.run {
            PeraTheme {
                PeraLineChartDeltaText(delta, deltaRenderer)
            }
        }
    }

    fun setDelta(delta: Float, deltaRenderer: AmountRenderer) {
        content = Content(delta, deltaRenderer)
    }

    fun showView() {
        if (content != null) show()
    }

    private data class Content(val delta: Float, val deltaRenderer: AmountRenderer)
}
