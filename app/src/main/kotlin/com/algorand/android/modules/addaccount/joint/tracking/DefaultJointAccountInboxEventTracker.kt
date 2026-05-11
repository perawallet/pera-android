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

package com.algorand.android.modules.addaccount.joint.tracking

import com.algorand.android.modules.tracking.core.BaseEventTracker
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import javax.inject.Inject

internal class DefaultJointAccountInboxEventTracker @Inject constructor(
    peraEventTracker: PeraEventTracker
) : BaseEventTracker(peraEventTracker), JointAccountInboxEventTracker {

    override suspend fun logInboxJointAccountInvitePress() {
        logEvent(INBOX_JOINT_ACCOUNT_INVITE_PRESS)
    }

    override suspend fun logInboxJointAccountPendingTxPress() {
        logEvent(INBOX_JOINT_ACCOUNT_PENDING_TX_PRESS)
    }

    private companion object {
        const val INBOX_JOINT_ACCOUNT_INVITE_PRESS = "inbox_jointAccount_invite_press"
        const val INBOX_JOINT_ACCOUNT_PENDING_TX_PRESS = "inbox_jointAccount_pendingTx_press"
    }
}
