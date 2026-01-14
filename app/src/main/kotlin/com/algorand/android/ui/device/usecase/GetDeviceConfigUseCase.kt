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

package com.algorand.android.ui.device.usecase

import android.content.SharedPreferences
import android.os.Build
import com.algorand.android.BuildConfig
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.ui.device.model.DeviceConfig
import com.algorand.android.usecase.NodeSettingsUseCase
import com.algorand.android.utils.preference.getSavedThemePreference
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.foundation.locale.LocaleProvider
import javax.inject.Inject

internal class GetDeviceConfigUseCase @Inject constructor(
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val sharedPreferences: SharedPreferences,
    private val getPrimaryCurrencyId: GetPrimaryCurrencyId,
    private val localeProvider: LocaleProvider,
    private val nodeSettingsUseCase: NodeSettingsUseCase
) : GetDeviceConfig {

    override suspend fun invoke(): DeviceConfig {
        val locale = localeProvider.getDefault()
        return DeviceConfig(
            platform = "android",
            appName = BuildConfig.APPLICATION_NAME,
            appPackageName = BuildConfig.APPLICATION_ID,
            appVersion = BuildConfig.VERSION_NAME,
            deviceId = getSelectedNodeDeviceId().orEmpty(),
            deviceVersion = Build.VERSION.SDK_INT.toString(),
            deviceModel = Build.MODEL,
            deviceOSVersion = Build.VERSION.RELEASE.orEmpty(),
            theme = sharedPreferences.getSavedThemePreference(),
            node = nodeSettingsUseCase.getActiveNodeOrDefault(),
            currency = getPrimaryCurrencyId(),
            region = locale.country,
            language = locale.language
        )
    }
}
