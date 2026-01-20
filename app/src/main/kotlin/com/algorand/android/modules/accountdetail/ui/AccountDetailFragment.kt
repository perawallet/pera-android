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

@file:Suppress("TooManyFunctions")

package com.algorand.android.modules.accountdetail.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.BaseAccountIconButton
import com.algorand.android.databinding.FragmentAccountDetailBinding
import com.algorand.android.models.AssetActionResult
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.DateFilter
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.accountcore.ui.model.AccountDetailSummary
import com.algorand.android.modules.accountcore.ui.model.AccountIconClickAction
import com.algorand.android.modules.accountdetail.assets.ui.AccountAssetsFragment
import com.algorand.android.modules.accountdetail.haveyoubackedupconfirmation.ui.HaveYouBackedUpAccountConfirmationBottomSheet.Companion.HAVE_YOU_BACKED_UP_ACCOUNT_CONFIRMATION_KEY
import com.algorand.android.modules.accountdetail.history.ui.AccountHistoryFragment
import com.algorand.android.modules.accountdetail.history.ui.AccountTransactionHistoryFragment.AccountTransactionHistoryListener
import com.algorand.android.modules.accountdetail.removeaccount.ui.RemoveAccountConfirmationBottomSheet.Companion.ACCOUNT_REMOVE_CONFIRMATION_KEY
import com.algorand.android.modules.accountdetail.ui.AccountDetailFragmentDirections.Companion.actionAccountDetailFragmentToAssetRemovalActionNavigation
import com.algorand.android.modules.accountdetail.ui.AccountDetailFragmentDirections.Companion.actionAccountDetailFragmentToAssetTransferBalanceActionNavigation
import com.algorand.android.modules.accountdetail.ui.AccountDetailFragmentDirections.Companion.actionAccountDetailFragmentToNftOptOutConfirmationNavigation
import com.algorand.android.modules.accountdetail.ui.AccountDetailViewModel.ViewEvent.NavToRemoveAsset
import com.algorand.android.modules.accountdetail.ui.AccountDetailViewModel.ViewEvent.NavToRemoveCollectible
import com.algorand.android.modules.accountdetail.ui.AccountDetailViewModel.ViewEvent.NavToTransferBalance
import com.algorand.android.modules.assets.action.transferbalance.TransferBalanceActionBottomSheet.Companion.TRANSFER_ASSET_ACTION_RESULT
import com.algorand.android.modules.inapppin.pin.ui.InAppPinFragment
import com.algorand.android.modules.transaction.detail.ui.model.TransactionDetailEntryPoint
import com.algorand.android.modules.transactionhistory.ui.model.BaseTransactionItem
import com.algorand.android.ui.accountoptions.AccountOptionsBottomSheet.Companion.ACCOUNT_REMOVE_ACTION_KEY
import com.algorand.android.ui.accounts.RenameAccountBottomSheet
import com.algorand.android.ui.asset.collectible.listing.account.view.AccountCollectiblesFragment
import com.algorand.android.utils.Event
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.collectOnLifecycle
import com.algorand.android.utils.startSavedStateListener
import com.algorand.android.utils.useFragmentResultListenerValue
import com.algorand.android.utils.useSavedStateValue
import com.algorand.android.utils.viewbinding.viewBinding
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import java.math.BigInteger

@AndroidEntryPoint
class AccountDetailFragment :
    BaseFragment(R.layout.fragment_account_detail),
    AccountHistoryFragment.Listener,
    AccountAssetsFragment.Listener,
    AccountCollectiblesFragment.Listener,
    AccountTransactionHistoryListener {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    private val onPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onSelectedPageChange(position)
        }
    }

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(isBottomBarNeeded = true)

    private val binding by viewBinding(FragmentAccountDetailBinding::bind)

    private val accountDetailViewModel: AccountDetailViewModel by viewModels()

    private val accountDetailSummaryCollector: suspend (AccountDetailSummary?) -> Unit = { summary ->
        if (summary != null) initAccountDetailSummary(summary)
    }

    private val accountDetailTabArgCollector: suspend (Event<Int>?) -> Unit = {
        it?.consume()?.run { updateViewPagerBySelectedTab(this) }
    }

    private val navBackEventCollector: suspend (Event<Unit>?) -> Unit = {
        it?.consume()?.run { navBack() }
    }

    private val viewEventCollector: suspend (AccountDetailViewModel.ViewEvent) -> Unit = { viewEvent ->
        when (viewEvent) {
            is NavToRemoveAsset -> nav(actionAccountDetailFragmentToAssetRemovalActionNavigation(viewEvent.assetAction))
            is NavToTransferBalance -> {
                nav(actionAccountDetailFragmentToAssetTransferBalanceActionNavigation(viewEvent.assetAction))
            }

            is NavToRemoveCollectible -> {
                nav(actionAccountDetailFragmentToNftOptOutConfirmationNavigation(viewEvent.assetAction))
            }
        }
    }

    private lateinit var accountDetailPagerAdapter: AccountDetailPagerAdapter

    override fun onStandardTransactionClick(transaction: BaseTransactionItem.TransactionItem) {
        navToTransactionDetail(transaction.id ?: return, TransactionDetailEntryPoint.STANDARD_TRANSACTION)
    }

    override fun onApplicationCallTransactionClick(
        transaction: BaseTransactionItem.TransactionItem.ApplicationCallItem
    ) {
        navToTransactionDetail(transaction.id ?: return, TransactionDetailEntryPoint.APPLICATION_CALL_TRANSACTION)
    }

    override fun onStandardTransactionClick(id: String) {
        navToTransactionDetail(id, TransactionDetailEntryPoint.STANDARD_TRANSACTION)
    }

    override fun onApplicationCallTransactionClick(id: String) {
        navToTransactionDetail(id, TransactionDetailEntryPoint.APPLICATION_CALL_TRANSACTION)
    }

    override fun onSwapTransactionClick(groupId: String) {
        nav(
            AccountDetailFragmentDirections.actionAccountDetailFragmentToSwapGroupDetailFragment(
                accountAddress = accountDetailViewModel.accountAddress,
                groupId = groupId
            )
        )
    }

    private fun navToTransactionDetail(txnId: String, entryPoint: TransactionDetailEntryPoint) {
        nav(
            AccountDetailFragmentDirections.actionAccountDetailFragmentToTransactionDetailNavigation(
                transactionId = txnId,
                accountAddress = accountDetailViewModel.accountAddress,
                entryPoint = entryPoint
            )
        )
    }

    override fun onFilterTransactionClick(dateFilter: DateFilter) {
        nav(AccountDetailFragmentDirections.actionAccountDetailFragmentToDateFilterNavigation(dateFilter))
    }

    override fun onAddAssetClick() {
        handleAddAssetClick()
    }

    override fun onAssetClick(assetId: Long) {
        val address = accountDetailViewModel.accountAddress
        val navDestination = if (accountDetailViewModel.isAssetDetailV2Enabled()) {
            AccountDetailFragmentDirections.actionAccountDetailFragmentToAssetDetailV2Fragment(assetId, address)
        } else {
            AccountDetailFragmentDirections.actionAccountDetailFragmentToAssetDetailNavigation(assetId, address)
        }
        nav(navDestination)
    }

    override fun onAssetLongClick(assetId: Long) {
        onAssetIdLongClick(assetId)
    }

    override fun onNFTClick(nftId: Long) {
        navToCollectibleDetailFragment(nftId)
    }

    override fun onNFTLongClick(nftId: Long) {
        onAssetIdLongClick(nftId)
    }

    override fun onRemoveAsset(assetId: Long) {
        accountDetailViewModel.removeAsset(assetId)
    }

    override fun onRemoveCollectible(assetId: Long) {
        accountDetailViewModel.removeCollectible(assetId)
    }

    override fun onInboxClick() {
        navToInboxWithFilter()
    }

    override fun onSendClick() {
        handleSendClick()
    }

    override fun onSwapClick() {
        handleSwapClick()
    }

    override fun onBuySellClick() {
        navToBuySellActionsBottomSheet()
    }

    override fun onFundClick() {
        navigateToXoSwap()
    }

    override fun onMoreClick() {
        navToAccountOptionsBottomSheet()
    }

    override fun onManageAssetsClick() {
        navToManageAssetsFragment()
    }

    override fun onAccountQuickActionsFloatingActionButtonClicked(isWatchAccount: Boolean) {
        val navigationDestination = with(AccountDetailFragmentDirections) {
            if (isWatchAccount) {
                actionAccountDetailFragmentToWatchAccountQuickActionsBottomSheet(
                    accountDetailViewModel.accountAddress
                )
            } else {
                actionAccountDetailFragmentToAccountQuickActionsBottomSheet(
                    accountDetailViewModel.accountAddress
                )
            }
        }
        nav(navigationDestination)
    }

    override fun onMinimumBalanceInfoClick() {
        navToMinimumBalanceInfoBottomSheet()
    }

    override fun onCopyAddressClick() {
        onAccountAddressCopied(accountDetailViewModel.accountAddress)
    }

    override fun onShowAddressClick() {
        navToShowQrFragment()
    }

    override fun onBackupNowClick() {
        navToBackupPassphraseInfoNavigation()
    }

    override fun onJointAccountBadgeClick() {
        navToJointAccountDetailFragment()
    }

    private fun navToJointAccountDetailFragment() {
        nav(
            AccountDetailFragmentDirections.actionAccountDetailFragmentToJointAccountDetailFragment(
                accountDetailViewModel.accountAddress
            )
        )
    }

    override fun onImageItemClick(nftAssetId: Long) {
        navToCollectibleDetailFragment(nftAssetId)
    }

    override fun onVideoItemClick(nftAssetId: Long) {
        navToCollectibleDetailFragment(nftAssetId)
    }

    override fun onSoundItemClick(nftAssetId: Long) {
        navToCollectibleDetailFragment(nftAssetId)
    }

    override fun onMixedItemClick(nftAssetId: Long) {
        navToCollectibleDetailFragment(nftAssetId)
    }

    private fun navToCollectibleDetailFragment(collectibleId: Long) {
        nav(
            AccountDetailFragmentDirections.actionAccountDetailFragmentToCollectibleDetailFragment(
                collectibleId,
                accountDetailViewModel.accountAddress
            )
        )
    }

    override fun onReceiveCollectibleClick() {
        nav(
            AccountDetailFragmentDirections
                .actionAccountDetailFragmentToReceiveCollectibleFragment(accountDetailViewModel.accountAddress)
        )
    }

    override fun onManageCollectiblesClick() {
        nav(AccountDetailFragmentDirections.actionAccountDetailFragmentToManageAccountNFTsBottomSheet())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        initSavedStateListener()
    }

    private fun initSavedStateListener() {
        useFragmentResultListenerValue<Boolean>(ACCOUNT_REMOVE_ACTION_KEY) { isConfirmed ->
            if (isConfirmed) {
                navToHaveYouBackedUpAccountConfirmationBottomSheet()
            }
        }
        useFragmentResultListenerValue<Boolean>(HAVE_YOU_BACKED_UP_ACCOUNT_CONFIRMATION_KEY) { isConfirmed ->
            if (isConfirmed) {
                navToRemoveAccountConfirmationNavigation()
            }
        }
        useFragmentResultListenerValue<Boolean>(ACCOUNT_REMOVE_CONFIRMATION_KEY) { isConfirmed ->
            if (isConfirmed) {
                accountDetailViewModel.removeAccount(
                    accountDetailViewModel.accountAddress
                )
            }
        }
        useFragmentResultListenerValue<Boolean>(InAppPinFragment.IN_APP_PIN_CONFIRMATION_KEY) { isConfirmed ->
            if (isConfirmed) {
                navToViewPassphraseNavigation(accountDetailViewModel.accountAddress)
            }
        }

        startSavedStateListener(R.id.accountDetailFragment) {
            useSavedStateValue<Boolean>(RenameAccountBottomSheet.RENAME_ACCOUNT_KEY) { isNameChanged ->
                if (isNameChanged) {
                    accountDetailViewModel.initAccountDetailPreview()
                }
            }
        }
        useFragmentResultListenerValue<AssetActionResult>(TRANSFER_ASSET_ACTION_RESULT, ::navToSendAlgoNavigation)
    }

    private fun navToSendAlgoNavigation(assetActionResult: AssetActionResult) {
        val assetTransaction = AssetTransaction(
            assetId = assetActionResult.assetId,
            senderAddress = accountDetailViewModel.accountAddress,
            amount = BigInteger.ZERO,
        )
        nav(HomeNavigationDirections.actionGlobalSendAlgoNavigation(assetTransaction, true))
    }

    private fun initUi() {
        initAccountDetailPager()
        setupTabLayout()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectOnLifecycle(
            flow = accountDetailViewModel.accountDetailPreviewFlow.map { it?.accountDetailSummary },
            collection = accountDetailSummaryCollector
        )
        viewLifecycleOwner.collectOnLifecycle(
            flow = accountDetailViewModel.accountDetailTabArgFlow,
            collection = accountDetailTabArgCollector
        )
        viewLifecycleOwner.collectOnLifecycle(
            flow = accountDetailViewModel.accountDetailPreviewFlow.map { it?.navBackEvent },
            collection = navBackEventCollector
        )
        viewLifecycleOwner.collectLatestOnLifecycle(accountDetailViewModel.viewEvent, viewEventCollector)
    }

    private fun setupTabLayout() {
        with(binding) {
            accountDetailViewPager.isUserInputEnabled = false
            accountDetailViewPager.registerOnPageChangeCallback(onPageChangeCallback)
            TabLayoutMediator(algorandTabLayout, accountDetailViewPager) { tab, position ->
                accountDetailPagerAdapter.getItem(position)?.titleResId?.let {
                    tab.text = getString(it)
                }
            }.attach()
        }
    }

    private fun initAccountDetailSummary(accountDetailSummary: AccountDetailSummary) {
        binding.toolbar.apply {
            configure(toolbarConfiguration)
            configureToolbarName(accountDetailSummary)
            setOnTitleLongClickListener { onAccountAddressCopied(accountDetailSummary.address) }
            val onClickAction = getAccountIconClickAction(accountDetailSummary.accountIconClickAction)
            val endButton = if (accountDetailSummary.shouldDisplayAccountType) {
                BaseAccountIconButton.ExtendedAccountButton(
                    accountIconDrawablePreview = accountDetailSummary.accountIconDrawable,
                    accountTypeResId = accountDetailSummary.accountTypeResId,
                    onClick = onClickAction
                )
            } else {
                BaseAccountIconButton.AccountButton(
                    accountIconDrawablePreview = accountDetailSummary.accountIconDrawable,
                    onClick = onClickAction
                )
            }
            setEndButton(button = endButton)
        }
    }

    private fun getAccountIconClickAction(action: AccountIconClickAction): () -> Unit {
        return when (action) {
            AccountIconClickAction.SHOW_JOINT_ACCOUNT_DETAIL -> ::navToJointAccountDetailFragment
            AccountIconClickAction.SHOW_ACCOUNT_STATUS_DETAIL -> ::navToAccountStatusDetailBottomSheet
        }
    }

    private fun configureToolbarName(accountDetailSummary: AccountDetailSummary) {
        with(binding.toolbar) {
            changeTitle(accountDetailSummary.accountDisplayName.primaryDisplayName)
            accountDetailSummary.accountDisplayName.secondaryDisplayName?.let {
                changeSubtitle(it)
            }
        }
    }

    private fun navToAccountOptionsBottomSheet() {
        nav(
            AccountDetailFragmentDirections
                .actionAccountDetailFragmentToAccountOptionsNavigation(accountDetailViewModel.accountAddress)
        )
    }

    private fun navToAccountStatusDetailBottomSheet() {
        nav(
            AccountDetailFragmentDirections
                .actionAccountDetailFragmentToAccountStatusDetailNavigation(accountDetailViewModel.accountAddress)
        )
    }

    private fun initAccountDetailPager() {
        accountDetailPagerAdapter = AccountDetailPagerAdapter(
            fragment = this,
            address = accountDetailViewModel.accountAddress,
            isAccountHistoryV2Enabled = accountDetailViewModel.isAccountHistoryV2Enabled()
        )
        binding.accountDetailViewPager.adapter = accountDetailPagerAdapter
    }

    private fun updateViewPagerBySelectedTab(selectedTab: Int) {
        binding.accountDetailViewPager.post {
            binding.accountDetailViewPager.setCurrentItem(selectedTab, false)
        }
    }

    private fun navToManageAssetsFragment() {
        nav(
            AccountDetailFragmentDirections
                .actionAccountDetailFragmentToManageAssetsBottomSheet(accountDetailViewModel.accountAddress)
        )
    }

    private fun onSelectedPageChange(position: Int) {
        with(accountDetailViewModel) {
            when (accountDetailPagerAdapter.getItem(position)?.fragmentInstance) {
                is AccountAssetsFragment -> logAccountDetailAssetsTapEventTracker()
                is AccountCollectiblesFragment -> logAccountDetailCollectiblesTapEventTracker()
                is AccountHistoryFragment -> logAccountDetailTransactionHistoryTapEventTracker()
            }
        }
    }

    private fun navToMinimumBalanceInfoBottomSheet() {
        nav(AccountDetailFragmentDirections.actionAccountDetailFragmentToRequiredMinimumBalanceInformationBottomSheet())
    }

    private fun navToRemoveAccountConfirmationNavigation() {
        nav(
            AccountDetailFragmentDirections.actionAccountDetailFragmentToRemoveAccountConfirmationNavigation(
                accountAddress = accountDetailViewModel.accountAddress
            )
        )
    }

    private fun navToHaveYouBackedUpAccountConfirmationBottomSheet() {
        nav(
            AccountDetailFragmentDirections
                .actionAccountDetailFragmentToHaveYouBackedUpAccountConfirmationBottomSheet()
        )
    }

    private fun navToViewPassphraseNavigation(accountAddress: String) {
        nav(
            AccountDetailFragmentDirections
                .actionAccountDetailFragmentToViewPassphraseNavigation(accountAddress)
        )
    }

    private fun navToShowQrFragment() {
        nav(
            AccountDetailFragmentDirections
                .actionGlobalShowQrNavigation(
                    title = getString(R.string.qr_code),
                    qrText = accountDetailViewModel.accountAddress
                )
        )
    }

    private fun navToBackupPassphraseInfoNavigation() {
        nav(
            AccountDetailFragmentDirections
                .actionAccountDetailFragmentToBackupPassphraseInfoNavigation(
                    accountsToBackup = arrayOf(accountDetailViewModel.accountAddress),
                    onboardingAccountType = if (accountDetailViewModel.accountType == AccountType.HdKey) {
                        OnboardingAccountType.HdKey
                    } else {
                        OnboardingAccountType.Algo25
                    }
                )
        )
    }

    private fun onAssetIdLongClick(assetId: Long) {
        if (assetId != ALGO_ID) onAssetIdCopied(assetId)
    }

    private fun handleSendClick() {
        if (accountDetailViewModel.canAccountSignTransaction) {
            val assetTransaction = AssetTransaction(senderAddress = accountDetailViewModel.accountAddress)
            nav(AccountDetailFragmentDirections.actionGlobalSendAlgoNavigation(assetTransaction))
        } else {
            showActionNotAvailableError()
        }
    }

    private fun handleAddAssetClick() {
        if (accountDetailViewModel.canAccountSignTransaction) {
            val direction = AccountDetailFragmentDirections
                .actionAccountDetailFragmentToAssetAdditionNavigation(accountDetailViewModel.accountAddress)
            nav(direction)
        } else {
            showActionNotAvailableError()
        }
    }

    private fun handleSwapClick() {
        if (accountDetailViewModel.canAccountSignTransaction) {
            handleSwapNavigationDestination()
        } else {
            showActionNotAvailableError()
        }
    }

    private fun handleSwapNavigationDestination() {
        with(accountDetailViewModel) {
            if (canAccountSignTransaction) {
                nav(AccountDetailFragmentDirections.actionGlobalSwapV2Navigation(accountAddress))
            } else {
                showActionNotAvailableError()
            }
        }
    }

    private fun showActionNotAvailableError() {
        val message = context?.getString(R.string.this_action_is_not_available)
        showGlobalError(errorMessage = emptyString(), title = message)
    }

    private fun navToInboxWithFilter() {
        nav(
            AccountDetailFragmentDirections
                .actionAccountDetailFragmentToAssetInboxAllAccountsNavigation(
                    filterAccountAddress = accountDetailViewModel.accountAddress
                )
        )
    }

    private fun navToBuySellActionsBottomSheet() {
        nav(HomeNavigationDirections.actionGlobalBuySellActionsBottomSheet())
    }
}
