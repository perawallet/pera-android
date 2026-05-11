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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.utils.setFragmentNavigationResult

class StoreBackupCredentialsBottomSheet : BaseBottomSheet(layoutResId = 0) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PeraTheme {
                    StoreBackupCredentialsScreen(
                        onCloseClick = ::navBack,
                        onThisDeviceClick = { dismissWithResult(StorageOption.THIS_DEVICE) },
                        onGoogleDriveClick = { dismissWithResult(StorageOption.GOOGLE_DRIVE) }
                    )
                }
            }
        }
    }

    private fun dismissWithResult(option: StorageOption) {
        setFragmentNavigationResult(RESULT_KEY, option.name)
        navBack()
    }

    enum class StorageOption {
        THIS_DEVICE,
        GOOGLE_DRIVE
    }

    companion object {
        const val RESULT_KEY = "storeBackupCredentialsResultKey"
    }
}
