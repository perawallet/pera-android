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

package com.algorand.wallet.inbox.domain.usecase

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.model.InboxSearchInput
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import javax.inject.Inject

internal class FetchInboxMessagesUseCase @Inject constructor(
    private val repository: InboxApiRepository
) : FetchInboxMessages {

    override suspend fun invoke(
        deviceId: Long,
        addresses: List<String>
    ): PeraResult<InboxMessages> {
        return repository.getInboxMessages(deviceId, InboxSearchInput(addresses = addresses))
    }
}
