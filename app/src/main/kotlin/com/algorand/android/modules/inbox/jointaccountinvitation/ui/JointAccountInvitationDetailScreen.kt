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

package com.algorand.android.modules.inbox.jointaccountinvitation.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@Composable
fun JointAccountInvitationDetailScreen(
    invitation: JointAccountInvitationInboxItem,
    accountDisplayNames: Map<String, AccountDisplayName>,
    accountIcons: Map<String, AccountIconDrawablePreview>,
    listener: JointAccountInvitationDetailScreenListener
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ToolbarSection(
            accountAddressShortened = invitation.accountAddressShortened,
            onBackClick = listener::onBackClick
        )

        ScrollableContentSection(
            modifier = Modifier.weight(1f),
            invitation = invitation,
            accountDisplayNames = accountDisplayNames,
            accountIcons = accountIcons,
            listener = listener
        )

        BottomActionsSection(
            onRejectClick = listener::onRejectClick,
            onAcceptClick = listener::onAcceptClick
        )
    }
}

@Composable
private fun ToolbarSection(
    accountAddressShortened: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        PeraToolbar(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.joint_account),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onBackClick)
                )
            }
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = accountAddressShortened,
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScrollableContentSection(
    modifier: Modifier = Modifier,
    invitation: JointAccountInvitationInboxItem,
    accountDisplayNames: Map<String, AccountDisplayName>,
    accountIcons: Map<String, AccountIconDrawablePreview>,
    listener: JointAccountInvitationDetailScreenListener
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        InformationCard(
            threshold = invitation.threshold,
            participantCount = invitation.participantAddresses.size
        )

        Spacer(modifier = Modifier.height(32.dp))

        ParticipantsSection(
            participantAddresses = invitation.participantAddresses,
            accountDisplayNames = accountDisplayNames,
            accountIcons = accountIcons,
            onCopyAddress = listener::onCopyAddress
        )
    }
}

@Composable
private fun BottomActionsSection(
    onRejectClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PeraTheme.colors.background.primary)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PeraSecondaryButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ignore),
                onClick = onRejectClick
            )
            PeraPrimaryButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.add_to_accounts),
                onClick = onAcceptClick
            )
        }
    }
}

@Composable
private fun InformationCard(
    threshold: Int,
    participantCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.layer.grayLighter,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NumberOfAccountsRow(participantCount = participantCount)

        ThresholdRow(threshold = threshold)
    }
}

@Composable
private fun ThresholdRow(threshold: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.threshold),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            Text(
                text = stringResource(R.string.minimum_number_of_accounts),
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }

        Text(
            text = threshold.toString(),
            style = PeraTheme.typography.title.small.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun NumberOfAccountsRow(participantCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.number_of_accounts),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            Text(
                text = stringResource(R.string.you_included),
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccountIcon(
                modifier = Modifier.size(32.dp),
                iconDrawablePreview = AccountIconDrawablePreview(
                    backgroundColorResId = AccountIconResource.JOINT.backgroundColorResId,
                    iconTintResId = AccountIconResource.JOINT.iconTintResId,
                    iconResId = AccountIconResource.JOINT.iconResId
                )
            )
            Text(
                text = participantCount.toString(),
                style = PeraTheme.typography.title.small.sansMedium,
                color = PeraTheme.colors.text.grayLighter
            )
        }
    }
}

@Composable
private fun ParticipantsSection(
    participantAddresses: List<String>,
    accountDisplayNames: Map<String, AccountDisplayName>,
    accountIcons: Map<String, AccountIconDrawablePreview>,
    onCopyAddress: (String) -> Unit
) {
    Text(
        text = stringResource(R.string.accounts_with_count, participantAddresses.size),
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.main
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        participantAddresses.forEachIndexed { index, address ->
            ParticipantItem(
                address = address,
                accountDisplayName = accountDisplayNames[address],
                accountIcon = accountIcons[address],
                onCopyAddress = onCopyAddress
            )
            if (index < participantAddresses.size - 1) {
                Spacer(modifier = Modifier.height(0.dp))
                Divider()
            }
        }
    }
}

@Composable
private fun Divider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 56.dp)
            .height(1.dp)
            .background(PeraTheme.colors.layer.grayLighter)
    )
}

@Composable
private fun ParticipantItem(
    address: String,
    accountDisplayName: AccountDisplayName?,
    accountIcon: AccountIconDrawablePreview?,
    onCopyAddress: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(
                color = PeraTheme.colors.background.primary
            )
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))

        AccountIcon(
            modifier = Modifier.size(40.dp),
            iconDrawablePreview = accountIcon ?: AccountIconDrawablePreview(
                backgroundColorResId = AccountIconResource.JOINT.backgroundColorResId,
                iconTintResId = AccountIconResource.JOINT.iconTintResId,
                iconResId = AccountIconResource.JOINT.iconResId
            )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = accountDisplayName?.primaryDisplayName ?: address,
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            accountDisplayName?.secondaryDisplayName?.let {
                Text(
                    text = it,
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.grayLighter
                )
            }
        }

        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickableNoRipple { onCopyAddress(address) },
            painter = painterResource(R.drawable.ic_copy),
            contentDescription = stringResource(R.string.copy_address),
            tint = PeraTheme.colors.text.grayLighter
        )

        Spacer(modifier = Modifier.width(16.dp))
    }
}

interface JointAccountInvitationDetailScreenListener {
    fun onBackClick()
    fun onAcceptClick()
    fun onRejectClick()
    fun onCopyAddress(address: String)
}
