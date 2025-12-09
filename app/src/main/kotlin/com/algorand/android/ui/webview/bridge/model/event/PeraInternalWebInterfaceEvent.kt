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

sealed interface PeraInternalWebInterfaceEvent : PeraWebInterfaceEvent {

    sealed interface Command : PeraInternalWebInterfaceEvent {

        data class PushPublicWebView(
            val url: String,
            val title: String?,
            val projectId: String?,
            val isFavorite: Boolean?
        ) : Command

        data class OpenSystemBrowser(val url: String) : Command

        data class OpenNativeUri(val uri: String) : Command

        sealed interface NotifyUser : Command {

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

        data class LogAnalyticsEvent(val name: String, val payload: Map<String, String>?) : Command

        data object CloseWebView : Command
    }

    data class Query(val id: Long, val type: QueryType) : PeraInternalWebInterfaceEvent {

        sealed interface QueryType {
            data object GetAddresses : QueryType
            data object GetSettings : QueryType
            data object GetPublicSettings : QueryType
            data class CanOpenUri(val uri: String) : QueryType
        }
    }
}
