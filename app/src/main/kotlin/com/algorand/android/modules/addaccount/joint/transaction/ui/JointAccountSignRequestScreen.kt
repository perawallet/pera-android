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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.algorand.android.modules.addaccount.joint.transaction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionPreview
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirmButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun JointAccountSignRequestScreen(
    viewModel: JointAccountTransactionViewModel,
    listener: JointAccountSignRequestScreenListener,
    showPendingSignatures: Boolean = false,
    onPendingSignaturesShown: () -> Unit = {}
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(showPendingSignatures) {
        if (showPendingSignatures) {
            showBottomSheet = true
            onPendingSignaturesShown()
        }
    }

    when (val state = viewState) {
        is ViewState.Loading -> LoadingState()
        is ViewState.Content -> {
            TransactionContent(
                preview = state.preview,
                listener = listener,
                onShowBottomSheet = { showBottomSheet = true },
                onConfirm = { viewModel.onConfirmTransaction() }
            )

            if (showBottomSheet) {
                BottomSheetContent(
                    scope = scope,
                    sheetState = sheetState,
                    preview = state.preview,
                    onHideSheet = { showBottomSheet = false },
                    onCancel = { viewModel.onCancel() },
                    onCloseForNow = listener::onCloseClick,
                    onCloseCompleted = listener::onNavigateToHome
                )
            }
        }

        is ViewState.Error -> {
            ErrorState(messageResId = state.messageResId)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary),
        contentAlignment = Alignment.Center
    ) {
        PeraCircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(messageResId: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(messageResId),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TransactionContent(
    preview: JointAccountTransactionPreview,
    listener: JointAccountSignRequestScreenListener,
    onShowBottomSheet: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 180.dp)
        ) {
            ToolbarSection(preview = preview, listener = listener)
            Spacer(modifier = Modifier.height(48.dp))
            JointAccountIconSection()
            Spacer(modifier = Modifier.height(24.dp))
            TransferToSection(
                recipientAddress = preview.recipientShortAddress,
                onCopyClick = listener::onCopyAddressClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            AmountSection(preview = preview)
        }
        BottomSection(
            modifier = Modifier.align(Alignment.BottomCenter),
            preview = preview,
            onShowTransactionDetailsClick = onShowBottomSheet,
            onSlideToConfirm = onConfirm,
            listener = listener
        )
    }
}

@Composable
private fun BottomSheetContent(
    scope: CoroutineScope,
    sheetState: SheetState,
    preview: JointAccountTransactionPreview,
    onHideSheet: () -> Unit,
    onCancel: () -> Unit,
    onCloseForNow: () -> Unit,
    onCloseCompleted: () -> Unit
) {
    fun hideSheetAndExecute(action: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onHideSheet()
                action()
            }
        }
    }

    PendingSignaturesBottomSheet(
        sheetState = sheetState,
        transactionPreview = preview,
        onDismiss = { hideSheetAndExecute {} },
        onCancel = { hideSheetAndExecute(onCancel) },
        onCloseForNow = { hideSheetAndExecute(onCloseForNow) },
        onCloseCompleted = { hideSheetAndExecute(onCloseCompleted) }
    )
}

@Composable
private fun ToolbarSection(
    preview: JointAccountTransactionPreview,
    listener: JointAccountSignRequestScreenListener
) {
    Column {
        PeraToolbar(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.review_transaction),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_close,
                    modifier = Modifier.clickableNoRipple(onClick = listener::onCloseClick)
                )
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccountIcon(modifier = Modifier.size(20.dp), iconDrawablePreview = preview.jointAccountIconPreview)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = preview.jointAccountDisplayName.primaryDisplayName,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
    }
}

@Composable
private fun JointAccountIconSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = PeraTheme.colors.layer.grayLighter, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(R.drawable.ic_joint),
                contentDescription = null,
                tint = PeraTheme.colors.text.gray
            )
        }
    }
}

@Composable
private fun TransferToSection(recipientAddress: String, onCopyClick: () -> Unit) {
    val transferTo = stringResource(R.string.transfer_to)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = PeraTheme.colors.text.gray)) { append(transferTo) }
                withStyle(
                    SpanStyle(
                        color = PeraTheme.colors.text.main,
                        fontWeight = PeraTheme.typography.body.regular.sansMedium.fontWeight
                    )
                ) { append(recipientAddress) }
            },
            style = PeraTheme.typography.body.regular.sans
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onCopyClick, modifier = Modifier.size(16.dp)) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .padding(1.dp),
                painter = painterResource(R.drawable.ic_copy),
                contentDescription = stringResource(R.string.copy),
                tint = PeraTheme.colors.text.gray
            )
        }
    }
}

@Composable
private fun AmountSection(preview: JointAccountTransactionPreview) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = preview.amount,
            style = PeraTheme.typography.title.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = preview.convertedAmount,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BottomSection(
    modifier: Modifier = Modifier,
    preview: JointAccountTransactionPreview,
    onShowTransactionDetailsClick: () -> Unit,
    onSlideToConfirm: () -> Unit,
    listener: JointAccountSignRequestScreenListener
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PeraTheme.colors.background.primary)
    ) {
        HorizontalDivider(color = PeraTheme.colors.layer.grayLighter, thickness = 1.dp)
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            TransactionFeeRow(fee = preview.transactionFee)
            Spacer(modifier = Modifier.height(8.dp))
            ShowDetailsLink(onClick = onShowTransactionDetailsClick)
            Spacer(modifier = Modifier.height(16.dp))
            ConfirmButton(onConfirm = onSlideToConfirm)
            Spacer(modifier = Modifier.height(12.dp))
            DeclineButton(onClick = listener::onDeclineClick)
        }
    }
}

@Composable
private fun TransactionFeeRow(fee: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.transacting_fee),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
        Text(
            text = fee,
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.status.negative
        )
    }
}

@Composable
private fun ShowDetailsLink(onClick: () -> Unit) {
    Row(modifier = Modifier.clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.show_transaction_details),
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.link.primary
        )
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_right_arrow),
            contentDescription = null,
            tint = PeraTheme.colors.link.primary
        )
    }
}

@Composable
private fun ConfirmButton(onConfirm: () -> Unit) {
    val buttonState = remember { mutableStateOf(SlideToConfirm.ButtonState.Idle) }
    SlideToConfirmButton(
        modifier = Modifier.fillMaxWidth(),
        buttonState = buttonState,
        onConfirmed = onConfirm
    )
}

@Composable
private fun DeclineButton(onClick: () -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        text = stringResource(R.string.decline),
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.status.negative,
        textAlign = TextAlign.Center
    )
}

interface JointAccountSignRequestScreenListener {
    fun onCloseClick()
    fun onCopyAddressClick()
    fun onDeclineClick()
    fun onNavigateToHome()
}
