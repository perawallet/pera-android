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

package com.algorand.android.ui.asset.remove.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentRemoveAssetsBinding
import com.algorand.android.models.AssetAction
import com.algorand.android.models.AssetActionResult
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.assets.action.transferbalance.TransferBalanceActionBottomSheet.Companion.TRANSFER_ASSET_ACTION_RESULT
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.RemoveAssetItem
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem
import com.algorand.android.ui.asset.remove.view.RemoveAssetsViewModel.ViewState.Content
import com.algorand.android.ui.asset.remove.view.RemoveAssetsViewModel.ViewState.Idle
import com.algorand.android.ui.asset.remove.view.adapter.RemoveAssetAdapter
import com.algorand.android.ui.asset.remove.view.adapter.RemoveAssetHeaderAdapter
import com.algorand.android.utils.ExcludedViewTypesDividerItemDecoration
import com.algorand.android.utils.addCustomDivider
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.isGreaterThan
import com.algorand.android.utils.useFragmentResultListenerValue
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigInteger

@AndroidEntryPoint
class RemoveAssetsFragment : BaseFragment(R.layout.fragment_remove_assets) {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_close,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    private val removeAssetsViewModel: RemoveAssetsViewModel by viewModels()

    private val binding by viewBinding(FragmentRemoveAssetsBinding::bind)

    private val removeAssetAdapterListener = object : RemoveAssetAdapter.RemoveAssetAdapterListener {
        override fun onAssetItemClick(assetId: Long) {
            navToAsaProfile(assetId)
        }

        override fun onCollectibleItemClick(collectibleId: Long) {
            navToCollectibleProfile(collectibleId)
        }

        override fun onCollectibleRemoveClick(removeAssetItem: RemoveAssetItem) {
            onRemoveCollectibleClick(removeAssetItem)
        }

        override fun onAssetRemoveClick(removeAssetItem: RemoveAssetItem) {
            onRemoveAssetClick(removeAssetItem)
        }
    }

    private val removeAssetHeaderAdapterListener = object : RemoveAssetHeaderAdapter.RemoveAssetHeaderAdapterListener {
        override fun onSearchQueryUpdate(query: String) {
            removeAssetsViewModel.updateSearchingQuery(query)
        }
    }

    private val removeAssetAdapter = RemoveAssetAdapter(removeAssetAdapterListener)

    private val removeAssetHeaderAdapter = RemoveAssetHeaderAdapter(removeAssetHeaderAdapterListener)

    private val concatAdapter = ConcatAdapter(removeAssetHeaderAdapter, removeAssetAdapter)

    private val viewStateCollector: suspend (RemoveAssetsViewModel.ViewState) -> Unit = { state ->
        when (state) {
            Idle -> Unit
            is Content -> removeAssetHeaderAdapter.submitList(state.headerItems)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        initObservers()
        removeAssetsViewModel.initializeViewState()
    }

    private fun setupToolbar() {
        getAppToolbar()?.configure(toolbarConfiguration)
    }

    private fun setupRecyclerView() {
        binding.assetsRecyclerView.apply {
            adapter = concatAdapter
            addCustomDivider(
                drawableResId = R.drawable.horizontal_divider_80_24dp,
                showLast = false,
                divider = ExcludedViewTypesDividerItemDecoration(RemoveAssetHeaderItem.excludedItemFromDivider)
            )
        }
    }

    private fun initObservers() {
        with(viewLifecycleOwner) {
            collectLatestOnLifecycle(removeAssetsViewModel.state, viewStateCollector)
            collectLatestOnLifecycle(removeAssetsViewModel.assetItemsPagingDataFlow, removeAssetAdapter::submitData)
        }
    }

    private fun onRemoveAssetClick(removeAssetItem: RemoveAssetItem) {
        val hasBalanceInAccount = removeAssetItem.amount isGreaterThan BigInteger.ZERO
        if (hasBalanceInAccount) {
            navToTransferBalanceActionBottomSheet(removeAssetItem)
        } else {
            navToRemoveAssetActionBottomSheet(removeAssetItem)
        }
    }

    private fun onRemoveCollectibleClick(removeAssetItem: RemoveAssetItem) {
        val hasBalanceInAccount = removeAssetItem.amount isGreaterThan BigInteger.ZERO
        if (hasBalanceInAccount) {
            navToTransferBalanceActionBottomSheet(removeAssetItem)
        } else {
            navToOptOutCollectibleActionBottomSheet(removeAssetItem)
        }
    }

    private fun navToCollectibleProfile(collectibleId: Long) {
        nav(
            RemoveAssetsFragmentDirections.actionRemoveAssetsFragmentToCollectibleProfileNavigation(
                collectibleId = collectibleId,
                accountAddress = removeAssetsViewModel.accountAddress
            )
        )
    }

    private fun navToAsaProfile(assetId: Long) {
        nav(
            RemoveAssetsFragmentDirections.actionRemoveAssetsFragmentToAsaProfileNavigation(
                assetId = assetId,
                accountAddress = removeAssetsViewModel.accountAddress
            )
        )
    }

    private fun navToTransferBalanceActionBottomSheet(removeAssetItem: RemoveAssetItem) {
        nav(
            RemoveAssetsFragmentDirections.actionRemoveAssetsFragmentToAssetTransferBalanceActionNavigation(
                assetAction = createAssetAction(removeAssetItem)
            )
        )
    }

    private fun navToRemoveAssetActionBottomSheet(removeAssetItem: RemoveAssetItem) {
        nav(
            RemoveAssetsFragmentDirections.actionRemoveAssetsFragmentToAssetRemovalActionNavigation(
                assetAction = createAssetAction(removeAssetItem)
            )
        )
    }

    private fun navToOptOutCollectibleActionBottomSheet(removeAssetItem: RemoveAssetItem) {
        nav(
            RemoveAssetsFragmentDirections.actionRemoveAssetsFragmentToNftOptOutConfirmationNavigation(
                assetAction = createAssetAction(removeAssetItem)
            )
        )
    }

    private fun createAssetAction(removeAssetItem: RemoveAssetItem): AssetAction {
        return AssetAction(
            assetId = removeAssetItem.id,
            assetFullName = removeAssetItem.name,
            publicKey = removeAssetsViewModel.accountAddress
        )
    }

    override fun onResume() {
        super.onResume()
        initSavedStateListener()
    }

    private fun navToSendAlgoNavigation(assetTransaction: AssetTransaction, shouldPopulateAmountWithMax: Boolean) {
        nav(HomeNavigationDirections.actionGlobalSendAlgoNavigation(assetTransaction, shouldPopulateAmountWithMax))
    }

    private fun initSavedStateListener() {
        useFragmentResultListenerValue<AssetActionResult>(
            key = TRANSFER_ASSET_ACTION_RESULT,
            result = { assetActionResult ->
                val assetId = assetActionResult.assetId
                val assetTransaction = AssetTransaction(
                    assetId = assetId,
                    senderAddress = removeAssetsViewModel.accountAddress,
                    amount = BigInteger.ZERO,
                )
                navToSendAlgoNavigation(
                    assetTransaction = assetTransaction,
                    shouldPopulateAmountWithMax = true
                )
            }
        )
    }
}
