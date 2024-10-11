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

package com.algorand.android.modules.assetinbox.info.ui

import android.widget.TextView
import com.algorand.android.R
import com.algorand.android.modules.informationbottomsheet.ui.BaseInformationBottomSheet
import com.google.android.material.button.MaterialButton

class AssetInboxInfoBottomSheet : BaseInformationBottomSheet() {

    override fun initTitleTextView(titleTextView: TextView) {
        titleTextView.setText(R.string.asset_transfer_requests)
    }

    override fun initDescriptionTextView(descriptionTextView: TextView) {
        descriptionTextView.setText(R.string.this_feature_as_an_inbox)
    }

    override fun initNeutralButton(neutralButton: MaterialButton) {
        neutralButton.apply {
            setText(R.string.close)
            setOnClickListener { navBack() }
        }
    }
}
