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

package com.algorand.android.ui.swap.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.algorand.android.MainNavigationDirections
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.swap.accountselection.view.SwapAddressSelectionFragment.Companion.SWAP_ADDRESS_SELECTION_KEY
import com.algorand.android.ui.swap.assetselection.view.SwapAssetInSelectionFragment.Companion.SWAP_ASSET_IN_ID_KEY
import com.algorand.android.ui.swap.assetselection.view.SwapAssetOutSelectionFragment.Companion.SWAP_ASSET_OUT_ID_KEY
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.utils.browser.SWAP_INFO_SUPPORT_URL
import com.algorand.android.utils.browser.openUrl
import com.algorand.android.utils.browser.openVestigeTermsOfServiceUrl
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegation
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegationImpl
import com.algorand.android.utils.useFragmentResultListenerValue
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwapFragment : BaseFragment(0), SwapScreenListener,
    BottomNavBarFragmentDelegation by BottomNavBarFragmentDelegationImpl() {

    private val swapViewModel: SwapViewModel by viewModels()

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(isBottomBarNeeded = true)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            PeraTheme {
                SwapScreen(swapViewModel, listener = this)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerBottomNavBarFragmentDelegation(this)
    }

    override fun onStart() {
        super.onStart()
        initSavedStateListener()
    }

    private fun initSavedStateListener() {
        useFragmentResultListenerValue<Long>(SWAP_ASSET_IN_ID_KEY) { assetId ->
            swapViewModel.setAssetInId(assetId)
        }
        useFragmentResultListenerValue<Long>(SWAP_ASSET_OUT_ID_KEY) { assetId ->
            swapViewModel.setAssetOutId(assetId)
        }
        useFragmentResultListenerValue<String>(SWAP_ADDRESS_SELECTION_KEY) { address ->
            swapViewModel.setAddress(address)
        }
    }

    override fun onCreateAccountClick() {
        nav(MainNavigationDirections.actionGlobalLoginNavigation())
    }

    override fun onAccountChipClick() {
        nav(SwapFragmentDirections.actionSwapFragmentToSwapAddressSelectionFragment())
    }

    override fun onInfoIconClick() {
        context?.openUrl(SWAP_INFO_SUPPORT_URL)
    }

    override fun onSwapClick(quote: SwapQuoteV2) {
        nav(SwapFragmentDirections.actionSwapFragmentToSwapConfirmationFragment(quote))
    }

    override fun onSwapHistorySeeAllClick() {
        val address = swapViewModel.getAddress() ?: return
        nav(SwapFragmentDirections.actionSwapFragmentToSwapHistoryFragment(address))
    }

    override fun onAssetInChipClick() {
        val address = swapViewModel.getAddress() ?: return
        nav(SwapFragmentDirections.actionSwapFragmentToSwapAssetInSelectionFragment(address))
    }

    override fun onAssetOutChipClick() {
        val address = swapViewModel.getAddress() ?: return
        val assetInId = swapViewModel.getAssetInId()
        nav(SwapFragmentDirections.actionSwapFragmentToSwapAssetOutSelectionFragment(address, assetInId))
    }

    override fun onStartSwappingClick() {
        swapViewModel.acceptTermsOfService()
    }

    override fun onTermsOfServiceClick() {
        context?.openVestigeTermsOfServiceUrl()
    }
}
