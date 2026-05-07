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

package com.algorand.android.ui.backup.credentials

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.backup.credentials.BackupCredentialsViewModel.ViewEvent
import com.algorand.android.ui.backup.overview.StoreBackupCredentialsBottomSheet
import com.algorand.android.ui.backup.overview.StoreBackupCredentialsBottomSheet.Companion.RESULT_KEY as STORE_CREDENTIALS_RESULT_KEY
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.disableScreenCapture
import com.algorand.android.utils.enableScreenCapture
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.useFragmentResultListenerValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupCredentialsFragment : BaseFragment(0) {

    private val viewModel: BackupCredentialsViewModel by viewModels()

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private var pendingFileContent: String? = null

    private val viewEventCollector: suspend (ViewEvent) -> Unit = { event ->
        when (event) {
            is ViewEvent.BackupFileReady -> {
                pendingFileContent = event.fileContent
                createDocumentLauncher.launch(event.fileName)
            }
            is ViewEvent.BackupFileCreationFailed -> {
                showGlobalError(getString(R.string.backup_credentials_load_error))
            }
        }
    }

    private val createDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument(MIME_TYPE_TEXT)
    ) { uri ->
        uri?.let { writeToFile(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            BackupCredentialsScreen(
                viewModel = viewModel,
                onCloseClick = ::navBack,
                onStoreCredentialsClick = ::navigateToStoreBackupCredentials
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectViewEvents()
        useFragmentResultListenerValue<String>(STORE_CREDENTIALS_RESULT_KEY) { optionName ->
            when (StoreBackupCredentialsBottomSheet.StorageOption.valueOf(optionName)) {
                StoreBackupCredentialsBottomSheet.StorageOption.THIS_DEVICE -> {
                    viewModel.exportBackupFile()
                }
                StoreBackupCredentialsBottomSheet.StorageOption.GOOGLE_DRIVE -> {
                    // TODO: Handle storing credentials on Google Drive
                }
            }
        }
    }

    private fun collectViewEvents() {
        collectLatestOnLifecycle(
            flow = viewModel.viewEvent,
            collection = viewEventCollector
        )
    }

    private fun writeToFile(uri: Uri) {
        val content = pendingFileContent ?: return
        pendingFileContent = null
        requireContext().contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(content.toByteArray(Charsets.UTF_8))
        }
    }

    private fun navigateToStoreBackupCredentials() {
        nav(
            BackupCredentialsFragmentDirections
                .actionBackupCredentialsFragmentToStoreBackupCredentialsBottomSheet()
        )
    }

    override fun onResume() {
        super.onResume()
        activity?.disableScreenCapture()
    }

    override fun onStop() {
        super.onStop()
        if (view?.hasWindowFocus() == true) {
            activity?.enableScreenCapture()
        }
    }

    private companion object {
        const val MIME_TYPE_TEXT = "text/plain"
    }
}
