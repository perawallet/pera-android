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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.accountdetail.removeaccount.ui.BackupDeleteConfirmationBottomSheet.Companion.BACKUP_DELETE_CONFIRMATION_KEY
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.useFragmentResultListenerValue
import com.algorand.backup.account.domain.model.AddressBackupPayload
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupAccountsReviewFragment : BaseFragment(0), BackupAccountsReviewScreenListener {

    private val viewModel: BackupAccountsReviewViewModel by viewModels()

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.accounts_to_review,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            BackupAccountsReviewScreen(listener = this@BackupAccountsReviewFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        useFragmentResultListenerValue<Boolean>(BACKUP_DELETE_CONFIRMATION_KEY) { confirmed ->
            if (confirmed) {
                viewModel.confirmRemoveFromBackup()
            } else {
                viewModel.cancelRemoveFromBackup()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onRemoveFromBackupClick(payload: AddressBackupPayload) {
        nav(
            BackupAccountsReviewFragmentDirections
                .actionBackupAccountsReviewFragmentToBackupDeleteConfirmationBottomSheet()
        )
    }

    override fun onBackUpSuccess() {
        showAlertSuccess(title = getString(R.string.account_has_been_backed_up))
    }

    override fun onAddSuccess() {
        showAlertSuccess(title = getString(R.string.account_has_been_added))
    }

    override fun onNavigateBackWithSuccess() {
        showAlertSuccess(title = getString(R.string.account_has_been_added), tag = baseActivityTag)
        navBack()
    }

    override fun onNavigateBack() {
        navBack()
    }

    override fun onShowImportError(message: String?) {
        showGlobalError(
            errorMessage = message ?: getString(R.string.an_error_occurred),
            title = getString(R.string.an_error_occurred)
        )
    }
}
