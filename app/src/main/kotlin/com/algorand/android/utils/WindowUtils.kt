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

package com.algorand.android.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.WindowMetrics

fun Activity.showDarkStatusBarIcons() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.showLightStatusBarIcons() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

fun Context.getDisplaySize(): Point {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowManager = getSystemService(WindowManager::class.java)
        val metrics: WindowMetrics = windowManager.currentWindowMetrics
        val bounds = metrics.bounds
        Point(bounds.width(), bounds.height())
    } else {
        val displaySize = Point()
        @Suppress("DEPRECATION")
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getSize(displaySize)
        displaySize
    }
}

// TODO: 20.01.2022 There is an issue on Android 12 to making app blurry when putting the app into background
//  https://github.com/Hipo/algorand-android/pull/781#discussion_r787853240
fun Activity.disableScreenCapture() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_SECURE,
        WindowManager.LayoutParams.FLAG_SECURE
    )
}

fun Activity.enableScreenCapture() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}
