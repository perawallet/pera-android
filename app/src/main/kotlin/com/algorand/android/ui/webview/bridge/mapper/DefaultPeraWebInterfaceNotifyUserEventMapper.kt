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

import com.algorand.android.ui.webview.bridge.model.NotifyUserParams
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser.Haptic
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser.Message
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser.Sound
import javax.inject.Inject

internal class DefaultPeraWebInterfaceNotifyUserEventMapper @Inject constructor() :
    PeraWebInterfaceNotifyUserEventMapper {

    override fun invoke(params: NotifyUserParams): NotifyUser? {
        return when (params.type) {
            "haptic" -> mapHapticEvent(params)
            "sound" -> mapSoundEvent(params)
            "message" -> mapMessageEvent(params)
            else -> null
        }
    }

    private fun mapHapticEvent(params: NotifyUserParams): Haptic? {
        val hapticType = when (params.variant) {
            "light" -> Haptic.HapticType.LIGHT
            "medium" -> Haptic.HapticType.MEDIUM
            "heavy" -> Haptic.HapticType.HEAVY
            "success" -> Haptic.HapticType.SUCCESS
            "warning" -> Haptic.HapticType.WARNING
            "error" -> Haptic.HapticType.ERROR
            else -> return null
        }
        return Haptic(hapticType)
    }

    private fun mapSoundEvent(params: NotifyUserParams): Sound? {
        val soundType = when (params.variant) {
            "tap" -> Sound.SoundType.TAP
            "success" -> Sound.SoundType.SUCCESS
            "warning" -> Sound.SoundType.WARNING
            "error" -> Sound.SoundType.ERROR
            else -> return null
        }
        return Sound(soundType)
    }

    private fun mapMessageEvent(params: NotifyUserParams): Message? {
        val messageType = when (params.variant) {
            "toast" -> Message.MessageType.TOAST
            "banner" -> Message.MessageType.BANNER
            else -> return null
        }
        val message = params.message ?: return null
        return Message(messageType, message)
    }
}
