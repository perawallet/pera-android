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

package com.algorand.android.modules.accountdetail.jointaccountdetail.domain.usecase

import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.usecase.DeleteInboxJointInvitationNotification
import com.algorand.wallet.inbox.domain.usecase.FetchInboxMessages
import javax.inject.Inject

class JointAccountInboxOperationsUseCase @Inject constructor(
    private val deviceIdUseCase: DeviceIdUseCase,
    private val fetchInboxMessages: FetchInboxMessages,
    private val deleteInboxJointInvitationNotification: DeleteInboxJointInvitationNotification
) {

    fun getDeviceId(): String? = deviceIdUseCase.getSelectedNodeDeviceId()

    suspend fun getInboxMessages(addresses: List<String>): PeraResult<InboxMessages> {
        val deviceId = getDeviceId()?.toLongOrNull()
            ?: return PeraResult.Error(Exception("Device ID not available"))
        return fetchInboxMessages(deviceId, addresses)
    }

    suspend fun deleteNotification(accountAddress: String): Boolean {
        val deviceId = getDeviceId()?.toLongOrNull() ?: return false
        val result = deleteInboxJointInvitationNotification(deviceId, accountAddress)
        return result is PeraResult.Success
    }
}
