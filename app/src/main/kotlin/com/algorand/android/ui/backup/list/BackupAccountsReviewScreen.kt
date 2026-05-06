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

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.list.BackupAccountsReviewViewModel.ViewEvent
import com.algorand.android.ui.backup.list.model.BackupAccountListItem
import com.algorand.android.ui.backup.list.model.BackupLocalAccountItem
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.backup.account.domain.model.AddressBackupPayload

@Composable
fun BackupAccountsReviewScreen(
    listener: BackupAccountsReviewScreenListener,
    viewModel: BackupAccountsReviewViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                ViewEvent.AddSuccess -> listener.onAddSuccess()
                ViewEvent.BackUpSuccess -> listener.onBackUpSuccess()
                ViewEvent.NavigateBackWithSuccess -> listener.onNavigateBackWithSuccess()
                ViewEvent.NavigateBack -> listener.onNavigateBack()
                is ViewEvent.ShowImportError -> listener.onShowImportError(event.message)
            }
        }
    }

    BackupAccountsReviewContent(
        notBackedUp = viewState.notBackedUp,
        availableFromBackup = viewState.availableFromBackup,
        isAddFromBackupExpanded = viewState.isAddFromBackupExpanded,
        onToggleAddFromBackup = viewModel::toggleAddFromBackupExpanded,
        onAddFromBackupClick = viewModel::addAccountToLocal,
        onRemoveFromBackupClick = { payload ->
            viewModel.requestRemoveFromBackup(payload)
            listener.onRemoveFromBackupClick(payload)
        },
        onBackUpAccountClick = viewModel::backUpAccount
    )
}

@Composable
private fun BackupAccountsReviewContent(
    notBackedUp: List<BackupLocalAccountItem>,
    availableFromBackup: List<BackupAccountListItem>,
    isAddFromBackupExpanded: Boolean,
    onToggleAddFromBackup: () -> Unit,
    onAddFromBackupClick: (AddressBackupPayload) -> Unit,
    onRemoveFromBackupClick: (AddressBackupPayload) -> Unit,
    onBackUpAccountClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        if (availableFromBackup.isNotEmpty()) {
            item("add_from_backup") {
                AddFromBackupCard(
                    items = availableFromBackup,
                    isExpanded = isAddFromBackupExpanded,
                    onToggle = onToggleAddFromBackup,
                    onAddClick = onAddFromBackupClick,
                    onRemoveClick = onRemoveFromBackupClick
                )
            }
        }
        if (notBackedUp.isNotEmpty()) {
            item("not_backed_up") {
                NotBackedUpSection(
                    items = notBackedUp,
                    onBackUpClick = onBackUpAccountClick
                )
            }
        }
    }
}

@Composable
private fun AddFromBackupCard(
    items: List<BackupAccountListItem>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onAddClick: (AddressBackupPayload) -> Unit,
    onRemoveClick: (AddressBackupPayload) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = PeraTheme.colors.layer.gray,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        SectionHeaderRow(
            title = stringResource(R.string.add_from_backup),
            count = items.size,
            subtitle = stringResource(R.string.add_from_backup_subtitle),
            modifier = Modifier
                .clickableNoRipple(onClick = onToggle)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            trailing = {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (isExpanded) 90f else 0f),
                    painter = painterResource(R.drawable.ic_right_arrow),
                    tint = PeraTheme.colors.text.gray,
                    contentDescription = null
                )
            }
        )

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                items.forEachIndexed { index, item ->
                    BackupItemRow(
                        item = item,
                        onAddClick = { item.payload?.let(onAddClick) },
                        onRemoveClick = { item.payload?.let(onRemoveClick) }
                    )
                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 56.dp),
                            color = PeraTheme.colors.layer.grayLighter,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotBackedUpSection(
    items: List<BackupLocalAccountItem>,
    onBackUpClick: (String) -> Unit
) {
    Column {
        SectionHeaderRow(
            title = stringResource(R.string.not_backed_up),
            count = items.size,
            subtitle = stringResource(R.string.not_backed_up_subtitle)
        )
        Spacer(modifier = Modifier.size(8.dp))
        items.forEach { item ->
            NotBackedUpRow(
                item = item,
                onBackUpClick = { onBackUpClick(item.address) }
            )
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    count: Int,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildSectionTitle(title = title, count = count),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main
            )
            Text(
                text = subtitle,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        }
    }
}

@Composable
private fun buildSectionTitle(title: String, count: Int) = buildAnnotatedString {
    append(title)
    append(' ')
    withStyle(SpanStyle(color = PeraTheme.colors.text.gray)) {
        append("($count)")
    }
}

@Composable
private fun BackupItemRow(
    item: BackupAccountListItem,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        AccountIcon(
            modifier = Modifier.size(40.dp),
            iconDrawablePreview = item.iconPreview
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.CenterStart
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
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_cloud_check),
                        tint = PeraTheme.colors.helper.positive,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            PeraSecondaryButton(
                onClick = onAddClick,
                text = stringResource(R.string.add),
                cornerRadius = 8.dp,
                leftIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_plus),
                        tint = PeraTheme.colors.text.main,
                        contentDescription = null
                    )
                }
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        SquareIconButton(
            iconRes = R.drawable.ic_trash,
            iconTint = PeraTheme.colors.helper.negative,
            backgroundColor = PeraTheme.colors.helper.negativeLighter,
            onClick = onRemoveClick
        )
    }
}

@Composable
private fun NotBackedUpRow(
    item: BackupLocalAccountItem,
    onBackUpClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
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
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_cloud_no_connection),
                    tint = PeraTheme.colors.helper.negative,
                    contentDescription = null
                )
            }
            val subtitle = formatNotBackedUpSubtitle(item)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.grayLighter
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        PeraPrimaryButton(
            onClick = onBackUpClick,
            text = stringResource(R.string.back_up)
        )
    }
}

@Composable
private fun formatNotBackedUpSubtitle(item: BackupLocalAccountItem): String? {
    val assets = item.assetCount?.let { stringResource(R.string.n_assets, it) }
    return when {
        assets != null && item.formattedBalance != null -> "$assets · ${item.formattedBalance}"
        assets != null -> assets
        item.formattedBalance != null -> item.formattedBalance
        else -> null
    }
}

@Composable
private fun SquareIconButton(
    @DrawableRes iconRes: Int,
    iconTint: androidx.compose.ui.graphics.Color,
    backgroundColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = backgroundColor)
            .clickableNoRipple(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(iconRes),
            tint = iconTint,
            contentDescription = null
        )
    }
}

interface BackupAccountsReviewScreenListener {
    fun onRemoveFromBackupClick(payload: AddressBackupPayload)
    fun onBackUpSuccess()
    fun onAddSuccess()
    fun onNavigateBackWithSuccess()
    fun onNavigateBack()
    fun onShowImportError(message: String?)
}
