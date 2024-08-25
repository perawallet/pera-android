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

package com.algorand.android.modules.dapp.bidali.ui.accountselection

import android.widget.TextView
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.accountcore.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.dapp.bidali.ui.accountselection.model.BidaliAccountSelectionPreview
import com.algorand.android.ui.accountselection.BaseAccountSelectionFragment
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BidaliAccountSelectionFragment : BaseAccountSelectionFragment() {

    override val toolbarConfiguration = ToolbarConfiguration(
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_close,
        titleResId = R.string.buy_gift_cards_with_bidali,
        titleColor = R.color.text_main
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val bidaliAccountSelectionPreviewCollector: suspend (BidaliAccountSelectionPreview) -> Unit = { preview ->
        with(preview) {
            finalizeAccountSelectionEvent?.consume()?.run {
                nav(this)
            }
        }
    }

    private val bidaliAccountSelectionViewModel by viewModels<BidaliAccountSelectionViewModel>()

    private val accountItemsCollector: suspend (List<BaseAccountSelectionListItem>) -> Unit = { accountItems ->
        accountAdapter.submitList(accountItems)
    }

    override fun onAccountSelected(publicKey: String) {
        bidaliAccountSelectionViewModel.onAccountSelected(publicKey)
    }

    override fun setTitleTextView(textView: TextView) {
        textView.apply {
            setText(R.string.select_account)
            show()
        }
    }

    override fun setDescriptionTextView(textView: TextView) {
        textView.apply {
            setText(R.string.choose_an_account_to_proceed)
            show()
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            bidaliAccountSelectionViewModel.accountItemsFlow,
            accountItemsCollector
        )
        viewLifecycleOwner.collectLatestOnLifecycle(
            bidaliAccountSelectionViewModel.bidaliAccountSelectionPreviewFlow,
            bidaliAccountSelectionPreviewCollector
        )
    }
}
