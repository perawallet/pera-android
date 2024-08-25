/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register.createaccount.name

import androidx.navigation.fragment.navArgs
import com.algorand.android.models.CreateAccount
import com.algorand.android.ui.register.nameregistration.BaseNameRegistrationFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountNameRegistrationFragment : BaseNameRegistrationFragment() {

    override val accountCreation: CreateAccount
        get() = args.accountCreation

    private val args: CreateAccountNameRegistrationFragmentArgs by navArgs()

    override fun navToNextFragment() {
        nav(
            CreateAccountNameRegistrationFragmentDirections
                .actionCreateAccountNameRegistrationFragmentToCreateAccountResultInfoFragment()
        )
    }
}
