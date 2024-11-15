/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.perawebview

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import com.algorand.android.discover.common.ui.model.WebViewTheme
import com.algorand.android.utils.preference.getSavedThemePreference
import javax.inject.Inject

class WebViewThemeHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun initWebViewTheme(webView: WebView) {
        with(webView.settings) {
            val isDarkMode = getWebViewThemeFromThemePreference(webView.context) == WebViewTheme.DARK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                isAlgorithmicDarkeningAllowed = isDarkMode
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                forceDark = if (isDarkMode) WebSettings.FORCE_DARK_ON else WebSettings.FORCE_DARK_OFF
                return
            }
        }
    }

    fun getWebViewThemeFromThemePreference(context: Context): WebViewTheme {
        val themePreference = sharedPreferences.getSavedThemePreference()
        val themeFromSystem = when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> WebViewTheme.DARK
            Configuration.UI_MODE_NIGHT_NO -> WebViewTheme.LIGHT
            else -> null
        }
        return WebViewTheme.getByThemePreference(themePreference, themeFromSystem)
    }
}
