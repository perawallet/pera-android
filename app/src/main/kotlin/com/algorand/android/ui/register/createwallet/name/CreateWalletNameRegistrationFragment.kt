/*
 *  Copyright 2022-2025 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register.createwallet.name

import android.os.Bundle
import android.view.View
import com.algorand.android.R
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.tracking.core.PeraEvent
import com.algorand.android.ui.register.nameregistration.BaseNameRegistrationFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateWalletNameRegistrationFragment : BaseNameRegistrationFragment() {

    override val accountCreation: AccountCreation?
        get() = null

    override fun navToNextFragment() {
        nameRegistrationViewModel.logEvent(PeraEvent.ONBOARDING_NAME_WALLET_COMPLETE)
        nav(
            CreateWalletNameRegistrationFragmentDirections
                .actionCreateWalletNameRegistrationFragmentToHomeNavigation()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.labelTextView.setText(R.string.name_your_wallet)
        binding.descriptionTextView.setText(R.string.name_your_wallet_to)
    }
}
