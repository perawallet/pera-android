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

package com.algorand.android.modules.inbox.allaccounts.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.inbox.allaccounts.domain.model.InboxWithAccount
import com.algorand.android.modules.inbox.allaccounts.domain.model.SignatureRequestInboxItem
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxPreview
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewState
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.ui.compose.theme.ColorPalette
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.utils.getRelativeTimeDifference
import kotlinx.coroutines.flow.StateFlow

@Composable
fun InboxScreen(
    modifier: Modifier = Modifier,
    viewModel: InboxViewModel,
    listener: InboxScreenListener
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (viewState) {
            is InboxViewState.Loading -> {
                LoadingState()
            }

            is InboxViewState.Empty -> {
                EmptyState()
            }

            is InboxViewState.Content -> {
                val content = viewState as InboxViewState.Content
                ContentState(
                    accounts = content.inboxWithAccountList,
                    signatureRequests = content.signatureRequestList,
                    jointAccountInvitations = content.jointAccountInvitationList,
                    onAccountClick = listener::onAccountClick,
                    onSignatureRequestClick = listener::onSignatureRequestClick,
                    onJointAccountInvitationClick = listener::onJointAccountInvitationClick
                )
            }

            is InboxViewState.Error -> {
                EmptyState()
            }
        }
    }
}

@Composable
fun InboxScreen(
    modifier: Modifier = Modifier,
    viewStateFlow: StateFlow<InboxPreview>,
    listener: InboxScreenListener
) {
    val preview by viewStateFlow.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            preview.isLoading -> {
                LoadingState()
            }

            preview.isEmptyStateVisible -> {
                EmptyState()
            }

            else -> {
                ContentState(
                    accounts = preview.inboxWithAccountList,
                    signatureRequests = preview.signatureRequestList,
                    jointAccountInvitations = preview.jointAccountInvitationList,
                    onAccountClick = listener::onAccountClick,
                    onSignatureRequestClick = listener::onSignatureRequestClick,
                    onJointAccountInvitationClick = listener::onJointAccountInvitationClick
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_pending_asset_transfer),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(R.string.when_you_have_an_asset),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun ContentState(
    accounts: List<InboxWithAccount>,
    signatureRequests: List<SignatureRequestInboxItem>,
    jointAccountInvitations: List<JointAccountInvitationInboxItem>,
    onAccountClick: (String) -> Unit,
    onSignatureRequestClick: (String, Boolean) -> Unit, // signRequestId, canUserSign
    onJointAccountInvitationClick: (JointAccountInvitationInboxItem) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(
            items = signatureRequests,
            key = { it.signRequestId }
        ) { signatureRequest ->
            SignatureRequestInboxItem(
                signatureRequest = signatureRequest,
                onClick = { onSignatureRequestClick(signatureRequest.signRequestId, signatureRequest.canUserSign) }
            )
        }
        items(
            items = jointAccountInvitations,
            key = { it.id }
        ) { invitation ->
            JointAccountInvitationInboxItem(
                invitation = invitation,
                onClick = { onJointAccountInvitationClick(invitation) }
            )
        }
        items(
            items = accounts,
            key = { it.accountAddress }
        ) { account ->
            AccountInboxItem(
                account = account,
                onAccountClick = { onAccountClick(account.accountAddress) }
            )
        }
    }
}

@Composable
private fun AccountInboxItem(
    account: InboxWithAccount,
    onAccountClick: () -> Unit
) {
    val resources = LocalResources.current
    val incomingAssetCountText = resources.getQuantityString(
        R.plurals.incoming_assets,
        account.requestCount,
        account.requestCount
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAccountClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AccountIcon(
            modifier = Modifier.size(40.dp),
            iconDrawablePreview = account.accountIconDrawablePreview
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = incomingAssetCountText,
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main,
                maxLines = 1
            )
            Text(
                text = account.accountDisplayName.primaryDisplayName,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SignatureRequestInboxItem(
    signatureRequest: SignatureRequestInboxItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PeraTheme.colors.background.primary)
            .clickable { onClick() }
            .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        UnreadIndicator(isRead = signatureRequest.isRead)
        Spacer(modifier = Modifier.width(12.dp))
        AccountIcon(
            modifier = Modifier.size(40.dp),
            iconDrawablePreview = signatureRequest.accountIconDrawablePreview
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = buildSignatureRequestTitle(signatureRequest.jointAccountAddressShortened),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatusLine(
                timeAgo = signatureRequest.timeAgo
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusPillsRow(
                signedCount = signatureRequest.signedCount,
                totalCount = signatureRequest.totalCount,
                timeLeft = signatureRequest.timeLeft
            )
        }
    }
}

@Composable
private fun buildSignatureRequestTitle(addressShortened: String): AnnotatedString {
    val signatureRequestText = stringResource(R.string.signature_request)
    val toSignForText = stringResource(R.string.to_sign_for_format, addressShortened)
    val boldWeight = PeraTheme.typography.body.regular.sansMedium.fontWeight
    val regularStyle = SpanStyle(
        color = PeraTheme.colors.text.main,
        fontStyle = PeraTheme.typography.body.regular.sans.fontStyle
    )
    val boldStyle = SpanStyle(
        color = PeraTheme.colors.text.main,
        fontWeight = boldWeight,
        fontStyle = PeraTheme.typography.body.regular.sansMedium.fontStyle
    )

    return buildAnnotatedString {
        withStyle(style = boldStyle) {
            append(signatureRequestText)
        }
        withStyle(style = regularStyle) {
            append(toSignForText)
        }
    }
}

@Composable
private fun StatusLine(timeAgo: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_pending),
                contentDescription = stringResource(R.string.pending_transaction),
                tint = ColorPalette.Yellow.V600
            )
            Text(
                text = stringResource(R.string.pending_transaction),
                style = PeraTheme.typography.footnote.sansMedium,
                color = ColorPalette.Yellow.V600
            )
        }

        Box(
            modifier = Modifier
                .size(2.dp)
                .background(
                    color = PeraTheme.colors.layer.grayLighter,
                    shape = CircleShape
                )
        )

        Text(
            text = timeAgo,
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.grayLighter,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatusPillsRow(
    signedCount: Int,
    totalCount: Int,
    timeLeft: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SignedCountPill(
            signedCount = signedCount,
            totalCount = totalCount
        )
        timeLeft?.let {
            TimeLeftPill(timeLeft = it)
        }
    }
}

@Composable
private fun SignedCountPill(
    signedCount: Int,
    totalCount: Int
) {
    Row(
        modifier = Modifier
            .background(
                color = PeraTheme.colors.layer.grayLighter,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(AccountIconResource.CONTACT.iconResId),
            contentDescription = stringResource(R.string.pending_signatures),
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
private fun TimeLeftPill(timeLeft: String) {
    Row(
        modifier = Modifier
            .background(
                color = PeraTheme.colors.layer.grayLighter,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = stringResource(R.string.time_left, timeLeft),
            tint = PeraTheme.colors.text.main
        )
        Text(
            text = stringResource(R.string.time_left, timeLeft),
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun JointAccountInvitationInboxItem(
    invitation: JointAccountInvitationInboxItem,
    onClick: () -> Unit
) {
    val resources = LocalResources.current
    val timeAgoText = remember(invitation.creationDateTime, invitation.timeDifference) {
        getRelativeTimeDifference(
            resources,
            invitation.creationDateTime,
            invitation.timeDifference
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        UnreadIndicator(isRead = invitation.isRead)
        Spacer(modifier = Modifier.width(8.dp))
        AccountIconSection()
        InvitationContentSection(
            modifier = Modifier.weight(1f),
            invitation = invitation,
            timeAgoText = timeAgoText,
            onClick = onClick
        )
    }
}

@Composable
private fun UnreadIndicator(isRead: Boolean) {
    val unreadDescription = stringResource(R.string.unread)
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(4.dp)
            .semantics {
                if (!isRead) {
                    contentDescription = unreadDescription
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (!isRead) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = PeraTheme.colors.link.icon,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun AccountIconSection() {
    AccountIcon(
        modifier = Modifier.size(40.dp),
        iconDrawablePreview = AccountIconDrawablePreview(
            backgroundColorResId = AccountIconResource.JOINT.backgroundColorResId,
            iconTintResId = AccountIconResource.JOINT.iconTintResId,
            iconResId = AccountIconResource.JOINT.iconResId
        )
    )
}

@Composable
private fun InvitationContentSection(
    modifier: Modifier = Modifier,
    invitation: JointAccountInvitationInboxItem,
    timeAgoText: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = buildInvitationText(invitation.accountAddressShortened),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        ViewInvitationDetailsButton(onClick = onClick)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = timeAgoText,
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.grayLighter,
            maxLines = 1
        )
    }
}

@Composable
private fun ViewInvitationDetailsButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = PeraTheme.colors.layer.grayLighter,
            contentColor = PeraTheme.colors.text.main
        ),
        border = BorderStroke(0.dp, Color.Transparent),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 0.dp
        )
    ) {
        Text(
            text = stringResource(R.string.view_invitation_details),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.ic_right_arrow),
            contentDescription = null, // Decorative, button text already describes action
            modifier = Modifier.size(16.dp),
            tint = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun buildInvitationText(accountAddressShortened: String): AnnotatedString {
    val fullText = stringResource(
        R.string.you_ve_been_invited_to_join_joint_account,
        accountAddressShortened
    )
    val boldPart = stringResource(R.string.you_ve_been_invited)

    return buildAnnotatedString {
        val boldStartIndex = fullText.indexOf(boldPart)
        val boldEndIndex = boldStartIndex + boldPart.length

        if (boldStartIndex >= 0) {
            // Text before bold part
            if (boldStartIndex > 0) {
                append(fullText.substring(0, boldStartIndex))
            }

            // Bold part
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Medium
                )
            ) {
                append(boldPart)
            }

            // Text after bold part
            if (boldEndIndex < fullText.length) {
                append(fullText.substring(boldEndIndex))
            }
        } else {
            // Fallback if pattern not found
            append(fullText)
        }
    }
}

interface InboxScreenListener {
    fun onAccountClick(accountAddress: String)
    fun onSignatureRequestClick(signRequestId: String, canUserSign: Boolean)
    fun onJointAccountInvitationClick(invitation: JointAccountInvitationInboxItem)
    fun onInfoClick()
}
