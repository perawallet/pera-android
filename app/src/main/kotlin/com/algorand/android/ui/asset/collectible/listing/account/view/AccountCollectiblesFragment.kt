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

package com.algorand.android.ui.asset.collectible.listing.account.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentBaseCollectiblesListingBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.asset.collectible.listing.account.viewmodel.AccountCollectibleListingViewModel
import com.algorand.android.ui.asset.collectible.listing.view.CollectibleListingFragmentDelegate
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountCollectiblesFragment : BaseFragment(R.layout.fragment_base_collectibles_listing),
    CollectibleListingFragmentDelegate.Listener {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        isBottomBarNeeded = true
    )

    private var collectiblesListingFragmentDelegate: CollectibleListingFragmentDelegate? = null

    private var listener: Listener? = null

    private val binding by viewBinding(FragmentBaseCollectiblesListingBinding::bind)

    private val collectiblesViewModel: AccountCollectibleListingViewModel by viewModels()

    override fun onOwnedNFTItemClick(collectibleAssetId: Long, address: String) {
        listener?.onVideoItemClick(collectibleAssetId)
    }

    override fun onReceiveCollectibleClick() {
        listener?.onReceiveCollectibleClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentDelegate()
        collectiblesViewModel.initPreview()
    }

    override fun getConfigurationHeaderItemIndex(): Int {
        return ACCOUNT_COLLECTIBLES_LIST_CONFIGURATION_HEADER_ITEM_INDEX
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

    override fun onAddCollectibleFabClicked() {
        listener?.onReceiveCollectibleClick()
    }

    override fun onManageCollectiblesClick() {
        listener?.onManageCollectiblesClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        collectiblesListingFragmentDelegate = null
    }

    interface Listener {
        fun onImageItemClick(nftAssetId: Long)
        fun onVideoItemClick(nftAssetId: Long)
        fun onSoundItemClick(nftAssetId: Long)
        fun onMixedItemClick(nftAssetId: Long)
        fun onReceiveCollectibleClick()
        fun onManageCollectiblesClick()
    }

    companion object {
        private const val PUBLIC_KEY = "public_key"
        private const val ACCOUNT_COLLECTIBLES_LIST_CONFIGURATION_HEADER_ITEM_INDEX = 0
        fun newInstance(publicKey: String): AccountCollectiblesFragment {
            return AccountCollectiblesFragment().apply {
                arguments = Bundle().apply { putString(PUBLIC_KEY, publicKey) }
            }
        }
    }
}
