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

package com.algorand.wallet.utils

import kotlin.properties.Delegates

class ClickCounter(
    private val timeWindowMillis: Long = DEFAULT_TIME_WINDOW_MILLIS,
    private val onClick: (Int) -> Unit
) {
    private var lastClickTime = 0L
    private var counter by Delegates.observable(0) { _, _, newValue ->
        onClick(newValue)
    }

    fun click() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > timeWindowMillis) {
            counter = 0
        }
        counter++
        lastClickTime = currentTime
    }

    fun reset() {
        counter = 0
        lastClickTime = 0L
    }

    private companion object {
        const val DEFAULT_TIME_WINDOW_MILLIS = 600L
    }
}
