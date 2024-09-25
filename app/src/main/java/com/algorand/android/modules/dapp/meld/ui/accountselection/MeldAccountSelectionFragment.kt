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

package com.algorand.android.modules.dapp.meld.ui.accountselection

import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.accountselection.BaseAccountSelectionFragment
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.setNavigationResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeldAccountSelectionFragment : BaseAccountSelectionFragment() {

    override val toolbarConfiguration = ToolbarConfiguration(
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_close,
        titleResId = R.string.select_account
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val meldAccountSelectionViewModel by viewModels<MeldAccountSelectionViewModel>()

    private val accountItemsCollector: suspend (List<BaseAccountSelectionListItem>) -> Unit = { accountItems ->
        accountAdapter.submitList(accountItems)
    }

    override fun onAccountSelected(publicKey: String) {
        setNavigationResult(ACCOUNT_SELECTION_RESULT_KEY, publicKey)
        navBack()
    }

    override fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            meldAccountSelectionViewModel.accountItemsFlow,
            accountItemsCollector
        )
    }

    companion object {
        const val ACCOUNT_SELECTION_RESULT_KEY = "account_selection_result_key"
    }
}
