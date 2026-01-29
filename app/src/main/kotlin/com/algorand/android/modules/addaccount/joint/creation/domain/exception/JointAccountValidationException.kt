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

package com.algorand.android.modules.addaccount.joint.creation.domain.exception

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.utils.exceptions.WarningException

sealed class JointAccountValidationException : Exception() {
    abstract fun toWarningException(): WarningException

    class InsufficientParticipants : JointAccountValidationException() {
        override fun toWarningException(): WarningException {
            return WarningException(
                titleRes = R.string.warning,
                annotatedString = AnnotatedString(R.string.joint_account_validation_insufficient_participants)
            )
        }
    }

    data class InvalidThreshold(
        val participantCount: Int,
        val threshold: Int
    ) : JointAccountValidationException() {
        override fun toWarningException(): WarningException {
            return WarningException(
                titleRes = R.string.warning,
                annotatedString = AnnotatedString(R.string.joint_account_validation_invalid_threshold)
            )
        }
    }

    companion object {
        const val MIN_PARTICIPANTS = 2
    }
}
