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

package com.algorand.android.ui.webview.bridge.mapper

import android.content.Context
import android.content.res.Configuration
import com.algorand.android.discover.common.ui.model.WebViewTheme
import com.algorand.android.discover.common.ui.model.WebViewTheme.Companion.getByThemePreference
import com.algorand.android.ui.device.model.DeviceConfig
import com.algorand.android.ui.webview.bridge.model.SettingsWebResponse
import com.algorand.android.utils.preference.ThemePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class DefaultSettingsWebResponseMapper @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SettingsWebResponseMapper {

    override fun mapInternalResponse(deviceConfig: DeviceConfig): SettingsWebResponse {
        return with(deviceConfig) {
            SettingsWebResponse(
                appName = appName,
                appPackageName = appPackageName,
                appVersion = appVersion,
                clientType = platform,
                deviceId = deviceId,
                deviceModel = deviceModel,
                deviceOSVersion = deviceOSVersion,
                theme = getWebViewTheme(theme),
                network = node.networkSlug,
                currency = currency,
                region = region,
                language = language,
            )
        }
    }

    override fun mapPublicResponse(deviceConfig: DeviceConfig): SettingsWebResponse {
        return with(deviceConfig) {
            SettingsWebResponse(
                appName = null,
                appPackageName = null,
                appVersion = null,
                clientType = null,
                deviceId = null,
                deviceModel = null,
                deviceOSVersion = null,
                theme = getWebViewTheme(theme),
                region = null,
                network = node.networkSlug,
                currency = currency,
                language = language,
            )
        }
    }

    private fun getWebViewTheme(theme: ThemePreference): String {
        return when (getByThemePreference(theme, getSystemThemePreference())) {
            WebViewTheme.LIGHT -> "light"
            WebViewTheme.DARK -> "dark"
        }
    }

    private fun getSystemThemePreference(): WebViewTheme? {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> WebViewTheme.DARK
            Configuration.UI_MODE_NIGHT_NO -> WebViewTheme.LIGHT
            else -> null
        }
    }
}
