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

import com.algorand.android.ui.device.model.DeviceConfig
import com.algorand.android.ui.webview.bridge.model.NotifyUserParams
import com.algorand.android.ui.webview.bridge.model.SettingsWebResponse
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult

interface PeraWebInterfaceEventMapper {
    fun mapRequests(params: String): List<PeraWebInterfaceEventResult>
}

internal interface PeraWebInterfaceNotifyUserEventMapper {
    operator fun invoke(params: NotifyUserParams): NotifyUser?
}

interface SettingsWebResponseMapper {
    fun mapInternalResponse(deviceConfig: DeviceConfig): SettingsWebResponse
    fun mapPublicResponse(deviceConfig: DeviceConfig): SettingsWebResponse
}
