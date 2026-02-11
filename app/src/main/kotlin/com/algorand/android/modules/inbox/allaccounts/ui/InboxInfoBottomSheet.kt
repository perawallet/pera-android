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

package com.algorand.android.modules.inbox.allaccounts.ui

import android.widget.TextView
import com.algorand.android.R
import com.algorand.android.modules.informationbottomsheet.ui.BaseInformationBottomSheet
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InboxInfoBottomSheet : BaseInformationBottomSheet() {

    @Inject
    lateinit var isFeatureToggleEnabled: IsFeatureToggleEnabled

    private val isJointAccountEnabled: Boolean
        get() = isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)

    override fun initTitleTextView(titleTextView: TextView) {
        val titleResId = if (isJointAccountEnabled) {
            R.string.inbox
        } else {
            R.string.asset_transfer_request
        }
        titleTextView.setText(titleResId)
    }

    override fun initDescriptionTextView(descriptionTextView: TextView) {
        val descriptionResId = if (isJointAccountEnabled) {
            R.string.this_feature_as_an_inbox
        } else {
            R.string.this_feature_as_an_inbox_asset_only
        }
        descriptionTextView.setText(descriptionResId)
    }

    override fun initNeutralButton(neutralButton: MaterialButton) {
        neutralButton.apply {
            setText(R.string.close)
            setOnClickListener { navBack() }
        }
    }
}
