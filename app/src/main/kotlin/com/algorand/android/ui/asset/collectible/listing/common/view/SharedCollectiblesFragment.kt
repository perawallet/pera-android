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

package com.algorand.android.ui.asset.collectible.listing.common.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentBaseCollectiblesListingBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.asset.collectible.listing.common.viewmodel.SharedCollectibleListingViewModel
import com.algorand.android.ui.asset.collectible.listing.view.CollectibleListingFragmentDelegate
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegation
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegationImpl
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SharedCollectiblesFragment : BaseFragment(R.layout.fragment_base_collectibles_listing),
    CollectibleListingFragmentDelegate.Listener,
    BottomNavBarFragmentDelegation by BottomNavBarFragmentDelegationImpl() {

    private val toolbarConfiguration = ToolbarConfiguration(backgroundColor = R.color.primary_background)

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration,
        isBottomBarNeeded = true
    )

    private var collectiblesListingFragmentDelegate: CollectibleListingFragmentDelegate? = null

    private val collectiblesViewModel: SharedCollectibleListingViewModel by viewModels()

    private val binding by viewBinding(FragmentBaseCollectiblesListingBinding::bind)

    private val args: SharedCollectiblesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentDelegate()
        initBottomNavBarFragmentDelegation()
        initToolbar()
        collectiblesViewModel.initPreview(args.registerBottomNavDelegation)
    }

    private fun initToolbar() {
        if (!args.registerBottomNavDelegation) {
            getAppToolbar()?.apply {
                configureStartButton(resId = R.drawable.ic_left_arrow, clickAction = ::navBack)
                changeTitle(R.string.nfts)
            }
        }
    }

    private fun initFragmentDelegate() {
        collectiblesListingFragmentDelegate = CollectibleListingFragmentDelegate(
            fragment = this,
            viewModel = collectiblesViewModel,
            binding = binding,
            listener = this
        )
        collectiblesListingFragmentDelegate?.init()
    }

    private fun initBottomNavBarFragmentDelegation() {
        if (args.registerBottomNavDelegation) {
            registerBottomNavBarFragmentDelegation(this)
        } else {
            getAppToolbar()?.configureStartButton(R.drawable.ic_left_arrow, clickAction = ::navBack)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        collectiblesListingFragmentDelegate = null
    }

    override fun getConfigurationHeaderItemIndex(): Int {
        return COLLECTIBLES_LIST_CONFIGURATION_HEADER_ITEM_INDEX
    }

    override fun onAddCollectibleFabClicked() {
        nav(
            SharedCollectiblesFragmentDirections
                .actionCollectiblesFragmentToCollectibleReceiverAccountSelectionFragment()
        )
    }

    override fun onOwnedNFTItemClick(collectibleAssetId: Long, address: String) {
        nav(
            SharedCollectiblesFragmentDirections.actionCollectiblesFragmentToCollectibleDetailFragment(
                collectibleAssetId = collectibleAssetId,
                publicKey = address
            )
        )
    }

    override fun onReceiveCollectibleClick() {
        nav(
            SharedCollectiblesFragmentDirections
                .actionCollectiblesFragmentToCollectibleReceiverAccountSelectionFragment()
        )
    }

    override fun onManageCollectiblesClick() {
        nav(SharedCollectiblesFragmentDirections.actionCollectiblesFragmentToManageCollectiblesBottomSheet())
    }

    private companion object {
        const val COLLECTIBLES_LIST_CONFIGURATION_HEADER_ITEM_INDEX = 1
    }
}
