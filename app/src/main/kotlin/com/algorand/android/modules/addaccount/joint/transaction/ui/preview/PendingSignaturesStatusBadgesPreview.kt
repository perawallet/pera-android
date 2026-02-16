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

package com.algorand.android.modules.addaccount.joint.transaction.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.modules.addaccount.joint.transaction.ui.StatusBadgesSection
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme

@PeraPreviewLightDark
@Composable
fun StatusBadgesSectionPendingPreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeraTheme.colors.background.primary)
                .padding(vertical = 16.dp)
        ) {
            StatusBadgesSection(
                signedCount = 1,
                totalParticipantCount = 3,
                timeRemaining = "≈ 52m",
                transactionState = JointAccountTransactionState.PendingSignatures
            )
        }
    }
}

@PeraPreviewLightDark
@Composable
fun StatusBadgesSectionPendingNoTimePreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeraTheme.colors.background.primary)
                .padding(vertical = 16.dp)
        ) {
            StatusBadgesSection(
                signedCount = 2,
                totalParticipantCount = 3,
                timeRemaining = null,
                transactionState = JointAccountTransactionState.PendingSignatures
            )
        }
    }
}

@PeraPreviewLightDark
@Composable
fun StatusBadgesSectionCanceledPreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeraTheme.colors.background.primary)
                .padding(vertical = 16.dp)
        ) {
            StatusBadgesSection(
                signedCount = 1,
                totalParticipantCount = 3,
                timeRemaining = null,
                transactionState = JointAccountTransactionState.Canceled
            )
        }
    }
}

@PeraPreviewLightDark
@Composable
fun StatusBadgesSectionCompletedPreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeraTheme.colors.background.primary)
                .padding(vertical = 16.dp)
        ) {
            StatusBadgesSection(
                signedCount = 3,
                totalParticipantCount = 3,
                timeRemaining = null,
                transactionState = JointAccountTransactionState.Completed
            )
        }
    }
}

@PeraPreviewLightDark
@Composable
fun StatusBadgesSectionFailedPreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeraTheme.colors.background.primary)
                .padding(vertical = 16.dp)
        ) {
            StatusBadgesSection(
                signedCount = 1,
                totalParticipantCount = 3,
                timeRemaining = null,
                transactionState = JointAccountTransactionState.Failed("Insufficient balance")
            )
        }
    }
}

@PeraPreviewLightDark
@Composable
fun StatusBadgesSectionAllStatesPreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeraTheme.colors.background.primary)
                .padding(16.dp)
        ) {
            StatusBadgesSection(
                signedCount = 1,
                totalParticipantCount = 3,
                timeRemaining = "≈ 52m",
                transactionState = JointAccountTransactionState.PendingSignatures
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusBadgesSection(
                signedCount = 1,
                totalParticipantCount = 3,
                timeRemaining = null,
                transactionState = JointAccountTransactionState.Canceled
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusBadgesSection(
                signedCount = 3,
                totalParticipantCount = 3,
                timeRemaining = null,
                transactionState = JointAccountTransactionState.Completed
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusBadgesSection(
                signedCount = 1,
                totalParticipantCount = 3,
                timeRemaining = null,
                transactionState = JointAccountTransactionState.Failed("Transaction failed")
            )
        }
    }
}
