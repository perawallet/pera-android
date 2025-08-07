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
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.swap.assetselection.fromasset.ui.SwapFromAssetSelectionFragment.Companion.SWAP_FROM_ASSET_ID_KEY
import com.algorand.android.modules.swap.assetselection.toasset.ui.SwapToAssetSelectionFragment.Companion.SWAP_TO_ASSET_ID_KEY
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.utils.useFragmentResultListenerValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwapFragment : BaseFragment(0), SwapScreenListener {

    private val swapViewModel: SwapViewModel by viewModels()

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            PeraTheme {
                SwapScreen(swapViewModel, listener = this)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initSavedStateListener()
    }

    private fun initSavedStateListener() {
        useFragmentResultListenerValue<Long>(SWAP_FROM_ASSET_ID_KEY) { assetId ->
            swapViewModel.setAssetInId(assetId)
        }
        useFragmentResultListenerValue<Long>(SWAP_TO_ASSET_ID_KEY) { assetId ->
            swapViewModel.setAssetOutId(assetId)
        }
    }

    override fun onCreateAccountClick() {
        // TODO
    }

    override fun onAccountChipClick() {
        // TODO
    }

    override fun onInfoIconClick() {
        // TODO
    }

    override fun onAssetInChipClick() {
        // TODO
    }

    override fun onAssetOutChipClick() {
        // TODO
    }
}
