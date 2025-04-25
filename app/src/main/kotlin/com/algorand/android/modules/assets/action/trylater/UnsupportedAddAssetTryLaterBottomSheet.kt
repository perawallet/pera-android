/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.assets.action.trylater

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.algorand.android.R
import com.algorand.android.modules.informationbottomsheet.ui.BaseInformationBottomSheet
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnsupportedAddAssetTryLaterBottomSheet : BaseInformationBottomSheet() {

    override fun initTitleTextView(titleTextView: TextView) {
        titleTextView.setText(R.string.your_accounts_don_t)
    }

    override fun initDescriptionTextView(descriptionTextView: TextView) {
        descriptionTextView.apply {
            text = getString(R.string.your_accounts_don_t_contain_this)
            setTextColor(ContextCompat.getColor(context, R.color.negative))
        }
    }

    override fun initNeutralButton(neutralButton: MaterialButton) {
        neutralButton.apply {
            setText(R.string.ok)
            setOnClickListener { navBack() }
        }
    }
}
