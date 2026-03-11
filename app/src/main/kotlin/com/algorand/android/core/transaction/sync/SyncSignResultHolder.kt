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

import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncSignResultHolder @Inject constructor() {

    private val pendingResults = ConcurrentHashMap<String, SyncSignResult>()
    private val _events = MutableSharedFlow<SyncSignResultEvent>(replay = 1, extraBufferCapacity = 1)
    val events: SharedFlow<SyncSignResultEvent> = _events.asSharedFlow()

    fun setResult(signRequestId: String, result: SyncSignResult) {
        pendingResults[signRequestId] = result
        _events.tryEmit(SyncSignResultEvent(signRequestId, result))
    }

    fun consumeResult(signRequestId: String): SyncSignResult? = pendingResults.remove(signRequestId)

    sealed interface SyncSignResult {
        data class SignaturesReady(val signRequest: SignRequestWithFullSignature) : SyncSignResult
        data object Failed : SyncSignResult
        data object Expired : SyncSignResult
        data object Declined : SyncSignResult
    }

    data class SyncSignResultEvent(val signRequestId: String, val result: SyncSignResult)
}
