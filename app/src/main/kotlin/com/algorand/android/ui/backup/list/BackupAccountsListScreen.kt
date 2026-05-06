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

package com.algorand.android.ui.backup.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.list.BackupAccountsListViewModel.ViewEvent
import com.algorand.android.ui.backup.list.model.BackupLocalAccountItem
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton

@Composable
fun BackupAccountsListScreen(
    listener: BackupAccountsListScreenListener,
    viewModel: BackupAccountsListViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                ViewEvent.BackUpSuccess -> listener.onBackUpSuccess()
            }
        }
    }

    BackupAccountsListContent(
        notBackedUpCount = viewState.notBackedUpCount,
        availableFromBackupCount = viewState.availableFromBackupCount,
        showReviewBanner = viewState.hasAccountsToReview,
        localAccounts = viewState.localAccounts,
        onReviewClick = listener::onReviewClick,
        onBackUpAccountClick = viewModel::backUpAccount
    )
}

@Composable
private fun BackupAccountsListContent(
    notBackedUpCount: Int,
    availableFromBackupCount: Int,
    showReviewBanner: Boolean,
    localAccounts: List<BackupLocalAccountItem>,
    onReviewClick: () -> Unit,
    onBackUpAccountClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        if (showReviewBanner) {
            item("review_banner") {
                AccountsToReviewBanner(
                    notBackedUpCount = notBackedUpCount,
                    availableFromBackupCount = availableFromBackupCount,
                    onReviewClick = onReviewClick
                )
                Spacer(modifier = Modifier.size(24.dp))
            }
        }

        item("section_header") {
            AccountsSectionHeader()
            Spacer(modifier = Modifier.size(8.dp))
        }

        items(localAccounts, key = { it.address }) { item ->
            AccountRow(
                item = item,
                onBackUpClick = { onBackUpAccountClick(item.address) }
            )
        }
    }
}

@Composable
private fun AccountsToReviewBanner(
    notBackedUpCount: Int,
    availableFromBackupCount: Int,
    onReviewClick: () -> Unit
) {
    val borderColor = PeraTheme.colors.testnet.background
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = borderColor.copy(alpha = 0.06f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = Color.White, shape = CircleShape)
                .border(width = 1.dp, color = borderColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_shield),
                tint = borderColor,
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.accounts_to_review),
                    style = PeraTheme.typography.body.large.sans,
                    color = PeraTheme.colors.text.main
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (notBackedUpCount > 0) {
                        BannerStatusRow(
                            iconRes = R.drawable.ic_cloud_no_connection,
                            iconTint = PeraTheme.colors.helper.negative,
                            text = stringResource(R.string.n_not_backed_up, notBackedUpCount)
                        )
                    }
                    if (availableFromBackupCount > 0) {
                        BannerStatusRow(
                            iconRes = R.drawable.ic_cloud_download,
                            iconTint = PeraTheme.colors.text.gray,
                            text = stringResource(
                                R.string.n_available_from_backup,
                                availableFromBackupCount
                            )
                        )
                    }
                }
            }
            PeraPrimaryButton(
                onClick = onReviewClick,
                text = stringResource(R.string.review)
            )
        }
    }
}

@Composable
private fun BannerStatusRow(
    iconRes: Int,
    iconTint: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(iconRes),
            tint = iconTint,
            contentDescription = null
        )
        Text(
            text = text,
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun AccountsSectionHeader() {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.accounts),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Text(
            text = stringResource(R.string.available_on_this_device),
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun AccountRow(
    item: BackupLocalAccountItem,
    onBackUpClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AccountIcon(
            modifier = Modifier.size(40.dp),
            iconDrawablePreview = item.iconPreview
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.displayName,
                    style = PeraTheme.typography.body.regular.sans,
                    color = PeraTheme.colors.text.main
                )
                if (item.isBackedUp) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_cloud_check),
                        tint = PeraTheme.colors.helper.positive,
                        contentDescription = null
                    )
                }
            }
            val subtitle = formatAccountSubtitle(item)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.grayLighter
                )
            }
        }
        if (!item.isBackedUp) {
            Spacer(modifier = Modifier.width(12.dp))
            PeraPrimaryButton(
                onClick = onBackUpClick,
                text = stringResource(R.string.back_up)
            )
        }
    }
}

@Composable
private fun formatAccountSubtitle(item: BackupLocalAccountItem): String? {
    val assets = item.assetCount?.let { stringResource(R.string.n_assets, it) }
    return when {
        assets != null && item.formattedBalance != null -> "$assets · ${item.formattedBalance}"
        assets != null -> assets
        item.formattedBalance != null -> item.formattedBalance
        else -> null
    }
}

interface BackupAccountsListScreenListener {
    fun onReviewClick()
    fun onBackUpSuccess()
}
