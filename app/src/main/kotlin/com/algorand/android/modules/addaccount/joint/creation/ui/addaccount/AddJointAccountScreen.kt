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

package com.algorand.android.modules.addaccount.joint.creation.ui.addaccount

import android.content.ClipboardManager
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.addaccount.joint.creation.model.JointAccountSelectionListItem
import com.algorand.android.modules.addaccount.joint.creation.ui.addaccount.viewmodel.AddJointAccountViewModel
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.AccountItemDisplayConfig
import com.algorand.android.ui.compose.widget.ContactIcon
import com.algorand.android.ui.compose.widget.PeraAccountItem
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.PeraToolbarTitle
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.textfield.PeraSlimTextField
import com.algorand.android.utils.getTextFromClipboard
import com.algorand.android.utils.toShortenedAddress

@Composable
fun AddJointAccountScreen(
    viewModel: AddJointAccountViewModel,
    listener: AddJointAccountScreenListener
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setHasClipboardContent(checkClipboardContent(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary)
    ) {
        ToolbarSection(onBackClick = listener::onBackClick)

        when (val state = viewState) {
            is AddJointAccountViewModel.ViewState.Loading -> {
                SearchBarSection(
                    searchQuery = "",
                    onSearchQueryChange = viewModel::onSearchQueryUpdate,
                    hasClipboardContent = false,
                    onPasteClick = { },
                    onQrScanClick = listener::onQrScanClick
                )
                Box(modifier = Modifier.fillMaxSize())
            }

            is AddJointAccountViewModel.ViewState.Content -> {
                SearchBarSection(
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryUpdate,
                    hasClipboardContent = state.hasClipboardContent,
                    onPasteClick = {
                        val clipboardText = context.getTextFromClipboard()
                        if (!clipboardText.isNullOrBlank()) {
                            viewModel.onSearchQueryUpdate(clipboardText)
                        }
                    },
                    onQrScanClick = listener::onQrScanClick
                )
                when {
                    state.showEmptyState -> EmptyStateSection()
                    else -> AccountListSection(
                        externalAddresses = state.externalAddresses,
                        accounts = state.accounts,
                        contacts = state.contacts,
                        nfds = state.nfds,
                        isCheckingJointAccount = state.isCheckingJointAccount,
                        listener = listener
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarSection(onBackClick: () -> Unit) {
    PeraToolbar(
        modifier = Modifier.padding(horizontal = 12.dp),
        centerContainer = {
            PeraToolbarTitle(text = stringResource(R.string.add_account))
        },
        startContainer = {
            PeraToolbarIcon(
                iconResId = R.drawable.ic_left_arrow,
                modifier = Modifier.clickableNoRipple(onClick = onBackClick)
            )
        }
    )
}

@Composable
private fun SearchBarSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    hasClipboardContent: Boolean,
    onPasteClick: () -> Unit,
    onQrScanClick: () -> Unit
) {
    PeraSlimTextField(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .background(
                color = PeraTheme.colors.layer.grayLighter,
                shape = RoundedCornerShape(4.dp)
            ),
        text = searchQuery,
        hint = stringResource(R.string.type_new_address_or_search),
        onTextChanged = onSearchQueryChange,
        startIconContainer = {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_search),
                tint = PeraTheme.colors.text.gray,
                contentDescription = stringResource(R.string.search)
            )
            Spacer(modifier = Modifier.width(8.dp))
        },
        endIconContainer = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (hasClipboardContent) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onPasteClick),
                        painter = painterResource(R.drawable.ic_clipboard),
                        tint = PeraTheme.colors.text.gray,
                        contentDescription = stringResource(R.string.paste_from_clipboard)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onQrScanClick),
                    painter = painterResource(R.drawable.ic_qr_scan),
                    tint = PeraTheme.colors.text.gray,
                    contentDescription = stringResource(R.string.scan_qr_code)
                )
            }
        }
    )
}

private fun checkClipboardContent(context: android.content.Context): Boolean {
    val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
    return clipboard?.primaryClip?.let { clipData ->
        clipData.itemCount > 0 && clipData.getItemAt(0)?.text?.isNotBlank() == true
    } ?: false
}

@Composable
private fun EmptyStateSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        contentAlignment = Alignment.TopStart
    ) {
        PeraBodyText(
            text = stringResource(R.string.no_accounts_found),
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun AccountListSection(
    externalAddresses: List<JointAccountSelectionListItem.ExternalAddressItem>,
    accounts: List<JointAccountSelectionListItem.AccountItem>,
    contacts: List<JointAccountSelectionListItem.ContactItem>,
    nfds: List<JointAccountSelectionListItem.NfdItem>,
    isCheckingJointAccount: Boolean,
    listener: AddJointAccountScreenListener
) {
    val onExternalAddressSelected: (String) -> Unit =
        if (isCheckingJointAccount) { _ -> } else listener::onExternalAddressSelected
    val onNfdSelected = if (isCheckingJointAccount) { _: String -> } else listener::onNfdSelected
    val onAccountSelected = if (isCheckingJointAccount) { _: String -> } else listener::onAccountSelected
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp)
    ) {
        externalAddressesSection(externalAddresses, onExternalAddressSelected)
        nfdsSection(nfds, onNfdSelected)
        accountsSection(accounts, onAccountSelected)
        contactsSection(contacts, onAccountSelected)
    }
}

private fun LazyListScope.externalAddressesSection(
    items: List<JointAccountSelectionListItem.ExternalAddressItem>,
    onSelect: (String) -> Unit
) {
    if (items.isEmpty()) return
    items(items = items, key = { "external_${it.address}" }) { item ->
        ExternalAddressSelectionItem(externalItem = item, onClick = { onSelect(item.address) })
    }
    item { Spacer(modifier = Modifier.height(24.dp)) }
}

private fun LazyListScope.nfdsSection(
    items: List<JointAccountSelectionListItem.NfdItem>,
    onSelect: (String) -> Unit
) {
    if (items.isEmpty()) return
    items(items = items, key = { "nfd_${it.address}" }) { item ->
        NfdSelectionItem(nfdItem = item, onClick = { onSelect(item.address) })
    }
    item { Spacer(modifier = Modifier.height(24.dp)) }
}

private fun LazyListScope.accountsSection(
    items: List<JointAccountSelectionListItem.AccountItem>,
    onSelect: (String) -> Unit
) {
    if (items.isEmpty()) return
    item {
        SectionHeader(text = stringResource(R.string.accounts))
        Spacer(modifier = Modifier.height(12.dp))
    }
    items(items = items, key = { "account_${it.address}" }) { item ->
        AccountSelectionItem(accountItem = item, onClick = { onSelect(item.address) })
    }
    item { Spacer(modifier = Modifier.height(24.dp)) }
}

private fun LazyListScope.contactsSection(
    items: List<JointAccountSelectionListItem.ContactItem>,
    onSelect: (String) -> Unit
) {
    if (items.isEmpty()) return
    item {
        SectionHeader(text = stringResource(R.string.contacts))
        Spacer(modifier = Modifier.height(12.dp))
    }
    items(items = items, key = { "contact_${it.address}" }) { item ->
        ContactSelectionItem(contactItem = item, onClick = { onSelect(item.address) })
    }
}

@Composable
private fun SectionHeader(text: String) {
    PeraBodyText(
        text = text,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun AccountSelectionItem(
    accountItem: JointAccountSelectionListItem.AccountItem,
    onClick: () -> Unit
) {
    PeraAccountItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        iconDrawablePreview = accountItem.iconDrawablePreview,
        displayName = AccountDisplayName(
            accountAddress = accountItem.address,
            primaryDisplayName = accountItem.displayName,
            secondaryDisplayName = accountItem.secondaryDisplayName
        ),
        displayConfig = AccountItemDisplayConfig(
            primaryValueText = accountItem.formattedAmount,
            secondaryValueText = accountItem.formattedCurrencyValue
        ),
        onAccountClick = { onClick() }
    )
}

@Composable
private fun ContactSelectionItem(
    contactItem: JointAccountSelectionListItem.ContactItem,
    onClick: () -> Unit
) {
    PeraAccountItem(
        modifier = Modifier.padding(vertical = 8.dp),
        displayName = AccountDisplayName(
            accountAddress = contactItem.address,
            primaryDisplayName = contactItem.displayName,
            secondaryDisplayName = contactItem.address.toShortenedAddress()
        ),
        onAccountClick = { onClick() },
        iconContent = {
            ContactIcon(
                imageUri = contactItem.imageUri,
                size = 40.dp
            )
        }
    )
}

@Composable
private fun NfdSelectionItem(
    nfdItem: JointAccountSelectionListItem.NfdItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactIcon(size = 40.dp)

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            PeraBodyText(
                text = nfdItem.domainName,
                color = PeraTheme.colors.text.main
            )
            PeraBodyText(
                text = nfdItem.address.toShortenedAddress(),
                color = PeraTheme.colors.text.grayLighter
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = PeraTheme.colors.button.square.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = stringResource(R.string.add_account),
                tint = PeraTheme.colors.button.square.icon
            )
        }
    }
}

@Composable
private fun ExternalAddressSelectionItem(
    externalItem: JointAccountSelectionListItem.ExternalAddressItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AccountIcon(
            modifier = Modifier.size(40.dp),
            iconDrawablePreview = externalItem.iconDrawablePreview
        )

        Spacer(modifier = Modifier.width(16.dp))

        PeraBodyText(
            text = externalItem.shortenedAddress,
            color = PeraTheme.colors.text.main,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = PeraTheme.colors.button.square.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = stringResource(R.string.add_account),
                tint = PeraTheme.colors.button.square.icon
            )
        }
    }
}

interface AddJointAccountScreenListener {
    fun onBackClick()
    fun onAccountSelected(address: String)
    fun onExternalAddressSelected(address: String)
    fun onNfdSelected(address: String)
    fun onQrScanClick()
}
