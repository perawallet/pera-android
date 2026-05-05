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

internal class DefaultJointAccountTransactionEventTracker @Inject constructor(
    peraEventTracker: PeraEventTracker
) : BaseEventTracker(peraEventTracker), JointAccountTransactionEventTracker {

    override suspend fun logJointAccountConfirmTxSlide() {
        logEvent(JOINT_ACCOUNT_CONFIRM_TX_SLIDE)
    }

    override suspend fun logJointAccountSignTxPress() {
        logEvent(JOINT_ACCOUNT_SIGN_TX_PRESS)
    }

    override suspend fun logJointAccountDeclinePendingTxPress() {
        logEvent(JOINT_ACCOUNT_DECLINE_PENDING_TX_PRESS)
    }

    override suspend fun logJointAccountCancelPendingTxPress() {
        logEvent(JOINT_ACCOUNT_CANCEL_PENDING_TX_PRESS)
    }

    override suspend fun logJointAccountCloseForNowPress() {
        logEvent(JOINT_ACCOUNT_CLOSE_FOR_NOW_PRESS)
    }

    override suspend fun logJointAccountCancelTxPress() {
        logEvent(JOINT_ACCOUNT_CANCEL_TX_PRESS)
    }

    override suspend fun logInboxJointAccountPendingTxClosePress() {
        logEvent(INBOX_JOINT_ACCOUNT_PENDING_TX_CLOSE_PRESS)
    }

    private companion object {
        const val JOINT_ACCOUNT_CONFIRM_TX_SLIDE = "jointAccount_confirmTx_slide"
        const val JOINT_ACCOUNT_SIGN_TX_PRESS = "jointAccount_signTx_press"
        const val JOINT_ACCOUNT_DECLINE_PENDING_TX_PRESS = "jointAccount_declinePendingTx_press"
        const val JOINT_ACCOUNT_CANCEL_PENDING_TX_PRESS = "jointAccount_cancelPendingTx_press"
        const val JOINT_ACCOUNT_CLOSE_FOR_NOW_PRESS = "jointAccount_closeForNow_press"
        const val JOINT_ACCOUNT_CANCEL_TX_PRESS = "jointAccount_cancelTx_press"
        const val INBOX_JOINT_ACCOUNT_PENDING_TX_CLOSE_PRESS = "inbox_jointAccount_pendingTx_close_press"
    }
}
