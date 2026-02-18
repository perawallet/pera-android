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

package com.algorand.android.modules.addaccount.joint.transaction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
internal fun StatusBadgesSection(
    signedCount: Int,
    totalParticipantCount: Int,
    timeRemaining: String?,
    transactionState: JointAccountTransactionState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (transactionState) {
            JointAccountTransactionState.Canceled -> {
                ErrorBadge(message = stringResource(R.string.transaction_canceled))
            }

            JointAccountTransactionState.Completed -> {
                SuccessBadge(message = stringResource(R.string.transaction_successfully_completed))
            }

            is JointAccountTransactionState.Failed -> {
                ErrorBadge(
                    message = transactionState.failReasonDisplay
                        ?: stringResource(R.string.failed_transaction)
                )
            }

            JointAccountTransactionState.Expired -> {
                ErrorBadge(message = stringResource(R.string.expired_transaction))
            }

            JointAccountTransactionState.Declined -> {
                ErrorBadge(message = stringResource(R.string.declined_transaction))
            }

            else -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SignedCountBadge(
                        signedCount = signedCount,
                        totalCount = totalParticipantCount
                    )
                    if (timeRemaining != null) {
                        TimeRemainingBadge(timeRemaining = timeRemaining)
                    }
                }
            }
        }
    }
}

@Composable
private fun SignedCountBadge(
    signedCount: Int,
    totalCount: Int
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PeraTheme.colors.layer.grayLighter)
            .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(AccountIconResource.CONTACT.iconResId),
            contentDescription = null,
            tint = PeraTheme.colors.text.main
        )
        Text(
            text = stringResource(R.string.of_signed, signedCount, totalCount),
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun TimeRemainingBadge(timeRemaining: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PeraTheme.colors.layer.grayLighter)
            .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            tint = PeraTheme.colors.text.main
        )
        Text(
            text = stringResource(R.string.time_left, timeRemaining),
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun ErrorBadge(message: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PeraTheme.colors.helper.negativeLighter)
            .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = PeraTheme.colors.helper.negative
        )
        Text(
            text = message,
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.helper.negative,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SuccessBadge(message: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PeraTheme.colors.helper.positiveLighter)
            .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = PeraTheme.colors.helper.positive
        )
        Text(
            text = message,
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.helper.positive
        )
    }
}
