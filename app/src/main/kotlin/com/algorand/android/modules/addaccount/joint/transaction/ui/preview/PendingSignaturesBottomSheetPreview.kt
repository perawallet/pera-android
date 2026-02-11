@file:Suppress("MagicNumber")
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionViewState
import com.algorand.android.modules.addaccount.joint.transaction.ui.PendingSignaturesBottomSheet
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.bottomsheet.PeraBottomSheetDragIndicator

@OptIn(ExperimentalMaterial3Api::class)
@PeraPreviewLightDark
@Composable
fun PendingSignaturesBottomSheetPreview() {
    PeraTheme {
        PendingSignaturesBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            transactionPreview = createMockPreviewPending(),
            onDismiss = {},
            onCancel = {},
            onCloseForNow = {},
            onCloseCompleted = {}
        )
    }
}

@PeraPreviewLightDark
@Composable
fun PendingSignaturesContentPreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(PeraTheme.colors.background.primary)
                .padding(bottom = 16.dp)
        ) {
            PeraBottomSheetDragIndicator(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
            PendingSignaturesContentInternal(preview = createMockPreviewPending())
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PendingSignaturesCanceledPreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(PeraTheme.colors.background.primary)
                .padding(bottom = 16.dp)
        ) {
            PeraBottomSheetDragIndicator(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
            PendingSignaturesContentInternal(preview = createMockPreviewCanceled())
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PendingSignaturesCompletedPreview() {
    PeraTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(PeraTheme.colors.background.primary)
                .padding(bottom = 16.dp)
        ) {
            PeraBottomSheetDragIndicator(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
            PendingSignaturesContentInternal(preview = createMockPreviewCompleted())
        }
    }
}

@Composable
private fun PendingSignaturesContentInternal(preview: JointAccountTransactionViewState) {
    // This is a simplified version for preview purposes
    // The actual content is rendered by PendingSignaturesBottomSheet
    Spacer(modifier = Modifier.height(400.dp))
}

private fun createMockPreviewPending(): JointAccountTransactionViewState {
    val jointIcon = AccountIconDrawablePreviews.getJointDrawable()
    return JointAccountTransactionViewState(
        jointAccountDisplayName = AccountDisplayName(
            accountAddress = "HZQ73CXUPMVKRB4LNGJAGVZQCFPQDCCPSDZZE",
            primaryDisplayName = "Joint Account #1",
            secondaryDisplayName = "HZQ7...DZZE"
        ),
        jointAccountIconPreview = jointIcon,
        recipientAddress = "JDM35UAJXUCQY4XKXGHDWZQSVXJD3M",
        recipientShortAddress = "JDM35...XJD3M",
        amount = "₳21.6500",
        convertedAmount = "$6.24",
        transactionFee = "-₳0.002",
        transactionState = JointAccountTransactionState.PendingSignatures,
        signerAccounts = listOf(
            JointAccountSignerItem(
                accountAddress = "HZQ73CXUPMVKRB4LNGJAGVZQCFPQDCCPSDZZE",
                accountDisplayName = AccountDisplayName(
                    accountAddress = "HZQ73CXUPMVKRB4LNGJAGVZQCFPQDCCPSDZZE",
                    primaryDisplayName = "HZQ73C...PSDZZE",
                    secondaryDisplayName = null
                ),
                accountIconDrawablePreview = jointIcon,
                imageUri = null,
                signatureStatus = JointAccountSignatureStatus.Signed
            ),
            JointAccountSignerItem(
                accountAddress = "DUA4XLTFPBPWDDCH47SGDNZ5IJ52DFXG7X2N2ETI",
                accountDisplayName = AccountDisplayName(
                    accountAddress = "DUA4XLTFPBPWDDCH47SGDNZ5IJ52DFXG7X2N2ETI",
                    primaryDisplayName = "tahir.algo",
                    secondaryDisplayName = "DUA4...2ETI"
                ),
                accountIconDrawablePreview = jointIcon,
                imageUri = null,
                signatureStatus = JointAccountSignatureStatus.Pending,
                showProgress = true
            ),
            JointAccountSignerItem(
                accountAddress = "S93KZQHV4XLTFPBPWDDCH47SGNSK2",
                accountDisplayName = AccountDisplayName(
                    accountAddress = "S93KZQHV4XLTFPBPWDDCH47SGNSK2",
                    primaryDisplayName = "Katie Rochester",
                    secondaryDisplayName = "S93K...NSK2"
                ),
                accountIconDrawablePreview = jointIcon,
                imageUri = null,
                signatureStatus = JointAccountSignatureStatus.Pending,
                showProgress = true
            )
        ),
        signedCount = 1,
        requiredSignatureCount = 3,
        timeRemaining = "≈ 52m"
    )
}

private fun createMockPreviewCanceled(): JointAccountTransactionViewState {
    val jointIcon = AccountIconDrawablePreviews.getJointDrawable()
    return createMockPreviewPending().copy(
        transactionState = JointAccountTransactionState.Canceled,
        signerAccounts = listOf(
            JointAccountSignerItem(
                accountAddress = "HZQ73CXUPMVKRB4LNGJAGVZQCFPQDCCPSDZZE",
                accountDisplayName = AccountDisplayName(
                    accountAddress = "HZQ73CXUPMVKRB4LNGJAGVZQCFPQDCCPSDZZE",
                    primaryDisplayName = "HZQ73C...PSDZZE",
                    secondaryDisplayName = null
                ),
                accountIconDrawablePreview = jointIcon,
                imageUri = null,
                signatureStatus = JointAccountSignatureStatus.Signed
            ),
            JointAccountSignerItem(
                accountAddress = "DUA4XLTFPBPWDDCH47SGDNZ5IJ52DFXG7X2N2ETI",
                accountDisplayName = AccountDisplayName(
                    accountAddress = "DUA4XLTFPBPWDDCH47SGDNZ5IJ52DFXG7X2N2ETI",
                    primaryDisplayName = "tahir.algo",
                    secondaryDisplayName = "DUA4...2ETI"
                ),
                accountIconDrawablePreview = jointIcon,
                imageUri = null,
                signatureStatus = JointAccountSignatureStatus.Declined
            )
        )
    )
}

private fun createMockPreviewCompleted(): JointAccountTransactionViewState {
    val jointIcon = AccountIconDrawablePreviews.getJointDrawable()
    return createMockPreviewPending().copy(
        transactionState = JointAccountTransactionState.Completed,
        signedCount = 3,
        signerAccounts = listOf(
            JointAccountSignerItem(
                accountAddress = "HZQ73CXUPMVKRB4LNGJAGVZQCFPQDCCPSDZZE",
                accountDisplayName = AccountDisplayName(
                    accountAddress = "HZQ73CXUPMVKRB4LNGJAGVZQCFPQDCCPSDZZE",
                    primaryDisplayName = "HZQ73C...PSDZZE",
                    secondaryDisplayName = null
                ),
                accountIconDrawablePreview = jointIcon,
                imageUri = null,
                signatureStatus = JointAccountSignatureStatus.Signed
            ),
            JointAccountSignerItem(
                accountAddress = "DUA4XLTFPBPWDDCH47SGDNZ5IJ52DFXG7X2N2ETI",
                accountDisplayName = AccountDisplayName(
                    accountAddress = "DUA4XLTFPBPWDDCH47SGDNZ5IJ52DFXG7X2N2ETI",
                    primaryDisplayName = "tahir.algo",
                    secondaryDisplayName = "DUA4...2ETI"
                ),
                accountIconDrawablePreview = jointIcon,
                imageUri = null,
                signatureStatus = JointAccountSignatureStatus.Signed
            ),
            JointAccountSignerItem(
                accountAddress = "S93KZQHV4XLTFPBPWDDCH47SGNSK2",
                accountDisplayName = AccountDisplayName(
                    accountAddress = "S93KZQHV4XLTFPBPWDDCH47SGNSK2",
                    primaryDisplayName = "Katie Rochester",
                    secondaryDisplayName = "S93K...NSK2"
                ),
                accountIconDrawablePreview = jointIcon,
                imageUri = null,
                signatureStatus = JointAccountSignatureStatus.Signed
            )
        )
    )
}
