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

@file:Suppress("MagicNumber")

package com.algorand.android.modules.addaccount.joint.creation.ui.createaccount

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.modules.addaccount.joint.creation.ui.createaccount.viewmodel.CreateJointAccountViewModel
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.ContactIcon
import com.algorand.android.ui.compose.widget.PeraAccountItem
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraTitleText
import com.algorand.android.utils.toShortenedAddress

@Composable
fun CreateJointAccountScreen(
    viewModel: CreateJointAccountViewModel,
    listener: CreateJointAccountScreenListener
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val selectedAccounts = viewState.selectedAccounts
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ToolbarSection(listener = listener)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            ContentSection(
                selectedAccounts = selectedAccounts,
                viewModel = viewModel,
                listener = listener
            )
        }
        ContinueButtonSection(
            isContinueEnabled = viewState.isContinueEnabled,
            onContinueClick = listener::onContinueClick
        )
    }
}

@Composable
private fun ToolbarSection(listener: CreateJointAccountScreenListener) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        PeraToolbarIcon(
            iconResId = R.drawable.ic_left_arrow,
            modifier = Modifier.clickableNoRipple(onClick = listener::onBackClick)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.create_joint_account),
            style = PeraTheme.typography.title.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun ContentSection(
    selectedAccounts: List<SelectedJointAccountItem>,
    viewModel: CreateJointAccountViewModel,
    listener: CreateJointAccountScreenListener
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        DescriptionSection()
        Spacer(modifier = Modifier.height(32.dp))
        AccountsSection(
            selectedAccounts = selectedAccounts,
            viewModel = viewModel,
            listener = listener
        )
        Spacer(modifier = Modifier.height(8.dp))
        AddAccountButtonSection(onAddAccountClick = listener::onAddAccountClick)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DescriptionSection() {
    PeraBodyText(
        text = stringResource(R.string.create_joint_account_description),
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun AccountsSection(
    selectedAccounts: List<SelectedJointAccountItem>,
    viewModel: CreateJointAccountViewModel,
    listener: CreateJointAccountScreenListener
) {
    PeraTitleText(
        text = stringResource(R.string.accounts)
    )
    Spacer(modifier = Modifier.height(4.dp))
    PeraBodyText(
        text = stringResource(R.string.joint_account_min_accounts_required),
        color = PeraTheme.colors.text.gray
    )
    Spacer(modifier = Modifier.height(16.dp))
    selectedAccounts.forEach { account ->
        SelectedAccountItem(
            account = account,
            onEditClick = { handleContactEditClick(account, listener) },
            onRemoveClick = { handleAccountRemoveClick(account, viewModel) }
        )
    }
}

private fun handleContactEditClick(
    account: SelectedJointAccountItem,
    listener: CreateJointAccountScreenListener
) {
    listener.onEditAccountClick(account.accountDisplayName.accountAddress)
}

private fun handleAccountRemoveClick(
    account: SelectedJointAccountItem,
    viewModel: CreateJointAccountViewModel,
) {
    viewModel.removeSelectedAccount(account.accountDisplayName.accountAddress)
}

@Composable
private fun AddAccountButtonSection(onAddAccountClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAddAccountClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = stringResource(id = R.string.add_account),
            tint = PeraTheme.colors.helper.positive
        )
        Spacer(modifier = Modifier.width(8.dp))
        PeraBodyText(
            text = stringResource(id = R.string.add_account),
            color = PeraTheme.colors.helper.positive
        )
    }
}

@Composable
private fun ContinueButtonSection(
    isContinueEnabled: Boolean,
    onContinueClick: () -> Unit
) {
    PeraPrimaryButton(
        text = stringResource(R.string.continue_text),
        onClick = onContinueClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        state = if (isContinueEnabled) PeraButtonState.ENABLED else PeraButtonState.DISABLED
    )
}

@Composable
private fun SelectedAccountItem(
    account: SelectedJointAccountItem,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val displayName = account.accountDisplayName
    PeraAccountItem(
        modifier = Modifier.padding(vertical = 12.dp),
        displayName = displayName.copy(
            secondaryDisplayName = displayName.secondaryDisplayName
                ?: displayName.accountAddress.toShortenedAddress()
        ),
        canCopyable = false,
        iconContent = {
            when {
                account.isContact -> {
                    ContactIcon(imageUri = account.imageUri, size = 40.dp)
                }
                account.iconDrawablePreview != null -> {
                    AccountIcon(
                        modifier = Modifier.size(40.dp),
                        iconDrawablePreview = account.iconDrawablePreview
                    )
                }
                else -> ContactIcon(size = 40.dp)
            }
        },
        trailingContent = {
            if (account.isContact) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_pen),
                        contentDescription = stringResource(R.string.edit_contact),
                        tint = PeraTheme.colors.button.square.icon
                    )
                }
            } else {
                IconButton(onClick = onRemoveClick) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = stringResource(R.string.remove),
                        tint = PeraTheme.colors.text.gray
                    )
                }
            }
        }
    )
}

interface CreateJointAccountScreenListener {
    fun onBackClick()
    fun onAddAccountClick()
    fun onEditAccountClick(address: String)
    fun onContinueClick()
}
