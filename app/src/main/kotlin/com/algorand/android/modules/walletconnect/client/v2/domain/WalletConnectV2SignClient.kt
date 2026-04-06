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

package com.algorand.android.modules.walletconnect.client.v2.domain

import app.perawallet.walletconnectv2.internal.common.exception.CannotFindSequenceForTopic
import app.perawallet.walletconnectv2.sign.client.Sign
import app.perawallet.walletconnectv2.sign.client.SignClient
import com.algorand.android.modules.walletconnect.client.v2.domain.repository.WalletConnectV2Repository
import com.algorand.android.utils.launchIO
import com.algorand.wallet.logger.PeraErrorLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WalletConnectV2SignClient @Inject constructor(
    @param:Named(WalletConnectV2Repository.INJECTION_NAME)
    private val walletConnectRepository: WalletConnectV2Repository,
    private val errorLogger: PeraErrorLogger
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun setWalletDelegate(delegate: SignClient.WalletDelegate) {
        SignClient.setWalletDelegate(delegate)
    }

    fun initialize(initParams: Sign.Params.Init, onSuccess: () -> Unit = {}) {
        try {
            SignClient.initialize(initParams, onSuccess = onSuccess) { error ->
                errorLogger.logError(error.throwable)
            }
        } catch (e: Exception) {
            errorLogger.logError(e)
        }
    }

    fun approveSession(approveProposal: Sign.Params.Approve) {
        SignClient.approveSession(approveProposal) { error ->
            errorLogger.logError(error.throwable)
        }
    }

    fun rejectSession(reject: Sign.Params.Reject) {
        SignClient.rejectSession(reject) { error ->
            errorLogger.logError(error.throwable)
        }
    }

    fun respond(response: Sign.Params.Response) {
        SignClient.respond(response) { error ->
            onSignClientError(response.sessionTopic, error)
        }
    }

    fun disconnect(disconnect: Sign.Params.Disconnect) {
        SignClient.disconnect(disconnect) { error ->
            onSignClientError(disconnect.sessionTopic, error)
        }
    }

    fun update(update: Sign.Params.Update) {
        SignClient.update(update) { error ->
            onSignClientError(update.sessionTopic, error)
        }
    }

    fun extend(extend: Sign.Params.Extend, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        SignClient.extend(
            extend = extend,
            onSuccess = { extendParams -> onSuccess(extendParams.topic) },
            onError = { error -> onError(error.throwable) }
        )
    }

    fun pingServer(ping: Sign.Params.Ping, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        val listener = object : Sign.Listeners.SessionPing {
            override fun onError(pingError: Sign.Model.Ping.Error) {
                onError(pingError.error)
            }

            override fun onSuccess(pingSuccess: Sign.Model.Ping.Success) {
                onSuccess(pingSuccess.topic)
            }
        }
        SignClient.ping(
            ping = ping,
            sessionPing = listener
        )
    }

    fun getActiveSessionByTopic(topic: String): Sign.Model.Session? {
        return SignClient.getActiveSessionByTopic(topic)
    }

    fun getListOfActiveSessions(): List<Sign.Model.Session> {
        return SignClient.getListOfActiveSessions()
    }

    private fun onSignClientError(sessionTopic: String, error: Sign.Model.Error) {
        errorLogger.logError(error.throwable)
        if (error.throwable is CannotFindSequenceForTopic) {
            coroutineScope.launchIO {
                walletConnectRepository.deleteById(sessionTopic)
            }
        }
    }
}
