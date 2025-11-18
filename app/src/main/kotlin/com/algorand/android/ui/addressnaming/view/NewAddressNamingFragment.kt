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

package com.algorand.android.ui.addressnaming.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.addressnaming.model.AddressNamingScreenConfig
import com.algorand.android.ui.addressnaming.viewmodel.DefaultAddressNamingViewModel
import com.algorand.android.ui.compose.extensions.createComposeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewAddressNamingFragment : BaseFragment(0), AddressNamingScreenListener {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    private val viewModel: DefaultAddressNamingViewModel by viewModels()

    private val args: NewAddressNamingFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            AddressNamingScreen(
                config = AddressNamingScreenConfig(R.string.finish_account_creation),
                listener = this,
                viewModel = viewModel
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(args.address)
    }

    override fun onNamingCompleted() {
        nav(NewAddressNamingFragmentDirections.actionNewAddressNamingFragmentToHomeNavigation())
    }
}
