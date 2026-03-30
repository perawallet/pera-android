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

package com.algorand.android.core.transaction.sync

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSyncSignRequestWithSignatures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncSignRequestPollingManager @Inject constructor(
    private val getSyncSignRequestWithSignatures: GetSyncSignRequestWithSignatures
) {
    private val _stateFlow = MutableStateFlow<SyncSignRequestPollingState>(SyncSignRequestPollingState.Idle)
    val stateFlow: StateFlow<SyncSignRequestPollingState> = _stateFlow.asStateFlow()

    private var pollingJob: Job? = null

    fun startPolling(
        scope: CoroutineScope,
        deviceId: String,
        signRequestId: String
    ) {
        stopPolling()
        _stateFlow.value = SyncSignRequestPollingState.Polling
        pollingJob = scope.launch {
            while (true) {
                val result = getSyncSignRequestWithSignatures(
                    deviceId = deviceId,
                    signRequestId = signRequestId
                )
                if (processPollResult(result)) return@launch
                delay(SYNC_POLL_INTERVAL_MS)
            }
        }
    }

    private fun processPollResult(result: PeraResult<SignRequestWithFullSignature>): Boolean {
        return when (result) {
            is PeraResult.Success -> processSignRequestStatus(result.data)
            is PeraResult.Error -> {
                _stateFlow.value = SyncSignRequestPollingState.Failed(SyncSignRequestFailReason.NetworkError)
                stopPolling()
                true
            }
        }
    }

    private fun processSignRequestStatus(signRequest: SignRequestWithFullSignature): Boolean {
        return when (val status = signRequest.status) {
            SignRequestStatus.READY -> {
                _stateFlow.value = SyncSignRequestPollingState.SignaturesReady(signRequest)
                stopPolling()
                true
            }

            SignRequestStatus.DECLINED -> {
                _stateFlow.value = SyncSignRequestPollingState.Declined
                stopPolling()
                true
            }

            SignRequestStatus.EXPIRED -> {
                _stateFlow.value = SyncSignRequestPollingState.Expired
                stopPolling()
                true
            }

            SignRequestStatus.FAILED -> {
                _stateFlow.value = SyncSignRequestPollingState.Failed(
                    SyncSignRequestFailReason.Unknown(signRequest.failReasonDisplay)
                )
                stopPolling()
                true
            }

            SignRequestStatus.CONFIRMED -> {
                _stateFlow.value = SyncSignRequestPollingState.SignaturesReady(signRequest)
                stopPolling()
                true
            }

            SignRequestStatus.PENDING,
            SignRequestStatus.SUBMITTING -> false

            null -> {
                _stateFlow.value = SyncSignRequestPollingState.Failed(
                    SyncSignRequestFailReason.SignRequestNotFound
                )
                stopPolling()
                true
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
        if (_stateFlow.value is SyncSignRequestPollingState.Polling) {
            _stateFlow.value = SyncSignRequestPollingState.Idle
        }
    }

    fun resetState() {
        stopPolling()
        _stateFlow.value = SyncSignRequestPollingState.Idle
    }

    companion object {
        const val SYNC_POLL_INTERVAL_MS = 3_000L
    }
}
