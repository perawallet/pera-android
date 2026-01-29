@file:Suppress("EmptyFunctionBlock", "Unused", "MagicNumber", "LongMethod")
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

package com.algorand.android.modules.accountdetail.jointaccountdetail.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.modules.accountdetail.jointaccountdetail.ui.model.JointAccountParticipantItem
import com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel.JointAccountDetailViewModel.ViewState
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.PeraAccountItem
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@PeraPreviewLightDark
@Composable
fun JointAccountDetailContentPreview() {
    val contentState = createSampleContentState()
    PeraTheme {
        JointAccountDetailContentPreviewScreen(contentState = contentState, showActions = false)
    }
}

@PeraPreviewLightDark
@Composable
fun JointAccountDetailWithActionsPreview() {
    val contentState = createSampleContentState().copy(showActions = true, accountDisplayName = "")
    PeraTheme {
        JointAccountDetailContentPreviewScreen(contentState = contentState, showActions = true)
    }
}

@Composable
private fun JointAccountDetailContentPreviewScreen(
    contentState: ViewState.Content,
    showActions: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary)
    ) {
        PeraToolbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            text = contentState.accountDisplayName,
            secondaryText = contentState.accountAddressShortened,
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = {})
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            InformationCardPreview(numberOfAccounts = contentState.numberOfAccounts, threshold = contentState.threshold)
            Spacer(modifier = Modifier.height(32.dp))
            AccountsSectionPreview(accounts = contentState.participants)
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showActions) {
            ActionFooterPreview()
        }
    }
}

@Composable
private fun InformationCardPreview(numberOfAccounts: Int, threshold: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLighter, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(AccountIconResource.JOINT.iconResId),
                    contentDescription = null,
                    tint = PeraTheme.colors.text.grayLighter
                )
                Text(
                    text = numberOfAccounts.toString(),
                    style = PeraTheme.typography.title.small.sansMedium,
                    color = PeraTheme.colors.text.grayLighter
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
}

@Composable
private fun AccountsSectionPreview(accounts: List<JointAccountParticipantItem>) {
    Text(
        text = stringResource(R.string.accounts_with_count, accounts.size),
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.main
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column {
        accounts.forEachIndexed { index, account ->
            PeraAccountItem(
                modifier = Modifier
                    .height(76.dp)
                    .background(color = PeraTheme.colors.background.primary)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                displayName = AccountDisplayName(
                    accountAddress = account.address,
                    primaryDisplayName = account.displayName,
                    secondaryDisplayName = account.secondaryDisplayName
                ),
                canCopyable = false,
                iconContent = {
                    AccountIcon(
                        modifier = Modifier.size(40.dp),
                        iconDrawablePreview = account.iconDrawablePreview
                    )
                },
                trailingContent = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_copy),
                        contentDescription = null,
                        tint = PeraTheme.colors.text.main
                    )
                }
            )
            if (index < accounts.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 56.dp),
                    thickness = 1.dp,
                    color = PeraTheme.colors.layer.grayLighter
                )
            }
        }
    }
}

@Composable
private fun ActionFooterPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PeraTheme.colors.background.primary)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PeraSecondaryButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.ignore),
            onClick = {}
        )

        PeraPrimaryButton(
            modifier = Modifier.weight(2f),
            text = stringResource(R.string.add_to_accounts),
            onClick = {}
        )
    }
}

private fun createSampleContentState(): ViewState.Content {
    return ViewState.Content(
        accountDisplayName = "Joint Account",
        accountAddressShortened = "DUA4...2ETI",
        numberOfAccounts = 3,
        threshold = 2,
        participants = listOf(
            JointAccountParticipantItem(
                address = "HZQ73C...PSDZZE",
                displayName = "HZQ73C...PSDZZE",
                secondaryDisplayName = "Joseph",
                iconDrawablePreview = AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
            ),
            JointAccountParticipantItem(
                address = "tahir.algo",
                displayName = "tahir.algo",
                secondaryDisplayName = "DUA4...2ETI",
                iconDrawablePreview = AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
            ),
            JointAccountParticipantItem(
                address = "CNSW64...C4HNPI",
                displayName = "CNSW64...C4HNPI",
                secondaryDisplayName = null,
                iconDrawablePreview = AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
            )
        ),
        showActions = false
    )
}
