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

package com.algorand.android.modules.accountdetail.removeaccount.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.algorand.android.R
import com.algorand.android.utils.BaseDoubleButtonBottomSheet
import com.algorand.android.utils.setFragmentNavigationResult
import com.google.android.material.button.MaterialButton

class BackupDeleteConfirmationBottomSheet : BaseDoubleButtonBottomSheet() {

    override fun setTitleText(textView: TextView) {
        textView.setText(R.string.delete_from_cloud_backup_title)
    }

    override fun setDescriptionText(textView: TextView) {
        textView.setText(R.string.delete_from_cloud_backup_description)
    }

    override fun setAcceptButton(materialButton: MaterialButton) {
        materialButton.apply {
            setText(R.string.delete)
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.negative_lighter)
            setTextColor(ContextCompat.getColor(context, R.color.negative))
            setOnClickListener {
                setFragmentNavigationResult(BACKUP_DELETE_CONFIRMATION_KEY, true)
                navBack()
            }
        }
    }

    override fun setCancelButton(materialButton: MaterialButton) {
        materialButton.apply {
            setText(R.string.cancel)
            setOnClickListener {
                setFragmentNavigationResult(BACKUP_DELETE_CONFIRMATION_KEY, false)
                navBack()
            }
        }
    }

    override fun setIconImageView(imageView: ImageView) {
        imageView.apply {
            setImageResource(R.drawable.ic_cloud_no_connection)
            imageTintList = ContextCompat.getColorStateList(context, R.color.negative)
        }
    }

    companion object {
        const val BACKUP_DELETE_CONFIRMATION_KEY = "backup_delete_confirmation"
    }
}
