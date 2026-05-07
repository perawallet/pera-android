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

package com.algorand.android.ui.backup.restore.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.algorand.android.R
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.ui.backup.BackupFileImporter
import com.algorand.android.ui.backup.model.BackupFile
import com.algorand.android.ui.backup.model.BackupFileImportResult
import com.algorand.android.ui.compose.theme.PeraTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RestoreBackupOptionsBottomSheet : BaseBottomSheet(layoutResId = 0) {

    @Inject
    lateinit var backupFileImporter: BackupFileImporter

    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@registerForActivityResult
        when (val result = backupFileImporter.importFile(requireContext(), uri)) {
            is BackupFileImportResult.Success -> navigateToPassphraseWithBackupFile(result.backupFile)
            is BackupFileImportResult.Error.FileUnreadable -> {
                showGlobalError(getString(R.string.the_file_could_not_be_read))
            }
            is BackupFileImportResult.Error.InvalidBackupFile -> {
                showGlobalError(getString(R.string.selected_file_is_not_valid_backup))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PeraTheme {
                    RestoreBackupOptionsScreen(
                        onCloseClick = ::navBack,
                        onScanQrClick = ::navigateToScanQr,
                        onImportFromDeviceClick = ::openFilePicker,
                        onEnterManuallyClick = ::navigateToManualRestore
                    )
                }
            }
        }
    }

    private fun openFilePicker() {
        openDocumentLauncher.launch(BackupFileImporter.ACCEPTED_MIME_TYPES)
    }

    private fun navigateToScanQr() {
        nav(
            RestoreBackupOptionsBottomSheetDirections
                .actionRestoreBackupOptionsBottomSheetToRestoreBackupQrScannerFragment()
        )
    }

    private fun navigateToManualRestore() {
        nav(
            RestoreBackupOptionsBottomSheetDirections
                .actionRestoreBackupOptionsBottomSheetToRestoreBackupPassphraseFragment()
        )
    }

    private fun navigateToPassphraseWithBackupFile(backupFile: BackupFile) {
        nav(
            RestoreBackupOptionsBottomSheetDirections
                .actionRestoreBackupOptionsBottomSheetToRestoreBackupPassphraseFragment(
                    encodedHash = backupFile.encodedHash,
                    backupAddress = backupFile.address
                )
        )
    }

}
