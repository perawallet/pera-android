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

package com.algorand.android.ui.spotbanner.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import com.algorand.wallet.spotbanner.domain.model.SpotBanner

class SpotBannerCarouselView(context: Context, attrs: AttributeSet? = null) : AbstractComposeView(context, attrs) {

    private var state by mutableStateOf<List<SpotBanner>>(emptyList())
    private var listener: SpotBannerCarouselListener? = null

    @Composable
    override fun Content() {
        SpotBannerCarousel(state, listener)
    }

    fun setListener(listener: SpotBannerCarouselListener) {
        this.listener = listener
    }

    fun updateData(data: List<SpotBanner>) {
        state = data
    }
}
