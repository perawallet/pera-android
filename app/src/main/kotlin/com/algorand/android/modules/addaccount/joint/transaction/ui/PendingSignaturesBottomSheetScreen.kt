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

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionViewState
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.ContactIcon
import com.algorand.android.ui.compose.widget.bottomsheet.PeraBottomSheetDragIndicator
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator

@Composable
fun PendingSignaturesBottomSheetScreen(
    viewModel: JointAccountTransactionViewModel,
    onClose: () -> Unit,
    onCloseCompleted: () -> Unit,
    onCancel: () -> Unit,
    onSignLedgerAccount: (JointAccountSignerItem) -> Unit = {}
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    when (val state = viewState) {
        is ViewState.Loading -> {
            LoadingContent()
        }

        is ViewState.Content -> {
            PendingSignaturesContent(
                transactionPreview = state.preview,
                hideCloseForNow = viewModel.hideCloseForNow,
                onCancel = onCancel,
                onClose = onClose,
                onCloseCompleted = onCloseCompleted,
                onSignLedgerAccount = onSignLedgerAccount
            )
        }

        is ViewState.Error -> {
            ErrorContent(
                message = stringResource(state.messageResId),
                onClose = onClose
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.background.primary,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        PeraCircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.background.primary,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = message,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onClose,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PeraTheme.colors.layer.grayLighter,
                contentColor = PeraTheme.colors.text.main
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.close),
                style = PeraTheme.typography.body.regular.sansMedium
            )
        }
    }
}

@Composable
fun PendingSignaturesContent(
    transactionPreview: JointAccountTransactionViewState,
    hideCloseForNow: Boolean = false,
    onCancel: () -> Unit,
    onClose: () -> Unit,
    onCloseCompleted: () -> Unit,
    onSignLedgerAccount: (JointAccountSignerItem) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.background.primary,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(bottom = 16.dp)
    ) {
        PeraBottomSheetDragIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, bottom = 8.dp)
        )

        TitleSection(title = stringResource(R.string.pending_signatures))

        Spacer(modifier = Modifier.height(16.dp))

        StatusBadgesSection(
            signedCount = transactionPreview.signedCount,
            threshold = transactionPreview.threshold,
            timeRemaining = transactionPreview.timeRemaining,
            transactionState = transactionPreview.transactionState
        )

        Spacer(modifier = Modifier.height(16.dp))

        AccountsSectionHeader(threshold = transactionPreview.threshold)

        Spacer(modifier = Modifier.height(16.dp))

        SignersListSection(
            modifier = Modifier.padding(horizontal = 24.dp),
            signers = transactionPreview.signerAccounts,
            onSignLedgerAccount = onSignLedgerAccount
        )

        Spacer(modifier = Modifier.height(24.dp))

        ActionButtonsSection(
            modifier = Modifier.padding(horizontal = 24.dp),
            transactionState = transactionPreview.transactionState,
            hasProposerAddress = transactionPreview.hasProposerAddress,
            hideCloseForNow = hideCloseForNow,
            onCancel = onCancel,
            onClose = onClose,
            onCloseCompleted = onCloseCompleted
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TitleSection(title: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = title,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.main,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun AccountsSectionHeader(threshold: Int) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = stringResource(R.string.accounts),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Text(
            text = stringResource(R.string.you_need_at_least_accounts_to_sign, threshold),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun SignersListSection(
    modifier: Modifier = Modifier,
    signers: List<JointAccountSignerItem>,
    onSignLedgerAccount: (JointAccountSignerItem) -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        signers.forEach { signer ->
            SignerItem(
                signer = signer,
                onSignLedgerAccount = onSignLedgerAccount
            )
        }
    }
}

@Composable
private fun SignerItem(
    signer: JointAccountSignerItem,
    onSignLedgerAccount: (JointAccountSignerItem) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = PeraTheme.colors.layer.gray,
                shape = RoundedCornerShape(12.dp)
            )
            .background(PeraTheme.colors.background.primary)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SignerIcon(
                iconDrawablePreview = signer.accountIconDrawablePreview,
                imageUri = signer.imageUri,
                isLocalAccount = signer.isLocalAccount
            )
            SignerInfo(
                displayName = signer.accountDisplayName,
                signatureStatus = signer.signatureStatus
            )
        }

        SignerStatusIcon(
            signer = signer,
            onSignLedgerAccount = onSignLedgerAccount
        )
    }
}

@Composable
private fun SignerIcon(
    iconDrawablePreview: AccountIconDrawablePreview,
    imageUri: Uri?,
    isLocalAccount: Boolean
) {
    if (isLocalAccount) {
        AccountIcon(
            modifier = Modifier.size(20.dp),
            iconDrawablePreview = iconDrawablePreview
        )
    } else {
        ContactIcon(imageUri = imageUri, size = 20.dp)
    }
}

@Composable
private fun SignerInfo(
    displayName: AccountDisplayName,
    signatureStatus: JointAccountSignatureStatus
) {
    val primaryName = displayName.primaryDisplayName
    val secondaryName = displayName.secondaryDisplayName

    Column {
        Text(
            text = primaryName,
            style = PeraTheme.typography.body.regular.sans,
            color = when (signatureStatus) {
                JointAccountSignatureStatus.Declined -> PeraTheme.colors.helper.negative
                else -> PeraTheme.colors.text.main
            }
        )
        if (secondaryName != null && primaryName != secondaryName) {
            Text(
                text = secondaryName,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.grayLighter
            )
        }
    }
}

@Composable
private fun SignerStatusIcon(
    signer: JointAccountSignerItem,
    onSignLedgerAccount: (JointAccountSignerItem) -> Unit = {}
) {
    when (signer.signatureStatus) {
        JointAccountSignatureStatus.Signed -> {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_check),
                contentDescription = stringResource(R.string.signed),
                tint = PeraTheme.colors.helper.positive
            )
        }

        JointAccountSignatureStatus.Declined -> {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.decline),
                tint = PeraTheme.colors.helper.negative
            )
        }

        JointAccountSignatureStatus.Pending -> {
            if (signer.canSignWithLedger) {
                LedgerSignButton(onClick = { onSignLedgerAccount(signer) })
            } else if (signer.showProgress) {
                PeraCircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
private fun LedgerSignButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(PeraTheme.colors.button.primary.background)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.sign),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.button.primary.text
        )
    }
}

@Composable
private fun ActionButtonsSection(
    modifier: Modifier = Modifier,
    transactionState: JointAccountTransactionState,
    hasProposerAddress: Boolean,
    hideCloseForNow: Boolean,
    onCancel: () -> Unit,
    onClose: () -> Unit,
    onCloseCompleted: () -> Unit
) {
    val isFinalized = transactionState.isFinalized()
    val isReadyToSubmit = transactionState == JointAccountTransactionState.ReadyToSubmit
    val showSingleCloseButton = isFinalized || !hasProposerAddress || isReadyToSubmit

    if (showSingleCloseButton) {
        SingleCloseButton(
            modifier = modifier,
            onClick = if (isFinalized) onCloseCompleted else onClose
        )
    } else if (hideCloseForNow) {
        SingleCancelButton(
            modifier = modifier,
            onClick = onCancel
        )
    } else {
        ProposerActionButtons(
            modifier = modifier,
            onCancel = onCancel,
            onClose = onClose
        )
    }
}

@Composable
private fun SingleCloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PeraTheme.colors.layer.grayLighter,
            contentColor = PeraTheme.colors.text.main
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(
            text = stringResource(R.string.close),
            style = PeraTheme.typography.body.regular.sansMedium
        )
    }
}

@Composable
private fun SingleCancelButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PeraTheme.colors.layer.grayLighter,
            contentColor = PeraTheme.colors.text.main
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(
            text = stringResource(R.string.cancel),
            style = PeraTheme.typography.body.regular.sansMedium
        )
    }
}

@Composable
private fun ProposerActionButtons(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            onClick = onCancel,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PeraTheme.colors.layer.grayLighter,
                contentColor = PeraTheme.colors.text.main
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = stringResource(R.string.cancel),
                style = PeraTheme.typography.body.regular.sansMedium
            )
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            onClick = onClose,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PeraTheme.colors.button.primary.background,
                contentColor = PeraTheme.colors.button.primary.text
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = stringResource(R.string.close_for_now),
                style = PeraTheme.typography.body.regular.sansMedium
            )
        }
    }
}
