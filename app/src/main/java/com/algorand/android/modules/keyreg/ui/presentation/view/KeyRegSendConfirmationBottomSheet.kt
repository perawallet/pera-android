/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.keyreg.ui.presentation.view

import android.widget.ImageView
import android.widget.TextView
import com.algorand.android.R
import com.algorand.android.utils.BaseDoubleButtonBottomSheet
import com.algorand.android.utils.setFragmentNavigationResult
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KeyRegSendConfirmationBottomSheet : BaseDoubleButtonBottomSheet() {

    override fun setTitleText(textView: TextView) {
        textView.setText(R.string.warning)
    }

    override fun setDescriptionText(textView: TextView) {
        textView.text = ("TODO SOME CONFIRMATION TEXT")
    }

    override fun setAcceptButton(materialButton: MaterialButton) {
        materialButton.apply {
            setText(R.string.confirm)
            setOnClickListener {
                navBack()
                setFragmentNavigationResult(KEY_REG_CONFIRMATION_RESULT_KEY, true)
            }
        }
    }

    override fun setCancelButton(materialButton: MaterialButton) {
        materialButton.apply {
            setText(R.string.cancel)
            setOnClickListener {
                navBack()
            }
        }
    }

    override fun setIconImageView(imageView: ImageView) {
        imageView.setImageResource(R.drawable.ic_error_orange)
    }

    companion object {
        const val KEY_REG_CONFIRMATION_RESULT_KEY = "keyRegConfirmationResultKey"
    }
}
