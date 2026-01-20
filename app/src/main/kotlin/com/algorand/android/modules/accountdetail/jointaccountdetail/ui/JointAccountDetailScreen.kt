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

package com.algorand.android.modules.accountdetail.jointaccountdetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountdetail.jointaccountdetail.ui.model.JointAccountParticipantItem
import com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel.JointAccountDetailViewModel
import com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel.JointAccountDetailViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.ContactIcon
import com.algorand.android.ui.compose.widget.PeraAccountItem
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.utils.toShortenedAddress

@Composable
fun JointAccountDetailScreen(
    viewModel: JointAccountDetailViewModel,
    listener: JointAccountDetailScreenListener
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary)
    ) {
        ScreenHeader(
            viewState = viewState,
            accountAddress = viewModel.accountAddress,
            onBackClick = listener::onBackClick
        )
        ScreenContent(viewState = viewState, listener = listener)
    }
}

@Composable
private fun ScreenHeader(
    viewState: ViewState,
    accountAddress: String,
    onBackClick: () -> Unit
) {
    val displayName = when (viewState) {
        is ViewState.Content -> viewState.accountDisplayName.ifBlank {
            stringResource(R.string.joint_account)
        }

        else -> stringResource(R.string.joint_account)
    }
    val addressShortened = when (viewState) {
        is ViewState.Content -> viewState.accountAddressShortened
        else -> accountAddress.toShortenedAddress()
    }

    PeraToolbar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        text = displayName,
        secondaryText = addressShortened,
        startContainer = {
            PeraToolbarIcon(
                iconResId = R.drawable.ic_left_arrow,
                modifier = Modifier.clickableNoRipple(onClick = onBackClick)
            )
        }
    )
}

@Composable
private fun ColumnScope.ScreenContent(
    viewState: ViewState,
    listener: JointAccountDetailScreenListener
) {
    when (viewState) {
        is ViewState.Loading -> LoadingState()
        is ViewState.Content -> ContentState(contentState = viewState, listener = listener)
        is ViewState.Error -> ErrorState(errorType = viewState.type)
    }
}

@Composable
private fun ColumnScope.LoadingState() {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PeraTheme.colors.button.primary.background)
    }
}

@Composable
private fun ColumnScope.ErrorState(errorType: JointAccountDetailViewModel.ErrorType) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val errorMessage = when (errorType) {
            JointAccountDetailViewModel.ErrorType.INVITATION_NOT_FOUND ->
                stringResource(R.string.account_not_found)

            JointAccountDetailViewModel.ErrorType.NETWORK_ERROR ->
                stringResource(R.string.error_connection_title)
        }
        Text(
            text = errorMessage,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun ColumnScope.ContentState(
    contentState: ViewState.Content,
    listener: JointAccountDetailScreenListener
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        InformationCard(numberOfAccounts = contentState.numberOfAccounts, threshold = contentState.threshold)
        Spacer(modifier = Modifier.height(32.dp))
        AccountsSection(
            accounts = contentState.participants,
            onEditClick = listener::onEditAddressClick,
            onCopyAddressClick = listener::onCopyAddressClick
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
    if (contentState.showActions) {
        ActionFooter(onIgnoreClick = listener::onIgnoreClick, onAddClick = listener::onAddClick)
    }
}

@Composable
private fun ActionFooter(
    onIgnoreClick: () -> Unit,
    onAddClick: () -> Unit
) {
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
            onClick = onIgnoreClick
        )

        PeraPrimaryButton(
            modifier = Modifier.weight(2f),
            text = stringResource(R.string.add_to_accounts),
            onClick = onAddClick
        )
    }
}

@Composable
private fun InformationCard(numberOfAccounts: Int, threshold: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLighter, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NumberOfAccountsRow(numberOfAccounts = numberOfAccounts)
        ThresholdRow(threshold = threshold)
    }
}

@Composable
private fun NumberOfAccountsRow(numberOfAccounts: Int) {
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
                contentDescription = stringResource(R.string.joint_account),
                tint = PeraTheme.colors.text.grayLighter
            )
            Text(
                text = numberOfAccounts.toString(),
                style = PeraTheme.typography.title.small.sansMedium,
                color = PeraTheme.colors.text.grayLighter
            )
        }
    }
}

@Composable
private fun ThresholdRow(threshold: Int) {
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

@Composable
private fun AccountsSection(
    accounts: List<JointAccountParticipantItem>,
    onEditClick: (String) -> Unit,
    onCopyAddressClick: (String) -> Unit
) {
    Text(
        text = stringResource(R.string.accounts_with_count, accounts.size),
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.main
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column {
        accounts.forEachIndexed { index, account ->
            ParticipantAccountItem(
                account = account,
                onEditClick = { onEditClick(account.address) },
                onCopyAddressClick = { onCopyAddressClick(account.address) }
            )
            if (index < accounts.size - 1) {
                ParticipantDivider()
            }
        }
    }
}

@Composable
private fun ParticipantDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 56.dp),
        thickness = 1.dp,
        color = PeraTheme.colors.layer.grayLighter
    )
}

@Composable
private fun ParticipantAccountItem(
    account: JointAccountParticipantItem,
    onEditClick: () -> Unit,
    onCopyAddressClick: () -> Unit
) {
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
            if (account.isLocalAccount) {
                AccountIcon(
                    modifier = Modifier.size(40.dp),
                    iconDrawablePreview = account.iconDrawablePreview
                )
            } else {
                ContactIcon(
                    imageUri = account.imageUri,
                    size = 40.dp
                )
            }
        },
        trailingContent = {
            if (account.isContact) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickableNoRipple(onClick = onEditClick),
                    painter = painterResource(R.drawable.ic_pen),
                    contentDescription = stringResource(R.string.edit_address),
                    tint = PeraTheme.colors.link.primary
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickableNoRipple(onClick = onCopyAddressClick),
                    painter = painterResource(R.drawable.ic_copy),
                    contentDescription = stringResource(R.string.copy),
                    tint = PeraTheme.colors.text.main
                )
            }
        }
    )
}

interface JointAccountDetailScreenListener {
    fun onBackClick()
    fun onEditAddressClick(address: String)
    fun onCopyAddressClick(address: String)
    fun onIgnoreClick()
    fun onAddClick()
}
