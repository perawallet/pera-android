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

package com.algorand.android.modules.accounts.ui.view

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.MainActivity
import com.algorand.android.MainNavigationDirections
import com.algorand.android.R
import com.algorand.android.banner.domain.model.BannerType
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.databinding.FragmentAccountsBinding
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.models.ScreenState
import com.algorand.android.modules.accounts.domain.model.BasePortfolioValueItem
import com.algorand.android.modules.accounts.ui.model.BaseAccountListItem
import com.algorand.android.modules.accounts.ui.viewmodel.AccountsViewModel
import com.algorand.android.modules.accounts.ui.viewmodel.AccountsViewModel.ViewEvent.NavigateToBackupPassphraseInfo
import com.algorand.android.modules.accounts.ui.viewmodel.AccountsViewModel.ViewEvent.NavigateToSwap
import com.algorand.android.modules.accounts.ui.viewmodel.AccountsViewModel.ViewEvent.ShowAccountAddressCopyTutorial
import com.algorand.android.modules.accounts.ui.viewmodel.AccountsViewModel.ViewEvent.ShowGiftCardsTutorial
import com.algorand.android.modules.accounts.ui.viewmodel.AccountsViewModel.ViewEvent.ShowMaxAccountLimitExceededError
import com.algorand.android.modules.accounts.ui.viewmodel.AccountsViewModel.ViewEvent.ShowNotificationPermission
import com.algorand.android.modules.accounts.ui.viewmodel.AccountsViewModel.ViewEvent.ShowSwapTutorial
import com.algorand.android.modules.sorting.accountsorting.ui.AccountSortFragment.Companion.ACCOUNT_SORT_RESULT_KEY
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.modules.tutorialdialog.util.showCopyAccountAddressTutorialDialog
import com.algorand.android.modules.tutorialdialog.util.showGiftCardsTutorialDialog
import com.algorand.android.modules.tutorialdialog.util.showSwapFeatureTutorialDialog
import com.algorand.android.utils.BannerViewTypesDividerItemDecoration
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegation
import com.algorand.android.utils.delegation.bottomnavfragment.BottomNavBarFragmentDelegationImpl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.setDrawableTintColor
import com.algorand.android.utils.useFragmentResultListenerValue
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Suppress("TooManyFunctions")
@AndroidEntryPoint
class AccountsFragment : DaggerBaseFragment(R.layout.fragment_accounts),
    BottomNavBarFragmentDelegation by BottomNavBarFragmentDelegationImpl() {

    private val viewEventCollector: suspend (AccountsViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is AccountsViewModel.ViewEvent.NavToLoginNavigation ->
                navToLoginNavigation()

            is ShowMaxAccountLimitExceededError -> showMaxAccountLimitExceededError()
            is NavigateToBackupPassphraseInfo -> navToBackupPassphraseInfo(event.addresses)
            is NavigateToSwap -> nav(event.navDirections)
            is ShowAccountAddressCopyTutorial -> showAccountAddressCopyTutorialDialog(event.tutorialId)
            is ShowGiftCardsTutorial -> showGiftCardsTutorialDialog(event.tutorialId)
            ShowNotificationPermission -> askNotificationPermission()
            is ShowSwapTutorial -> showSwapTutorialDialog(event.tutorialId)
        }
    }

    override val fragmentConfiguration = FragmentConfiguration(
        isBottomBarNeeded = true,
        firebaseEventScreenId = FIREBASE_EVENT_SCREEN_ID
    )

    private val binding by viewBinding(FragmentAccountsBinding::bind)

    private val accountsViewModel: AccountsViewModel by viewModels<AccountsViewModel>()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        // Nothing to do
    }

    private val accountsEmptyState by lazy {
        ScreenState.CustomState(
            icon = R.drawable.ic_wallet,
            title = R.string.create_an_account,
            description = R.string.you_need_to_create,
            buttonText = R.string.create_new_account
        )
    }

    private val accountAdapterListener = object : AccountsAdapter.AccountAdapterListener {
        override fun onSucceedAccountClick(publicKey: String) {
            nav(AccountsFragmentDirections.actionAccountsFragmentToAccountDetailFragment(publicKey))
        }

        override fun onFailedAccountClick(publicKey: String) {
            nav(AccountsFragmentDirections.actionAccountsFragmentToAccountErrorOptionsBottomSheet(publicKey))
        }

        override fun onAccountItemLongPressed(publicKey: String) {
            onAccountAddressCopied(publicKey)
        }

        override fun onBannerCloseButtonClick(bannerId: Long) {
            accountsViewModel.dismissBanner(bannerId)
        }

        override fun onBackupBannerActionButtonClick() {
            accountsViewModel.navigateToBackUpPassphraseInfo()
        }

        override fun onBannerActionButtonClick(url: String, bannerType: BannerType) {
            accountsViewModel.logBannerClick(bannerType)
            when (bannerType) {
                BannerType.STAKING -> nav(AccountsFragmentDirections.actionAccountsFragmentToStakingFragment())
                BannerType.CARD -> nav(AccountsFragmentDirections.actionAccountsFragmentToCardsFragment())
                else -> nav(AccountsFragmentDirections.actionAccountsFragmentToBannerFragment(url))
            }
        }

        override fun onBuySellClick() {
            accountsViewModel.logAlgoBuyClick()
            navToBuySellActionsBottomSheet()
        }

        override fun onSendClick() {
            accountsViewModel.onSendTapEvent()
            navToSendAlgoNavigation()
        }

        override fun onSwapClick() {
            accountsViewModel.onSwapTapEvent()
        }

        override fun onScanQrClick() {
            accountsViewModel.logQrScanClick()
            navToQrScanFragment()
        }

        override fun onSortClick() {
            accountsViewModel.logSortClick()
            onArrangeListClick()
        }

        override fun onAddAccountClick() {
            accountsViewModel.onAddAccountClick()
        }

        override fun onStakingClick() {
            accountsViewModel.logEvent(PeraClickEvent.TAP_HOME_SCREEN_STAKE)
            nav(AccountsFragmentDirections.actionAccountsFragmentToStakingFragment())
        }
    }

    private val accountAdapter: AccountsAdapter = AccountsAdapter(accountAdapterListener = accountAdapterListener)

    private val accountListCollector: suspend (List<BaseAccountListItem>?) -> Unit = { accountList ->
        accountList?.let { safeList ->
            loadAccountsAndBalancePreview(safeList)
            (activity as MainActivity).hideProgress()
        }
    }

    private val emptyStateVisibilityCollector: suspend (Boolean?) -> Unit = { isEmptyStateVisible ->
        binding.emptyScreenStateView.isVisible = isEmptyStateVisible == true
        binding.notificationImageButton.isInvisible = isEmptyStateVisible == true
        binding.qrImageButton.isInvisible = isEmptyStateVisible == true
    }

    private val fullScreenLoadingCollector: suspend (Boolean?) -> Unit = { isFullScreenLoadingVisible ->
        binding.loadingProgressBar.isVisible = isFullScreenLoadingVisible == true
    }

    private val accountsPortfolioValuesCollector: suspend (BasePortfolioValueItem?) -> Unit = {
        if (it != null) setPortfolioValues(it)
    }

    private val portfolioValuesBackgroundColorCollector: suspend (Int?) -> Unit = {
        if (it != null) binding.toolbarLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, it))
    }

    private val successStateVisibilityCollector: suspend (Boolean?) -> Unit = { isVisible ->
        if (isVisible != null) {
            with(binding) {
                portfolioValueTitleTextView.isInvisible = !isVisible
                primaryPortfolioValue.isInvisible = !isVisible
                toolbarPrimaryPortfolioValue.isInvisible = !isVisible
                secondaryPortfolioValue.isInvisible = !isVisible
                toolbarSecondaryPortfolioValue.isInvisible = !isVisible
                accountsRecyclerView.isInvisible = !isVisible
                if (isVisible.not()) binding.accountsFragmentMotionLayout.transitionToState(R.id.start)
                accountsFragmentMotionLayout.getTransition(R.id.accountsFragmentTransition).isEnabled = isVisible
            }
        }
    }

    private val notificationStateCollector: suspend (Boolean?) -> Unit = { isActive ->
        if (isActive != null) {
            binding.notificationImageButton.isActivated = isActive
        }
    }

    private val assetInboxCountCollector: suspend (Int?) -> Unit = { assetInboxCountNullable ->
        val assetInboxCount = assetInboxCountNullable ?: 0
        binding.assetInboxAllAccountsButton.apply {
            text = resources.getQuantityString(R.plurals.asset_requests, assetInboxCount, assetInboxCount)
            isVisible = assetInboxCount > 0
        }
    }

    private fun showAccountAddressCopyTutorialDialog(tutorialId: Int) {
        accountsViewModel.dismissTutorial(tutorialId)
        binding.root.context.showCopyAccountAddressTutorialDialog()
    }

    private fun showSwapTutorialDialog(tutorialId: Int) {
        with(accountsViewModel) {
            accountsViewModel.dismissTutorial(tutorialId)
            binding.root.context.showSwapFeatureTutorialDialog(
                onTrySwap = ::onSwapClickFromTutorialDialog,
                onLater = ::logSwapLaterClick
            )
        }
    }

    private fun showGiftCardsTutorialDialog(tutorialId: Int) {
        accountsViewModel.dismissTutorial(tutorialId)
        binding.root.context.showGiftCardsTutorialDialog(
            onBuyGiftCards = ::navToBidali,
            onLater = { }
        )
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun setPortfolioValues(portfolioValues: BasePortfolioValueItem) {
        with(binding) {
            primaryPortfolioValue.apply { text = portfolioValues.getPrimaryAccountValue(context) }
            toolbarPrimaryPortfolioValue.apply { text = portfolioValues.getPrimaryAccountValue(context) }
            secondaryPortfolioValue.apply { text = portfolioValues.getSecondaryAccountValue(context) }
            toolbarSecondaryPortfolioValue.apply { text = portfolioValues.getSecondaryAccountValue(context) }
            portfolioValueTitleTextView.apply {
                setTextColor(ContextCompat.getColor(root.context, portfolioValues.titleColorResId))
                setDrawableTintColor(portfolioValues.titleColorResId)
                setOnClickListener { navToPortfolioInfoBottomSheet(portfolioValues) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerBottomNavBarFragmentDelegation(this)
        initObservers()
        initUi()
    }

    private fun initUi() {
        binding.accountsRecyclerView.apply {
            adapter = accountAdapter
            itemAnimator = null
            addItemDecoration(
                BannerViewTypesDividerItemDecoration(
                    BaseAccountListItem.bannerItemTypes,
                    resources.getDimensionPixelSize(R.dimen.spacing_normal)
                )
            )
        }
        binding.emptyScreenStateView.apply {
            setOnNeutralButtonClickListener(::onAddAccountClick)
            setupUi(accountsEmptyState)
        }
        binding.qrImageButton.setOnClickListener {
            accountsViewModel.logQrScanClick()
            navToQrScanFragment()
        }
        binding.notificationImageButton.setOnClickListener {
            accountsViewModel.logNotificationClick()
            navigateToNotifications()
        }
        binding.assetInboxAllAccountsButton.setOnClickListener { navToAssetInboxAllAccountsNavigation() }
    }

    override fun onResume() {
        super.onResume()
        accountsViewModel.refreshCachedAlgoPrice()
        initSavedStateListener()
    }

    private fun initSavedStateListener() {
        useFragmentResultListenerValue<Boolean>(ACCOUNT_SORT_RESULT_KEY) { isSortTypeChanged ->
            if (isSortTypeChanged) {
                accountsViewModel.initializeAccountPreviewFlow()
            }
        }
    }

    @Suppress("LongMethod")
    private fun initObservers() {
        with(accountsViewModel) {
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountPreviewFlow.map { it?.accountListItems },
                accountListCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountPreviewFlow.map { it?.isFullScreenAnimatedLoadingVisible },
                fullScreenLoadingCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountPreviewFlow.map { it?.isEmptyStateVisible },
                emptyStateVisibilityCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountPreviewFlow.map { it?.portfolioValueItem }.distinctUntilChanged(),
                accountsPortfolioValuesCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountPreviewFlow.map { it?.portfolioValuesBackgroundRes }.distinctUntilChanged(),
                portfolioValuesBackgroundColorCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountPreviewFlow.map { it?.isSuccessStateVisible }.distinctUntilChanged(),
                successStateVisibilityCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountPreviewFlow.map { it?.hasNewNotification }.distinctUntilChanged(),
                notificationStateCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountPreviewFlow.map { it?.assetInboxCount },
                assetInboxCountCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                accountsViewModel.viewEvent,
                viewEventCollector
            )
        }
    }

    private fun onAddAccountClick() {
        accountsViewModel.onAddAccountClick()
    }

    private fun loadAccountsAndBalancePreview(accountListItems: List<BaseAccountListItem>) {
        accountAdapter.submitList(accountListItems)
    }

    private fun navToQrScanFragment() {
        nav(HomeNavigationDirections.actionGlobalAccountsQrScannerFragment())
    }

    private fun navigateToNotifications() {
        nav(AccountsFragmentDirections.actionAccountsFragmentToNotificationCenterFragment())
    }

    private fun navToAssetInboxAllAccountsNavigation() {
        nav(AccountsFragmentDirections.actionAccountsFragmentToAssetInboxAllAccountsNavigation())
    }

    private fun onArrangeListClick() {
        nav(AccountsFragmentDirections.actionAccountsFragmentToStandardAccountOrderFragment())
    }

    private fun navToPortfolioInfoBottomSheet(portfolio: BasePortfolioValueItem) {
        nav(
            MainNavigationDirections.actionGlobalSingleButtonBottomSheet(
                titleAnnotatedString = AnnotatedString(R.string.how_we_calculate_portfolio),
                descriptionAnnotatedString = AnnotatedString(R.string.the_total_portfolio_value),
                errorAnnotatedString = portfolio.errorStringResId?.run { AnnotatedString(this) }
            )
        )
    }

    private fun navToBuySellActionsBottomSheet() {
        nav(AccountsFragmentDirections.actionAccountsFragmentToBuySellActionsBottomSheet())
    }

    private fun navToSendAlgoNavigation() {
        nav(AccountsFragmentDirections.actionGlobalSendAlgoNavigation(null))
    }

    private fun navToBackupPassphraseInfo(addresses: Set<String>) {
        nav(
            AccountsFragmentDirections.actionAccountsFragmentToBackupPassphraseInfoNavigation(
                addresses.toTypedArray(),
                OnboardingAccountType.Algo25
            )
        )
    }

    private fun navToLoginNavigation() {
        nav(MainNavigationDirections.actionGlobalLoginNavigation())
    }

    private fun navToBidali() {
        nav(AccountsFragmentDirections.actionAccountsFragmentToBidaliNavigation())
    }

    companion object {
        private const val FIREBASE_EVENT_SCREEN_ID = "screen_accounts"
    }
}
