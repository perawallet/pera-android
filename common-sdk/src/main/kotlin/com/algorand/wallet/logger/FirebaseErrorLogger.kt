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

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

internal class FirebaseErrorLogger @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) : PeraErrorLogger {

    override fun logError(message: String) {
        PeraLogger.e(TAG, message)
        crashlytics.recordException(Exception(message))
    }

    override fun logError(throwable: Throwable) {
        PeraLogger.e(TAG, throwable.message.orEmpty(), throwable)
        crashlytics.recordException(throwable)
    }

    private companion object {
        const val TAG = "PeraError"
    }
}
