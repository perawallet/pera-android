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

package com.algorand.android.ui.asset.collectible.listing.view

import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ConcatAdapter
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentBaseCollectiblesListingBinding
import com.algorand.android.modules.collectibles.listingviewtype.domain.model.NFTListingViewType
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem
import com.algorand.android.ui.asset.collectible.listing.view.adapter.CollectibleListAdapter
import com.algorand.android.ui.asset.collectible.listing.view.adapter.CollectibleListHeadersAdapter
import com.algorand.android.ui.asset.collectible.listing.view.adapter.CollectibleListHeadersAdapter.CollectibleListHeadersAdapterListener
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.ContentState
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.ContentState.ContentStateType.Content
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.ContentState.ContentStateType.Empty
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.Idle
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.Loading
import com.algorand.android.utils.ExcludedViewTypesDividerItemDecoration
import com.algorand.android.utils.addCustomDivider
import com.algorand.android.utils.addItemVisibilityChangeListener
import com.algorand.android.utils.extensions.collectLatestOnLifecycle

class CollectibleListingFragmentDelegate(
    private val fragment: BaseFragment,
    private val binding: FragmentBaseCollectiblesListingBinding,
    private val viewModel: CollectibleListingViewModel,
    private val listener: Listener
) {

    private val collectibleListAdapterListener = object : CollectibleListAdapter.CollectibleListAdapterListener {
        override fun onOwnedNFTItemClick(collectibleAssetId: Long, publicKey: String) {
            listener.onOwnedNFTItemClick(collectibleAssetId, publicKey)
        }
    }

    private val collectibleListHeadersAdapterListener = object : CollectibleListHeadersAdapterListener {
        override fun onReceiveCollectibleItemClick() {
            onReceiveCollectibleClick()
        }

        override fun onSearchQueryUpdated(query: String) {
            viewModel.updateSearchKeyword(query)
        }

        override fun onManageCollectiblesClick() {
            listener.onManageCollectiblesClick()
        }

        override fun onLinearVerticalListingOptionSelected() {
            viewModel.saveNFTListingViewTypePreference(NFTListingViewType.LINEAR_VERTICAL)
        }

        override fun onGridListingOptionSelected() {
            viewModel.saveNFTListingViewTypePreference(NFTListingViewType.GRID)
        }
    }

    private val collectibleListAdapter = CollectibleListAdapter(collectibleListAdapterListener)

    private val collectibleListHeadersAdapter = CollectibleListHeadersAdapter(collectibleListHeadersAdapterListener)

    private val collectibleConcatAdapter = ConcatAdapter(collectibleListHeadersAdapter, collectibleListAdapter)

    private val viewStateCollector: suspend (ViewState) -> Unit = {
        updateViewState(it)
    }

    fun init() {
        initObserver()
        initUi()
    }

    private suspend fun updateViewState(state: ViewState) {
        with(binding) {
            if (state is ContentState && state.isThereAnyAuthAddress) {
                addItemVisibilityChangeListenerToRecyclerView()
                addBottomPaddingToEmptyState()
            }
            emptyStateScrollView.isVisible = state is ContentState && state.type is Empty
            initReceiveCollectiblesButton(state)
            progressBar.root.isVisible = state is Loading || state is Idle
            initClearFilterButton(state)
            addCollectibleFloatingActionButton.setOnClickListener { listener.onAddCollectibleFabClicked() }
            initCollectibleListItems(state)
        }
    }

    private fun addBottomPaddingToEmptyState() {
        val paddingBottom = fragment
            .resources.getDimensionPixelSize(R.dimen.safe_padding_for_floating_action_button)
        binding.emptyStateScrollView.apply {
            updatePadding(bottom = paddingBottom)
            clipToPadding = false
        }
    }

    private fun initReceiveCollectiblesButton(state: ViewState) {
        val isVisible = state is ContentState && state.type is Empty.NoCollectible && state.isThereAnyAuthAddress
        binding.receiveCollectiblesButton.isVisible = isVisible
    }

    private suspend fun initCollectibleListItems(state: ViewState) {
        binding.collectiblesRecyclerView.isVisible = state is ContentState && state.type is Content
        if (state is ContentState && state.type is Content) {
            collectibleListHeadersAdapter.submitList(state.type.headersList)
            collectibleListAdapter.submitData(state.type.collectibleList)
        }
    }

    private fun initClearFilterButton(state: ViewState) {
        binding.clearFiltersButton.apply {
            setOnClickListener { viewModel.clearFilters() }
            if (state is ContentState && state.type is Empty.AllFilteredOut) {
                text = resources.getString(R.string.show_filtered_nfts_formatted, state.type.filteredOutCount)
            }
            isVisible = state is ContentState && state.type is Empty.AllFilteredOut
        }
    }

    private fun addItemVisibilityChangeListenerToRecyclerView() {
        binding.collectiblesRecyclerView.addItemVisibilityChangeListener(
            listener.getConfigurationHeaderItemIndex()
        ) { isVisible -> onListItemConfigurationHeaderItemVisibilityChange(isVisible) }
    }

    private fun onListItemConfigurationHeaderItemVisibilityChange(isVisible: Boolean) {
        with(binding.baseCollectiblesListingMotionLayout) {
            if (isVisible) {
                transitionToStart()
            } else {
                transitionToEnd()
            }
        }
    }

    private fun initUi() {
        with(binding) {
            collectiblesRecyclerView.apply {
                adapter = collectibleConcatAdapter
                layoutManager = CollectibleListGridLayoutManager(context, collectibleConcatAdapter)
                addCustomDivider(
                    drawableResId = R.drawable.horizontal_divider_80_24dp,
                    showLast = false,
                    divider = ExcludedViewTypesDividerItemDecoration(CollectibleListItem.excludedItemFromDivider)
                )
            }
            receiveCollectiblesButton.setOnClickListener { onReceiveCollectibleClick() }
        }
    }

    private fun onReceiveCollectibleClick() {
        viewModel.logCollectibleReceiveEvent()
        listener.onReceiveCollectibleClick()
    }

    private fun initObserver() {
        fragment.collectLatestOnLifecycle(viewModel.state, viewStateCollector)
    }

    interface Listener {
        fun onManageCollectiblesClick()
        fun onOwnedNFTItemClick(collectibleAssetId: Long, address: String)
        fun onReceiveCollectibleClick()
        fun onAddCollectibleFabClicked()
        fun getConfigurationHeaderItemIndex(): Int
    }
}
