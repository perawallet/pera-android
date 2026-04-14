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

package com.algorand.backup.data.service

import com.algorand.backup.data.mapper.BackupWebSocketEventMapper
import com.algorand.backup.domain.model.BackupWebSocketEvent
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

internal class BackupWebSocketClient @Inject constructor(
    private val httpClient: OkHttpClient,
    private val webSocketUrlBuilder: BackupWebSocketUrlBuilder,
    private val eventMapper: BackupWebSocketEventMapper
) {

    private val _events = MutableSharedFlow<BackupWebSocketEvent>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<BackupWebSocketEvent>
        get() = _events.asSharedFlow()

    private var webSocket: WebSocket? = null
    private var reconnectJob: Job? = null
    private var scope: CoroutineScope? = null
    private var isConnecting = false
    private var shouldReconnect = false
    private var reconnectAttempt = 0

    @Synchronized
    fun connect(scope: CoroutineScope) {
        if (webSocket != null || isConnecting) return

        this.scope = scope
        shouldReconnect = true
        openConnection()
    }

    @Synchronized
    fun disconnect() {
        shouldReconnect = false
        reconnectJob?.cancel()
        reconnectJob = null
        webSocket?.close(CLOSE_NORMAL, "Client disconnect")
        webSocket = null
        isConnecting = false
        reconnectAttempt = 0
        scope = null
    }

    @Synchronized
    private fun openConnection() {
        isConnecting = true

        val wsUrl = when (val result = webSocketUrlBuilder.buildUrl()) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> {
                isConnecting = false
                _events.tryEmit(BackupWebSocketEvent.Error(result.exception))
                scheduleReconnect()
                return
            }
        }

        val request = Request.Builder().url(wsUrl).build()
        webSocket = httpClient.newWebSocket(request, getWebSocketListener())
    }

    private fun getWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                handleOpen()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                eventMapper.mapToEvent(text)?.let { event ->
                    _events.tryEmit(event)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(CLOSE_NORMAL, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _events.tryEmit(BackupWebSocketEvent.Disconnected(reason))
                handleDisconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _events.tryEmit(BackupWebSocketEvent.Error(Exception(t.message, t)))
                handleDisconnect()
            }
        }
    }

    @Synchronized
    private fun handleOpen() {
        isConnecting = false
        reconnectAttempt = 0
        _events.tryEmit(BackupWebSocketEvent.Connected)
    }

    @Synchronized
    private fun handleDisconnect() {
        webSocket = null
        isConnecting = false
        scheduleReconnect()
    }

    private fun scheduleReconnect() {
        if (!shouldReconnect) return

        val currentScope = scope ?: return

        reconnectJob?.cancel()
        reconnectJob = currentScope.launch {
            val delayMs = calculateBackoff(reconnectAttempt)
            delay(delayMs)

            if (!shouldReconnect) return@launch

            reconnectAttempt++
            openConnection()
        }
    }

    private fun calculateBackoff(attempt: Int): Long {
        val delayMs = INITIAL_BACKOFF_MS * (1L shl attempt.coerceAtMost(MAX_BACKOFF_SHIFT))
        val jitter = Random.nextInt(JITTER_MAX_MS + 1)
        return delayMs.coerceAtMost(MAX_BACKOFF_MS) + jitter
    }

    private companion object {
        const val CLOSE_NORMAL = 1000
        const val INITIAL_BACKOFF_MS = 1000L
        const val MAX_BACKOFF_MS = 60_000L
        const val MAX_BACKOFF_SHIFT = 5
        const val JITTER_MAX_MS = 500
    }
}
