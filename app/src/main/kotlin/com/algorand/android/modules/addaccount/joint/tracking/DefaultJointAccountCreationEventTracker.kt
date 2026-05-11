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

internal class DefaultJointAccountCreationEventTracker @Inject constructor(
    peraEventTracker: PeraEventTracker
) : BaseEventTracker(peraEventTracker), JointAccountCreationEventTracker {

    override suspend fun logOnbJointAccountWelcomePress() {
        logEvent(ONB_JOINT_ACCOUNT_WELCOME_PRESS)
    }

    override suspend fun logOnbJointAccountInfoScreenGoBackPress() {
        logEvent(ONB_JOINT_ACCOUNT_INFO_SCREEN_GO_BACK_PRESS)
    }

    override suspend fun logOnbJointAccountAddAccountPress() {
        logEvent(ONB_JOINT_ACCOUNT_ADD_ACCOUNT_PRESS)
    }

    override suspend fun logOnbJointAccountAddAccountContinuePress() {
        logEvent(ONB_JOINT_ACCOUNT_ADD_ACCOUNT_CONTINUE_PRESS)
    }

    override suspend fun logOnbJointAccountEditAccountPress() {
        logEvent(ONB_JOINT_ACCOUNT_EDIT_ACCOUNT_PRESS)
    }

    override suspend fun logOnbJointAccountRemoveAddressPress() {
        logEvent(ONB_JOINT_ACCOUNT_REMOVE_ADDRESS_PRESS)
    }

    override suspend fun logOnbJointAccountInfoScreenProceedPress() {
        logEvent(ONB_JOINT_ACCOUNT_INFO_SCREEN_PROCEED_PRESS)
    }

    override suspend fun logOnbJointAccountThresholdContinuePress() {
        logEvent(ONB_JOINT_ACCOUNT_THRESHOLD_CONTINUE_PRESS)
    }

    override suspend fun logOnbJointAccountNameAccountPress() {
        logEvent(ONB_JOINT_ACCOUNT_NAME_ACCOUNT_PRESS)
    }

    private companion object {
        const val ONB_JOINT_ACCOUNT_WELCOME_PRESS = "onb_jointAccount_welcome_press"
        const val ONB_JOINT_ACCOUNT_INFO_SCREEN_GO_BACK_PRESS = "onb_jointAccount_infoScreen_goBack_press"
        const val ONB_JOINT_ACCOUNT_ADD_ACCOUNT_PRESS = "onb_jointAccount_addAccount_press"
        const val ONB_JOINT_ACCOUNT_ADD_ACCOUNT_CONTINUE_PRESS = "onb_jointAccount_addAcc_continue_press"
        const val ONB_JOINT_ACCOUNT_EDIT_ACCOUNT_PRESS = "onb_jointAccount_editAccount_press"
        const val ONB_JOINT_ACCOUNT_REMOVE_ADDRESS_PRESS = "onb_jointAccount_removeAddress_press"
        const val ONB_JOINT_ACCOUNT_INFO_SCREEN_PROCEED_PRESS = "onb_jointAccount_infoScr_proceed_press"
        const val ONB_JOINT_ACCOUNT_THRESHOLD_CONTINUE_PRESS = "onb_jointAccount_thresholdContinue_press"
        const val ONB_JOINT_ACCOUNT_NAME_ACCOUNT_PRESS = "onb_jointAccount_nameAccount_press"
    }
}
