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

sealed interface SyncSignRequestPollingState {
    data object Idle : SyncSignRequestPollingState
    data object Polling : SyncSignRequestPollingState
    data class SignaturesReady(val signRequest: SignRequestWithFullSignature) : SyncSignRequestPollingState
    data class Failed(val reason: SyncSignRequestFailReason) : SyncSignRequestPollingState
    data object Expired : SyncSignRequestPollingState
    data object Declined : SyncSignRequestPollingState
}

sealed interface SyncSignRequestFailReason {
    data object NetworkError : SyncSignRequestFailReason
    data object SignRequestNotFound : SyncSignRequestFailReason
    data class Unknown(val message: String?) : SyncSignRequestFailReason
}
