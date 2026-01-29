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

package com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel

import android.util.Log
import com.algorand.android.R
import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.android.modules.addaccount.joint.creation.domain.exception.JointAccountValidationException
import com.algorand.wallet.inbox.domain.usecase.DeleteInboxJointInvitationNotification
import com.algorand.wallet.account.core.domain.usecase.AddJointAccount
import com.algorand.wallet.account.custom.domain.usecase.GetAllAccountOrderIndexes
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import java.io.IOException
import javax.inject.Inject

internal class DefaultNameJointAccountProcessor @Inject constructor(
    private val getAllAccountOrderIndexes: GetAllAccountOrderIndexes,
    private val getJointAccount: GetJointAccount,
    private val addJointAccount: AddJointAccount,
    private val deviceIdUseCase: DeviceIdUseCase,
    private val deleteInboxJointInvitationNotification: DeleteInboxJointInvitationNotification
) : NameJointAccountProcessor {

    override fun mapExceptionToErrorResId(exception: Throwable?): Int {
        return when (exception) {
            is JointAccountValidationException -> R.string.joint_account_validation_insufficient_participants
            is IOException -> R.string.the_internet_connection
            else -> R.string.an_error_occurred
        }
    }

    override suspend fun createLocalAccount(
        jointAccountAddress: String,
        participantAddresses: List<String>,
        threshold: Int,
        version: Int,
        accountName: String
    ): NameJointAccountProcessor.CreateLocalAccountResult {
        if (isAccountAlreadyExists(jointAccountAddress)) {
            tryDeleteInboxNotification(jointAccountAddress)
            return NameJointAccountProcessor.CreateLocalAccountResult.AlreadyExists
        }

        return trySaveJointAccountLocally(
            jointAccountAddress,
            participantAddresses,
            threshold,
            version,
            accountName
        )
    }

    private suspend fun isAccountAlreadyExists(address: String): Boolean {
        return getJointAccount(address) != null
    }

    private suspend fun trySaveJointAccountLocally(
        jointAccountAddress: String,
        participantAddresses: List<String>,
        threshold: Int,
        version: Int,
        accountName: String
    ): NameJointAccountProcessor.CreateLocalAccountResult {
        return try {
            addJointAccount(
                address = jointAccountAddress,
                participantAddresses = participantAddresses,
                threshold = threshold,
                version = version,
                customName = accountName.takeIf { it.isNotBlank() },
                orderIndex = calculateNextOrderIndex()
            )
            tryDeleteInboxNotification(jointAccountAddress)
            NameJointAccountProcessor.CreateLocalAccountResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save joint account: ${e.message}", e)
            NameJointAccountProcessor.CreateLocalAccountResult.Error(R.string.an_error_occurred)
        }
    }

    private suspend fun calculateNextOrderIndex(): Int {
        val orderIndexes = getAllAccountOrderIndexes()
        return if (orderIndexes.isEmpty()) 0 else (orderIndexes.maxOfOrNull { it.index } ?: -1) + 1
    }

    private suspend fun tryDeleteInboxNotification(jointAccountAddress: String) {
        val deviceIdLong = getDeviceIdAsLongOrNull() ?: return
        deleteInboxJointInvitationNotification(deviceIdLong, jointAccountAddress).use(
            onSuccess = { },
            onFailed = { exception, _ ->
                Log.w(TAG, "Failed to delete inbox notification: ${exception?.message}", exception)
            }
        )
    }

    private suspend fun getDeviceIdAsLongOrNull(): Long? {
        return try {
            deviceIdUseCase.getSelectedNodeDeviceId()?.toLong()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Device ID cannot be converted to Long: ${e.message}", e)
            null
        }
    }

    companion object {
        private const val TAG = "NameJointAccountProc"
    }
}
