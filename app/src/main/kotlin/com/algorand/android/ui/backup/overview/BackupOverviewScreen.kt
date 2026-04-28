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

package com.algorand.android.ui.backup.overview

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.list.model.BackupListTab
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewEvent
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewState
import com.algorand.android.ui.backup.overview.BackupOverviewViewModel.ViewState.LatestSyncState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@Composable
fun BackupOverviewScreen(
    listener: BackupOverviewScreenListener,
    viewModel: BackupOverviewViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                ViewEvent.NavigateBack -> listener.onNavigateBack()
            }
        }
    }

    BackupOverviewContent(viewState = viewState, listener = listener)
}

@Composable
private fun BackupOverviewContent(
    viewState: ViewState,
    listener: BackupOverviewScreenListener
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        StatusSection(
            credentialAddress = viewState.credentialAddress,
            latestSync = viewState.latestSync,
            onCredentialAddressClick = listener::onCredentialAddressClick
        )

        SectionContainer(titleRes = R.string.protected_data) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProtectedDataRow(
                    iconRes = R.drawable.ic_wallet,
                    titleRes = R.string.accounts,
                    description = stringResource(R.string.accounts_in_sync, viewState.accountCount),
                    notSyncedCount = viewState.notSyncedAccountCount,
                    notSyncedTextRes = R.string.accounts_not_synced,
                    onEditClick = { listener.onEditAccountsClick(BackupListTab.SYNCED) },
                    onNotSyncedClick = { listener.onEditAccountsClick(BackupListTab.NOT_SYNCED) }
                )
                ProtectedDataRow(
                    iconRes = R.drawable.ic_contacts,
                    titleRes = R.string.contacts,
                    description = stringResource(R.string.contacts_in_sync, viewState.contactCount),
                    notSyncedCount = viewState.notSyncedContactCount,
                    notSyncedTextRes = R.string.contacts_not_synced,
                    onEditClick = { listener.onEditContactsClick(BackupListTab.SYNCED) },
                    onNotSyncedClick = { listener.onEditContactsClick(BackupListTab.NOT_SYNCED) }
                )
            }
        }

        SectionContainer(titleRes = R.string.backup_settings) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BorderedNavigationRow(
                    iconRes = R.drawable.ic_qr,
                    title = stringResource(R.string.sync_with_other_devices),
                    description = stringResource(R.string.sync_with_other_devices_description),
                    showChevron = true,
                    onClick = listener::onSyncWithOtherDevicesClick
                )
                BorderedNavigationRow(
                    iconRes = R.drawable.ic_cloud_no_connection,
                    title = stringResource(R.string.disable_cloud_backup),
                    description = stringResource(R.string.disable_cloud_backup_description),
                    iconTint = PeraTheme.colors.helper.negative,
                    titleColor = PeraTheme.colors.helper.negative,
                    onClick = listener::onDisableBackupClick
                )
            }
        }
    }
}

@Composable
private fun StatusSection(
    credentialAddress: String,
    latestSync: LatestSyncState?,
    onCredentialAddressClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BorderedNavigationRow(
            iconRes = R.drawable.ic_key,
            title = stringResource(R.string.credential_address),
            description = credentialAddress,
            showChevron = true,
            showInfoIcon = true,
            onClick = onCredentialAddressClick
        )
        if (latestSync != null) {
            LatestSyncRow(latestSync = latestSync)
        }
    }
}

@Composable
private fun SectionContainer(
    @StringRes titleRes: Int,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
            text = stringResource(titleRes),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        content()
    }
}

@Composable
private fun BorderedNavigationRow(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
    showChevron: Boolean = false,
    showInfoIcon: Boolean = false,
    iconTint: Color = PeraTheme.colors.text.main,
    titleColor: Color = PeraTheme.colors.text.main
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickableNoRipple(onClick = onClick)
            .border(
                width = 1.dp,
                color = PeraTheme.colors.layer.gray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(iconRes),
            tint = iconTint,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = PeraTheme.typography.body.regular.sansMedium,
                    color = titleColor
                )
                if (showInfoIcon) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_info),
                        tint = PeraTheme.colors.text.gray,
                        contentDescription = null
                    )
                }
            }
            Text(
                text = description,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
        if (showChevron) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_right_arrow),
                tint = PeraTheme.colors.text.gray,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun LatestSyncRow(latestSync: LatestSyncState) {
    val isSuccess = latestSync.isSuccess
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = PeraTheme.colors.layer.gray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(
                if (isSuccess) R.drawable.ic_cloud_check else R.drawable.ic_cloud_failed
            ),
            tint = if (isSuccess) PeraTheme.colors.link.primary else PeraTheme.colors.helper.negative,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.latest_sync),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main
            )
            Text(
                text = latestSync.formattedTimestamp,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
        SyncOutcomeBadge(isSuccess = isSuccess)
    }
}

@Composable
private fun SyncOutcomeBadge(isSuccess: Boolean) {
    val backgroundColor = if (isSuccess) {
        PeraTheme.colors.helper.positiveLighter
    } else {
        PeraTheme.colors.helper.negativeLighter
    }
    val textColor = if (isSuccess) {
        PeraTheme.colors.helper.positive
    } else {
        PeraTheme.colors.helper.negative
    }
    val labelRes = if (isSuccess) R.string.backup_status_success else R.string.backup_status_failed
    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(labelRes),
            style = PeraTheme.typography.caption.sansBold,
            color = textColor
        )
    }
}

@Composable
private fun ProtectedDataRow(
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    description: String,
    notSyncedCount: Int,
    @StringRes notSyncedTextRes: Int,
    onEditClick: () -> Unit,
    onNotSyncedClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PeraTheme.colors.layer.grayLighter,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(iconRes),
                tint = PeraTheme.colors.text.main,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(titleRes),
                        style = PeraTheme.typography.body.large.sansMedium,
                        color = PeraTheme.colors.text.main
                    )
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickableNoRipple(onClick = onEditClick)
                            .padding(horizontal = 4.dp),
                        text = stringResource(R.string.edit),
                        style = PeraTheme.typography.body.regular.sansMedium,
                        color = PeraTheme.colors.link.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = PeraTheme.typography.body.regular.sans,
                    color = PeraTheme.colors.text.gray
                )
            }
        }
        if (notSyncedCount > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                color = PeraTheme.colors.layer.gray,
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            NotSyncedRow(
                text = stringResource(notSyncedTextRes, notSyncedCount),
                onClick = onNotSyncedClick
            )
        }
    }
}

@Composable
private fun NotSyncedRow(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickableNoRipple(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_cloud_no_connection),
            tint = PeraTheme.colors.helper.negative,
            contentDescription = null
        )
        Text(
            text = text,
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.text.gray
        )
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(R.drawable.ic_right_arrow),
            tint = PeraTheme.colors.text.gray,
            contentDescription = null
        )
    }
}

interface BackupOverviewScreenListener {
    fun onNavigateBack()
    fun onDisableBackupClick()
    fun onEditAccountsClick(selectedTab: BackupListTab)
    fun onEditContactsClick(selectedTab: BackupListTab)
    fun onCredentialAddressClick()
    fun onSyncWithOtherDevicesClick()
}
