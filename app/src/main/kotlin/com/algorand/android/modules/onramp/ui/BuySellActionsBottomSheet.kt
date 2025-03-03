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

package com.algorand.android.modules.onramp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.databinding.BottomSheetBuySellActionsBinding
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.utils.viewbinding.viewBinding

class BuySellActionsBottomSheet : BaseBottomSheet(
    layoutResId = R.layout.bottom_sheet_buy_sell_actions,
) {
    private val binding by viewBinding(BottomSheetBuySellActionsBinding::bind)

    private val args: BuySellActionsBottomSheetArgs by navArgs()

    private val buySellActionsBottomSheetViewModel by viewModels<BuySellActionsBottomSheetViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        with(binding) {
            // TODO add logging?
            meldButton.setOnClickListener { navToMeldNavigation() }
            bidaliButton.setOnClickListener { navToBidaliNavigation() }
        }
    }

    private fun navToMeldNavigation() {
        buySellActionsBottomSheetViewModel.logEvent(PeraClickEvent.TAP_MELD_SCREEN_ALGO_SELECT_WALLET)
        nav(BuySellActionsBottomSheetDirections.actionBuySellActionsBottomSheetToMeldNavigation(args.accountAddress))
    }

    private fun navToBidaliNavigation() {
        buySellActionsBottomSheetViewModel.logEvent(PeraClickEvent.TAP_BIDALI_SCREEN_ALGO_SELL)
        nav(BuySellActionsBottomSheetDirections.actionBuySellActionsBottomSheetToBidaliNavigation(args.accountAddress))
    }
}
