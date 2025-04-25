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

package com.algorand.android.ui.rekeyedaccounts.hdkey.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentRekeyedAccountSelectionBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.rekeyedaccounts.view.RekeyedAccountSelectionFragmentDelegate
import com.algorand.android.ui.rekeyedaccounts.view.RekeyedAccountSelectionFragmentDelegate.RekeyedAccountSelectionListener
import com.algorand.android.ui.rekeyedaccounts.viewmodel.RekeyedAccountSelectionViewModel
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverHdKeyRekeyedAccountSelectionFragment : BaseFragment(R.layout.fragment_rekeyed_account_selection),
    RekeyedAccountSelectionListener {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )
    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val viewModel: RekeyedAccountSelectionViewModel by viewModels()

    private val binding by viewBinding(FragmentRekeyedAccountSelectionBinding::bind)

    private var fragmentDelegate: RekeyedAccountSelectionFragmentDelegate? = null

    private val args by navArgs<RecoverHdKeyRekeyedAccountSelectionFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentDelegate()
        viewModel.initializeViewState(args.rekeyedAccountSelectionNavArg.toList())
    }

    private fun initFragmentDelegate() {
        fragmentDelegate = RekeyedAccountSelectionFragmentDelegate(
            listener = this,
            viewModel = viewModel,
            fragment = this,
            binding = binding
        )
        fragmentDelegate?.init()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentDelegate = null
    }

    override fun navToNextScreen() {
        nav(
            RecoverHdKeyRekeyedAccountSelectionFragmentDirections
                .actionRecoverHdKeyRekeyedAccountSelectionFragmentToRecoverAccountResultInfoFragment()
        )
    }

    override fun navToAccountInformationBottomSheet(address: String) {
        nav(
            RecoverHdKeyRekeyedAccountSelectionFragmentDirections
                .actionRecoverHdKeyRekeyedAccountSelectionFragmentToRekeyedAccountInformationFragment(address)
        )
    }
}
