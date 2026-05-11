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

package com.algorand.android.modules.tutorialdialog.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import com.algorand.android.R
import com.algorand.android.databinding.DialogBackupTutorialBinding
import com.algorand.android.utils.getDisplaySize
import com.algorand.android.utils.viewbinding.viewBinding

class BackupTutorialDialog private constructor(context: Context) : Dialog(context) {

    private val binding = viewBinding(DialogBackupTutorialBinding::inflate)

    var onContinueClick: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        updateDialogWindow()
        updateDialogDecorView()
        bindFeatureSteps()
        bindContinueButton()
    }

    private fun updateDialogWindow() {
        window?.apply {
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
            attributes = attributes?.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            setBackgroundDrawableResource(R.drawable.bg_tutorial_dialog)
            setDimAmount(DIALOG_DIM_AMOUNT)
        }
    }

    private fun updateDialogDecorView() {
        window?.decorView?.apply {
            val displayMetrics = DisplayMetrics().apply { context.getDisplaySize() }
            minimumWidth = displayMetrics.widthPixels
        }
    }

    private fun bindFeatureSteps() {
        with(binding.featureSecureEncryption) {
            stepNumberTextView.text = STEP_ONE
            stepTitleTextView.setText(R.string.backup_tutorial_feature_secure_encryption_title)
            stepDescriptionTextView.setText(R.string.backup_tutorial_feature_secure_encryption_description)
        }
        with(binding.featureMultiDeviceSync) {
            stepNumberTextView.text = STEP_TWO
            stepTitleTextView.setText(R.string.backup_tutorial_feature_multi_device_sync_title)
            stepDescriptionTextView.setText(R.string.backup_tutorial_feature_multi_device_sync_description)
        }
        with(binding.featureAccountRecovery) {
            stepNumberTextView.text = STEP_THREE
            stepTitleTextView.setText(R.string.backup_tutorial_feature_account_recovery_title)
            stepDescriptionTextView.setText(R.string.backup_tutorial_feature_account_recovery_description)
        }
    }

    private fun bindContinueButton() {
        binding.primaryDialogButton.setOnClickListener {
            onContinueClick?.invoke()
            dismiss()
        }
    }

    companion object {
        private const val DIALOG_DIM_AMOUNT = 0.3f
        private const val STEP_ONE = "1"
        private const val STEP_TWO = "2"
        private const val STEP_THREE = "3"

        fun create(context: Context): BackupTutorialDialog {
            return BackupTutorialDialog(context)
        }
    }
}
