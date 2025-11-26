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

package com.algorand.android.ui.webview.bridge.model.event

data class PeraInternalWebInterfaceEvent(
    override val name: String,
    val type: EventType
) : PeraWebInterfaceEvent {

    sealed interface EventType {
        data class PushPublicWebView(
            val url: String,
            val title: String?,
            val projectId: String?,
            val isFavorite: Boolean?
        ) : EventType

        data class OpenSystemBrowser(val url: String) : EventType

        data class CanOpenUri(val uri: String) : EventType

        data class OpenNativeUri(val uri: String) : EventType

        sealed interface NotifyUser : EventType {

            data class Haptic(val type: HapticType) : NotifyUser {
                enum class HapticType {
                    LIGHT, MEDIUM, HEAVY, SUCCESS, WARNING, ERROR
                }
            }

            data class Sound(val type: SoundType) : NotifyUser {
                enum class SoundType {
                    TAP, SUCCESS, WARNING, ERROR
                }
            }

            data class Message(val type: MessageType, val message: String) : NotifyUser {
                enum class MessageType {
                    TOAST, BANNER
                }
            }
        }

        data object GetAddresses : EventType

        data object GetSettings : EventType

        data class LogAnalyticsEvent(val name: String, val payload: Map<String, String>?) : EventType

        data object CloseWebView : EventType
    }
}
