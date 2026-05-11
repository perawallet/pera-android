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
import android.view.View
import com.algorand.android.R
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.databinding.BottomSheetDisableBackupConfirmationBinding
import com.algorand.android.utils.setFragmentNavigationResult
import com.algorand.android.utils.viewbinding.viewBinding

class DisableBackupConfirmationBottomSheet : BaseBottomSheet(
    layoutResId = R.layout.bottom_sheet_disable_backup_confirmation
) {

    private val binding by viewBinding(BottomSheetDisableBackupConfirmationBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.keepEnabledButton.setOnClickListener { dismissWithResult(Action.KEEP_ENABLED) }
        binding.disableButton.setOnClickListener { dismissWithResult(Action.DISABLE) }
        binding.removeButton.setOnClickListener { dismissWithResult(Action.REMOVE) }
    }

    private fun dismissWithResult(action: Action) {
        setFragmentNavigationResult(DISABLE_BACKUP_CONFIRMATION_KEY, action.name)
        navBack()
    }

    enum class Action {
        KEEP_ENABLED,
        DISABLE,
        REMOVE
    }

    companion object {
        const val DISABLE_BACKUP_CONFIRMATION_KEY = "disable_backup_confirmation"
    }
}
