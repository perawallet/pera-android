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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.backup.list.model.BackupContactListItem
import com.algorand.android.ui.backup.list.model.BackupListTab
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.ContactIcon
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.utils.toShortenedAddress
import com.algorand.backup.contact.domain.model.ContactBackupPayload

@Composable
fun BackupContactsListScreen(
    viewModel: BackupContactsListViewModel = hiltViewModel()
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    BackupContactsListContent(
        selectedTab = viewState.selectedTab,
        items = if (viewState.selectedTab == BackupListTab.SYNCED) viewState.synced else viewState.notSynced,
        onTabSelected = viewModel::selectTab,
        onAddToLocalClick = viewModel::addContactToLocal
    )
}

@Composable
private fun BackupContactsListContent(
    selectedTab: BackupListTab,
    items: List<BackupContactListItem>,
    onTabSelected: (BackupListTab) -> Unit,
    onAddToLocalClick: (ContactBackupPayload) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        BackupTabSelector(selected = selectedTab, onTabSelected = onTabSelected)

        Spacer(modifier = Modifier.size(28.dp))

        BackupCountHeader(text = stringResource(R.string.contact_count, items.size))

        Spacer(modifier = Modifier.size(16.dp))

        LazyColumn {
            itemsIndexed(items, key = { _, item -> item.address }) { index, item ->
                ContactRow(
                    item = item,
                    showAddToLocal = selectedTab == BackupListTab.NOT_SYNCED,
                    onAddToLocalClick = onAddToLocalClick
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

@Composable
private fun ContactRow(
    item: BackupContactListItem,
    showAddToLocal: Boolean,
    onAddToLocalClick: (ContactBackupPayload) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactIcon(size = 40.dp)
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.name,
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            Text(
                text = item.address.toShortenedAddress(),
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.grayLighter
            )
        }
        if (showAddToLocal && item.payload != null) {
            Text(
                modifier = Modifier
                    .clickableNoRipple { onAddToLocalClick(item.payload) }
                    .padding(horizontal = 4.dp),
                text = stringResource(R.string.add_to_local),
                style = PeraTheme.typography.footnote.sansMedium,
                color = PeraTheme.colors.link.primary
            )
        }
    }
}
