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

package com.algorand.android.ui.settings.developeroptions.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.compose.extensions.createComposeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeveloperOptionsFragment : BaseFragment(0) {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            DeveloperOptionsScreen(::navBack, ::navigateToOverrideFeatureFlags)
        }
    }

    private fun navigateToOverrideFeatureFlags() {
        nav(DeveloperOptionsFragmentDirections.actionDeveloperOptionsFragmentToOverrideFeatureFlagsFragment())
    }
}
