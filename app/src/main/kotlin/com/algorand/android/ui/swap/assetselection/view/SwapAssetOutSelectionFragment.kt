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

package com.algorand.android.ui.swap.assetselection.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.android.ui.swap.assetselection.viewmodel.SwapAssetOutSelectionViewModel
import com.algorand.android.ui.swap.assetselection.viewmodel.SwapAssetOutSelectionViewModel.ViewEvent
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.setFragmentNavigationResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwapAssetOutSelectionFragment : BaseFragment(0), SwapAssetOutSelectionScreenListener {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val assetOutSelectionViewModel: SwapAssetOutSelectionViewModel by viewModels()

    private val args by navArgs<SwapAssetOutSelectionFragmentArgs>()

    private val viewEventObserver: suspend (ViewEvent) -> Unit = { viewEvent ->
        when (viewEvent) {
            ViewEvent.ShowGenericError -> showGlobalError(getString(R.string.swap_asset_load_failed))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createComposeView {
            SwapAssetOutSelectionScreen(assetOutSelectionViewModel, this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectLatestOnLifecycle(assetOutSelectionViewModel.viewEvent, viewEventObserver)
        assetOutSelectionViewModel.init(args.address, args.assetId)
    }

    override fun onAssetClick(assetListItem: AssetListItem) {
        setFragmentNavigationResult(SWAP_ASSET_OUT_ID_KEY, assetListItem.assetId)
        navBack()
    }

    override fun onBackButtonClick() {
        navBack()
    }

    companion object {
        const val SWAP_ASSET_OUT_ID_KEY: String = "swapAssetOutIdKey"
    }
}
