@file:SuppressWarnings("TooManyFunctions")
/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.modules.accountdetail.assets.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentAccountAssetsBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.accountdetail.assets.ui.adapter.AccountAssetsAccountDetailAdapter
import com.algorand.android.modules.accountdetail.assets.ui.adapter.AccountAssetsAdapter
import com.algorand.android.modules.accountdetail.assets.ui.adapter.AssetSwipeToDeleteCallback
import com.algorand.android.modules.accountdetail.assets.ui.domain.AccountDetailAccountsItemProcessor
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem
import com.algorand.android.utils.ExcludedViewTypesDividerItemDecoration
import com.algorand.android.utils.RecyclerViewPositionVisibilityHandler
import com.algorand.android.utils.addCustomDivider
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class AccountAssetsFragment : BaseFragment(R.layout.fragment_account_assets) {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(isBottomBarNeeded = true)

    private val binding by viewBinding(FragmentAccountAssetsBinding::bind)

    private val accountAssetsViewModel: AccountAssetsViewModel by viewModels()

    private val accountAssetsLineChartViewModel: AccountAssetsLineChartViewModel by viewModels()

    private var listener: Listener? = null

    private val recyclerViewPositionVisibilityListener = RecyclerViewPositionVisibilityHandler.Listener { isVisible ->
        with(binding.accountAssetsMotionLayout) {
            if (isVisible) transitionToStart() else transitionToEnd()
        }
    }

    private val recyclerViewPositionVisibilityHandler = RecyclerViewPositionVisibilityHandler(
        position = AccountDetailAccountsItemProcessor.QUICK_ACTIONS_INDEX,
        listener = recyclerViewPositionVisibilityListener
    )

    private val accountAssetsListener = object : AccountAssetsAdapter.Listener {
        override fun onAssetClick(assetId: Long) {
            listener?.onAssetClick(assetId)
        }

        override fun onAssetLongClick(assetId: Long) {
            listener?.onAssetLongClick(assetId)
        }

        override fun onNFTClick(nftId: Long) {
            listener?.onNFTClick(nftId)
        }

        override fun onNFTLongClick(nftId: Long) {
            listener?.onNFTLongClick(nftId)
        }

        override fun onRemoveAsset(assetId: Long) {
            listener?.onRemoveAsset(assetId)
        }

        override fun onRemoveCollectible(assetId: Long) {
            listener?.onRemoveCollectible(assetId)
        }
    }

    private val accountDetailAdapterListener = object : AccountAssetsAccountDetailAdapter.Listener {
        override fun onAddNewAssetClick() {
            accountAssetsViewModel.logAddAssetClick()
            listener?.onAddAssetClick()
        }

        override fun onSearchQueryUpdated(query: String) {
            accountAssetsViewModel.updateSearchQuery(query = query)
        }

        override fun onManageAssetsClick() {
            accountAssetsViewModel.logManageAssetsClick()
            listener?.onManageAssetsClick()
        }

        override fun onAssetInboxClick() {
            accountAssetsViewModel.logInboxClick()
            listener?.onAssetInboxClick()
        }

        override fun onSendClick() {
            listener?.onSendClick()
        }

        override fun onSwapClick() {
            accountAssetsViewModel.logSwapClick()
            listener?.onSwapClick()
        }

        override fun onMoreClick() {
            accountAssetsViewModel.logMoreClick()
            listener?.onMoreClick()
        }

        override fun onRequiredMinimumBalanceClick() {
            listener?.onMinimumBalanceInfoClick()
        }

        override fun onCopyAddressClick() {
            listener?.onCopyAddressClick()
        }

        override fun onShowAddressClick() {
            listener?.onShowAddressClick()
        }

        override fun onBackupNowClick() {
            listener?.onBackupNowClick()
        }

        override fun onBuySellClick() {
            accountAssetsViewModel.logBuyAlgoClick()
            listener?.onBuySellClick()
        }

        override fun onAccountValueClick() {
            accountAssetsViewModel.togglePrivacy()
        }

        override fun onChartTap() {
            accountAssetsViewModel.logChartTap()
        }

        override fun onFundClick() {
            listener?.onFundClick()
        }

        override fun onJointAccountBadgeClick() {
            listener?.onJointAccountBadgeClick()
        }
    }

    private val accountAssetsAdapter = AccountAssetsAdapter(accountAssetsListener)

    private lateinit var accountAssetsAccountDetailAdapter: AccountAssetsAccountDetailAdapter

    private lateinit var accountAssetsConcatAdapter: ConcatAdapter

    private val swipeToDeleteCallback = AssetSwipeToDeleteCallback(accountAssetsAdapter::onSwiped)

    private val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)

    private val accountAssetsCollector: suspend (PagingData<AccountDetailAssetsItem>?) -> Unit = { items ->
        items?.let { accountAssetsAdapter.submitData(items) }
    }

    private val accountAssetsHeadersCollector: suspend (List<AccountDetailAccountsItem>?) -> Unit = { items ->
        items?.let { accountAssetsAccountDetailAdapter.submitList(items) }
    }

    private val canSignTransactionCollector: suspend (Boolean?) -> Unit = { canSignTransaction ->
        if (canSignTransaction == true) {
            itemTouchHelper.attachToRecyclerView(binding.accountAssetsRecyclerView)
        } else {
            itemTouchHelper.attachToRecyclerView(null)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        // TODO: find a way to update the preview flow only in case of filter option changes
        accountAssetsViewModel.initAccountAssetsFlow()
    }

    private fun initUi() {
        accountAssetsAccountDetailAdapter = AccountAssetsAccountDetailAdapter(
            address = accountAssetsViewModel.accountAddress,
            lineChartViewModel = accountAssetsLineChartViewModel,
            listener = accountDetailAdapterListener
        )
        accountAssetsConcatAdapter = ConcatAdapter(accountAssetsAccountDetailAdapter, accountAssetsAdapter)
        binding.accountAssetsRecyclerView.apply {
            recyclerViewPositionVisibilityHandler.addOnScrollListener(this)
            adapter = accountAssetsConcatAdapter
            itemAnimator = null
            addCustomDivider(
                drawableResId = R.drawable.horizontal_divider_80_24dp,
                showLast = false,
                divider = ExcludedViewTypesDividerItemDecoration(AccountDetailAssetsItem.excludedItemFromDivider)
            )
        }
        binding.accountQuickActionsFloatingActionButton.setOnClickListener {
            accountAssetsViewModel.canSignTransaction?.let { canSignTransaction ->
                listener?.onAccountQuickActionsFloatingActionButtonClicked(isWatchAccount = !canSignTransaction)
            }
        }
    }

    private fun initObservers() {
        with(accountAssetsViewModel.accountAssetsFlow) {
            collectLatestOnLifecycle(
                flow = map { it?.accountDetailAssetsItemList }.distinctUntilChanged(),
                collection = accountAssetsCollector
            )
            collectLatestOnLifecycle(
                flow = map { it?.accountDetailAccountItems }.distinctUntilChanged(),
                collection = accountAssetsHeadersCollector
            )
            collectLatestOnLifecycle(
                flow = map { it?.canSignTransaction }.distinctUntilChanged(),
                collection = canSignTransactionCollector
            )
        }
    }

    interface Listener {
        fun onAddAssetClick()
        fun onAssetClick(assetId: Long)
        fun onAssetLongClick(assetId: Long)
        fun onNFTClick(nftId: Long)
        fun onNFTLongClick(nftId: Long)
        fun onRemoveAsset(assetId: Long)
        fun onRemoveCollectible(assetId: Long)
        fun onInboxClick()
        fun onSendClick()
        fun onSwapClick()
        fun onMoreClick()
        fun onManageAssetsClick()
        fun onAccountQuickActionsFloatingActionButtonClicked(isWatchAccount: Boolean)
        fun onMinimumBalanceInfoClick()
        fun onCopyAddressClick()
        fun onShowAddressClick()
        fun onBackupNowClick()
        fun onBuySellClick()
        fun onFundClick()
        fun onJointAccountBadgeClick()
    }

    companion object {
        const val ADDRESS_KEY: String = "address_key"
        fun newInstance(address: String): AccountAssetsFragment {
            return AccountAssetsFragment().apply { arguments = Bundle().apply { putString(ADDRESS_KEY, address) } }
        }
    }
}
