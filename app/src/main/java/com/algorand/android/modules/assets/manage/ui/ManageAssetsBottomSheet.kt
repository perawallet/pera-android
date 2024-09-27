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

package com.algorand.android.modules.assets.manage.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseBottomSheet
import com.algorand.android.databinding.BottomSheetManageAssetsBinding
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageAssetsBottomSheet : DaggerBaseBottomSheet(
    layoutResId = R.layout.bottom_sheet_manage_assets,
    fullPageNeeded = false,
    firebaseEventScreenId = null
) {

    private val binding by viewBinding(BottomSheetManageAssetsBinding::bind)
    private val manageAssetsViewModel by viewModels<ManageAssetsViewModel>()

    private val accountDetailObserver: suspend (AccountDetail?) -> Unit = {
        initAccountDetail(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        manageAssetsViewModel.initAccountDetail()
    }

    private fun initUi() {
        configureToolbar()
        configureSortAssetsButton()
        configureFilterAssetsButton()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            manageAssetsViewModel.accountDetailFlow,
            accountDetailObserver
        )
    }

    private fun configureToolbar() {
        val toolbarConfiguration = ToolbarConfiguration(titleResId = R.string.manage_assets)
        binding.customToolbar.configure(toolbarConfiguration)
    }

    private fun initAccountDetail(accountDetail: AccountDetail?) {
        with(binding.removeAssetsButton) {
            setOnClickListener { navToRemoveAssetsFragment() }
            isVisible = accountDetail?.accountType?.canSignTransaction() == true
        }
    }

    private fun configureSortAssetsButton() {
        binding.sortAssetsButton.setOnClickListener { navToSortAssetsFragment() }
    }

    private fun configureFilterAssetsButton() {
        binding.filterAssetsButton.setOnClickListener { navToFilterAssetNavigation() }
    }

    private fun navToSortAssetsFragment() {
        nav(ManageAssetsBottomSheetDirections.actionManageAssetsBottomSheetToAssetSortPreferenceFragment())
    }

    private fun navToRemoveAssetsFragment() {
        nav(
            ManageAssetsBottomSheetDirections.actionManageAssetsBottomSheetToRemoveAssetsFragment(
                publicKey = manageAssetsViewModel.publicKey
            )
        )
    }

    private fun navToFilterAssetNavigation() {
        nav(ManageAssetsBottomSheetDirections.actionManageAssetsBottomSheetToAssetFilterNavigation())
    }
}
