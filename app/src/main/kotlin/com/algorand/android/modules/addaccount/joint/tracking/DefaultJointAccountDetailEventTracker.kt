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

internal class DefaultJointAccountDetailEventTracker @Inject constructor(
    peraEventTracker: PeraEventTracker
) : BaseEventTracker(peraEventTracker), JointAccountDetailEventTracker {

    override suspend fun logInboxJointAccountInviteAddPress() {
        logEvent(INBOX_JOINT_ACCOUNT_INVITE_ADD_PRESS)
    }

    override suspend fun logInboxJointAccountInviteIgnorePress() {
        logEvent(INBOX_JOINT_ACCOUNT_INVITE_IGNORE_PRESS)
    }

    override suspend fun logInboxJointAccountNameAccountPress() {
        logEvent(INBOX_JOINT_ACCOUNT_NAME_ACCOUNT_PRESS)
    }

    private companion object {
        const val INBOX_JOINT_ACCOUNT_INVITE_ADD_PRESS = "inbox_jointAccount_invite_add_press"
        const val INBOX_JOINT_ACCOUNT_INVITE_IGNORE_PRESS = "inbox_jointAccount_invite_ignore_press"
        const val INBOX_JOINT_ACCOUNT_NAME_ACCOUNT_PRESS = "inbox_jointAccount_nameAccount_press"
    }
}
