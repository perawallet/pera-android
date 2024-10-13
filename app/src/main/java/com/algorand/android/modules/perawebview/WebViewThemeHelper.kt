package com.algorand.android.modules.perawebview

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import com.algorand.android.discover.common.ui.model.WebViewTheme
import com.algorand.android.utils.preference.ThemePreference
import com.algorand.android.utils.preference.getSavedThemePreference
import javax.inject.Inject

class WebViewThemeHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun initWebViewTheme(webView: WebView) {
        with(webView.settings) {
            val themePreference = sharedPreferences.getSavedThemePreference()
            val isDarkMode = getWebViewThemeFromThemePreference(webView.context, themePreference) == WebViewTheme.DARK
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

    private fun getWebViewThemeFromThemePreference(context: Context, themePreference: ThemePreference): WebViewTheme {
        val themeFromSystem = when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> WebViewTheme.DARK
            Configuration.UI_MODE_NIGHT_NO -> WebViewTheme.LIGHT
            else -> null
        }
        return WebViewTheme.getByThemePreference(themePreference, themeFromSystem)
    }
}
