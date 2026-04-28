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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.backup.list.model.BackupListTab
import com.algorand.android.ui.backup.overview.DisableBackupConfirmationBottomSheet.Companion.DISABLE_BACKUP_CONFIRMATION_KEY
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.useFragmentResultListenerValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupOverviewFragment : BaseFragment(0), BackupOverviewScreenListener {

    private val viewModel: BackupOverviewViewModel by viewModels()

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.cloud_backup,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            BackupOverviewScreen(listener = this@BackupOverviewFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        useFragmentResultListenerValue<String>(DISABLE_BACKUP_CONFIRMATION_KEY) { actionName ->
            when (DisableBackupConfirmationBottomSheet.Action.valueOf(actionName)) {
                DisableBackupConfirmationBottomSheet.Action.KEEP_ENABLED -> Unit
                DisableBackupConfirmationBottomSheet.Action.DISABLE -> viewModel.disableBackup()
                DisableBackupConfirmationBottomSheet.Action.REMOVE -> viewModel.deleteBackup()
            }
        }
    }

    override fun onNavigateBack() {
        navBack()
    }

    override fun onDisableBackupClick() {
        nav(
            BackupOverviewFragmentDirections
                .actionBackupOverviewFragmentToDisableBackupConfirmationBottomSheet()
        )
    }

    override fun onEditAccountsClick(selectedTab: BackupListTab) {
        nav(
            BackupOverviewFragmentDirections
                .actionBackupOverviewFragmentToBackupAccountsListFragment(selectedTab)
        )
    }

    override fun onEditContactsClick(selectedTab: BackupListTab) {
        nav(
            BackupOverviewFragmentDirections
                .actionBackupOverviewFragmentToBackupContactsListFragment(selectedTab)
        )
    }

    override fun onCredentialAddressClick() {
        // TODO: navigate to credential address detail
    }

    override fun onSyncWithOtherDevicesClick() {
        // TODO: navigate to QR sync flow
    }
}
