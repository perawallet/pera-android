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

package com.algorand.android.ui.vibration

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

@Suppress("MagicNumber")
object PeraVibration {

    private var vibrator: Vibrator? = null

    private const val INITIAL_DELAY_MS = 0L
    private const val DELAY_MS = 40L
    private const val DO_NOT_REPEAT = -1

    fun init(context: Context) {
        val appContext = context.applicationContext

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun light() {
        vibrate(10)
    }

    fun medium() {
        vibrate(25)
    }

    fun heavy() {
        vibrate(70)
    }

    fun success() {
        vibrate(50)
    }

    fun warning() {
        vibrate(40)
    }

    fun error() {
        vibrate(100)
    }

    fun vibrate(durationMs: Long) {
        vibrator?.let { v ->
            if (v.hasVibrator()) {
                val pattern = longArrayOf(INITIAL_DELAY_MS, durationMs, DELAY_MS, durationMs)
                v.vibrate(VibrationEffect.createWaveform(pattern, DO_NOT_REPEAT))
            }
        }
    }
}
