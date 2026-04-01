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

internal class DefaultJointAccountOptionsEventTracker @Inject constructor(
    peraEventTracker: PeraEventTracker
) : BaseEventTracker(peraEventTracker), JointAccountOptionsEventTracker {

    override suspend fun logAccountScrTapmenuMoreRekeyJointTap() {
        logEvent(ACCOUNTSCR_TAPMENU_MORE_REKEY_JOINT_TAP)
    }

    override suspend fun logAccountScrTapmenuMoreJointAccountExportTap() {
        logEvent(ACCOUNTSCR_TAPMENU_MORE_JOINT_ACCOUNT_EXPORT_TAP)
    }

    override suspend fun logAccountScrTapmenuMoreJointAccountExportCopyTap() {
        logEvent(ACCOUNTSCR_TAPMENU_MORE_JOINT_ACCOUNT_EXPORT_COPY_TAP)
    }

    override suspend fun logAccountScrTapmenuMoreJointAccountExportShareTap() {
        logEvent(ACCOUNTSCR_TAPMENU_MORE_JOINT_ACCOUNT_EXPORT_SHARE_TAP)
    }

    private companion object {
        const val ACCOUNTSCR_TAPMENU_MORE_REKEY_JOINT_TAP = "accountscr_tapmenu_rekeyJntAcc_tap"
        const val ACCOUNTSCR_TAPMENU_MORE_JOINT_ACCOUNT_EXPORT_TAP =
            "accountscr_tapmenu_jntAccExport_tap"
        const val ACCOUNTSCR_TAPMENU_MORE_JOINT_ACCOUNT_EXPORT_COPY_TAP =
            "accountscr_tapmenu_jntAccExpCopy_tap"
        const val ACCOUNTSCR_TAPMENU_MORE_JOINT_ACCOUNT_EXPORT_SHARE_TAP =
            "accountscr_tapmenu_jntAccExpShare_tap"
    }
}
