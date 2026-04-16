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

package com.algorand.wallet.logger

import android.util.Log

object PeraLogger {

    private var isEnabled: Boolean = false

    fun init(enabled: Boolean) {
        isEnabled = enabled
    }

    fun d(tag: String, message: String) {
        if (isEnabled) Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        if (isEnabled) Log.i(tag, message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (isEnabled) {
            if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (isEnabled) {
            if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
        }
    }
}
