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

@file:Suppress("TooManyFunctions") // TODO: We should remove this after function count decrease under 25

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

package com.algorand.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.algorand.android.HomeNavigationDirections.Companion.actionGlobalDiscoverHomeNavigation
import com.algorand.android.MainNavigationDirections.Companion.actionToLockPreferenceNavigation
import com.algorand.android.core.transaction.TransactionSignManager
import com.algorand.android.customviews.LedgerLoadingDialog
import com.algorand.android.customviews.alertview.ui.delegation.AlertDialogDelegation
import com.algorand.android.customviews.alertview.ui.delegation.AlertDialogDelegationImpl
import com.algorand.android.customviews.customsnackbar.CustomSnackbar
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.AssetAction
import com.algorand.android.models.AssetActionResult
import com.algorand.android.models.AssetOperationResult
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.Node
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.TransactionManagerResult
import com.algorand.android.models.TransactionSignData
import com.algorand.android.models.WalletConnectRequest
import com.algorand.android.models.WalletConnectRequest.WalletConnectArbitraryDataRequest
import com.algorand.android.models.WalletConnectRequest.WalletConnectTransaction
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountNavArgs
import com.algorand.android.modules.autolockmanager.ui.AutoLockManager
import com.algorand.android.modules.deeplink.ui.DeeplinkHandler
import com.algorand.android.modules.keyreg.ui.model.KeyRegTransactionDetail
import com.algorand.android.modules.perawebview.ui.BasePeraWebViewFragment
import com.algorand.android.modules.transaction.refactor.ui.AssetOperationViewModel
import com.algorand.android.modules.walletconnect.connectionrequest.ui.WalletConnectConnectionBottomSheet
import com.algorand.android.modules.walletconnect.connectionrequest.ui.model.WCSessionRequestResult
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionIdentifier
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionProposal
import com.algorand.android.modules.webimport.common.data.model.WebImportQrCode
import com.algorand.android.notification.domain.model.NotificationMetadata
import com.algorand.android.ui.accounts.AccountsQrScannerViewModel
import com.algorand.android.ui.accountselection.receive.ReceiveAccountSelectionFragment
import com.algorand.android.ui.xoswap.view.XoSwapFragment
import com.algorand.android.usecase.IsAccountLimitExceedUseCase.Companion.MAX_NUMBER_OF_ACCOUNTS
import com.algorand.android.utils.Event
import com.algorand.android.utils.Resource
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.collectOnLifecycle
import com.algorand.android.utils.getSafeParcelableExtra
import com.algorand.android.utils.inappreview.InAppReviewManager
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.walletconnect.WalletConnectUrlHandler
import com.algorand.android.utils.walletconnect.WalletConnectViewModel
import com.algorand.wallet.deeplink.model.DeepLink
import com.algorand.wallet.deeplink.model.NotificationGroupType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("LargeClass")
@AndroidEntryPoint
class MainActivity :
    CoreMainActivity(),
    WalletConnectConnectionBottomSheet.Callback,
    ReceiveAccountSelectionFragment.ReceiveAccountSelectionFragmentListener,
    AlertDialogDelegation by AlertDialogDelegationImpl() {

    private val mainViewEventCollector: suspend (MainViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is MainViewModel.ViewEvent.HandleAssetTransactionDeepLink -> navToAssetDetailNavigation(
                event.address,
                event.assetId
            )

            is MainViewModel.ViewEvent.HandleAssetOptInRequestDeepLink -> handleOptInDeeplink(
                AssetAction(
                    assetId = event.assetId,
                    publicKey = event.address
                )
            )

            is MainViewModel.ViewEvent.NavToAssetInboxOneAccountNavigation -> navToAssetInboxOneAccountNavigation(
                event.address
            )

            is MainViewModel.ViewEvent.NavToAccountDetailFragment -> navToAccountDetailFragment(
                event.address
            )

            is MainViewModel.ViewEvent.ShowForegroundNotification -> showForegroundNotification(
                event.notificationMetadata
            )

            is MainViewModel.ViewEvent.ShowDeeplinkAccountNotFoundError -> showDeeplinkAccountNotFoundError()

            is MainViewModel.ViewEvent.NavToWalletConnectArbitraryDataRequestNavigation ->
                navToWalletConnectArbitraryDataRequestNavigation(event.wcRequestId)

            is MainViewModel.ViewEvent.NavToWalletConnectTransactionRequestNavigation ->
                navToWalletConnectTransactionRequestNavigation(event.wcRequestId)

            is MainViewModel.ViewEvent.ShowLockSuggestion -> showLockSuggestion()

            is MainViewModel.ViewEvent.StartInAppReview -> startInAppReview()
            is MainViewModel.ViewEvent.ProcessNodeChange -> onNewNodeActivated()
            is MainViewModel.ViewEvent.NavToRecoverWithPassphraseNavigation ->
                navToRecoverWithPassphraseNavigation(event.mnemonic)

            MainViewModel.ViewEvent.ShowMaxAccountLimitExceededError -> showMaxAccountLimitExceededError()
            is MainViewModel.ViewEvent.NavToKeyRegTransactionFragment ->
                navToKeyRegTransactionFragment(event.transactionDetail)

            is MainViewModel.ViewEvent.ShowKeyRegDeeplinkError -> showKeyRegDeeplinkError(event.address)
            is MainViewModel.ViewEvent.NavToAssetDetailFragment -> navToAssetDetailNavigation(
                event.address,
                event.assetId
            )
        }
    }

    val mainViewModel: MainViewModel by viewModels()

    private val assetOperationViewModel: AssetOperationViewModel by viewModels()
    private val walletConnectViewModel: WalletConnectViewModel by viewModels()
    private val accountsQrScannerViewModel: AccountsQrScannerViewModel by viewModels()
    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    @Inject
    lateinit var transactionManager: TransactionSignManager

    @Inject
    lateinit var inAppReviewManager: InAppReviewManager

    private val autoLockManagerCollector: suspend (Event<AutoLockManager.AutoLockEvent>) -> Unit = {
        when (it.consume()) {
            AutoLockManager.AutoLockEvent.Lock -> navToLockFragment()
            AutoLockManager.AutoLockEvent.Unlock -> {
                navToLockFragmentPop()
                mainViewModel.handlePendingIntent(true)
            }

            AutoLockManager.AutoLockEvent.Idle, null -> Unit
        }
    }

    private val assetOperationResultCollector: suspend (Event<Resource<AssetOperationResult>>?) -> Unit = {
        it?.consume()?.use(
            onSuccess = { assetOperationResult -> showAssetOperationForegroundNotification(assetOperationResult) },
            onFailed = { error -> showGlobalError(errorMessage = error.parse(this), tag = activityTag) }
        )
    }

    private val assetTransactionDataCollector: suspend (Event<TransactionSignData>?) -> Unit = {
        it?.consume()?.let { transactionData ->
            sendAssetOperationTransaction(transactionData)
        }
    }

    private val newNotificationObserver = Observer<Event<NotificationMetadata>> {
        it.consume()?.let { newNotificationData ->
            if (!mainViewModel.isAppUnlocked()) {
                return@let
            }
            mainViewModel.handleNewNotification(newNotificationData)
        }
    }

    private val invalidTransactionCauseObserver = Observer<Event<Resource.Error.Local>> { cause ->
        cause.consume()?.let { onInvalidWalletConnectTransactionReceived(it) }
    }

    private val walletConnectUrlHandlerListener = object : WalletConnectUrlHandler.Listener {
        override fun onValidWalletConnectUrl(url: String) {
            if (!isBasePeraWebViewFragmentActive()) showProgress()
            walletConnectViewModel.connectToSessionByUrl(url)
        }

        override fun onInvalidWalletConnectUrl(errorResId: Int) {
            accountsQrScannerViewModel.setQrCodeInProgress(false)
            showGlobalError(errorMessage = getString(errorResId), tag = activityTag)
        }
    }

    private val walletConnectSessionSettleCollector: suspend (Event<WalletConnectSessionIdentifier>) -> Unit = {
        it.consume()?.let { sessionIdentifier ->
            if (!isBasePeraWebViewFragmentActive()) {
                nav(HomeNavigationDirections.actionGlobalWcConnectionLaunchBackBrowserBottomSheet(sessionIdentifier))
            }
        }
    }

    private val deepLinkHandlerListener = object : DeeplinkHandler.Listener {

        override fun onAssetTransferDeepLink(assetTransaction: AssetTransaction): Boolean {
            return true.also {
                navToSendAlgoNavigation(assetTransaction)
            }
        }

        override fun onAddContactDeepLink(address: String, label: String?): Boolean {
            return true.also {
                navToContactAdditionNavigation(address, label)
            }
        }

        override fun onAccountAddressDeeplink(address: String, label: String?): Boolean {
            return true.also {
                navToAccountsAddressScanActionBottomSheet(address, label)
            }
        }

        override fun onWalletConnectConnectionDeeplink(wcUrl: String): Boolean {
            return true.also {
                handleWalletConnectUrl(wcUrl)
            }
        }

        override fun onWebImportQrCodeDeepLink(webImportQrCode: WebImportQrCode): Boolean {
            return true.also {
                navToWebImportNavigation(webImportQrCode)
            }
        }

        override fun onAssetTransferWithNotOptInDeepLink(assetId: Long): Boolean {
            return true.also {
                navToAddAssetTryLaterBottomSheet(AssetAction(assetId))
            }
        }

        override fun onAssetOptInDeepLink(assetAction: AssetAction): Boolean {
            return true.also {
                handleOptInDeeplink(assetAction)
            }
        }

        override fun onNotificationDeepLink(
            accountAddress: String,
            assetId: Long,
            notificationGroupType: NotificationGroupType
        ): Boolean {
            handleNotificationDeepLink(accountAddress, assetId, notificationGroupType)
            return true
        }

        override fun onUndefinedDeepLink() {
            showInvalidDeeplinkError()
        }

        override fun onDeepLinkNotHandled(deepLink: DeepLink) {
            showInvalidDeeplinkError()
        }

        override fun onDiscoverBrowserDeepLink(webUrl: String): Boolean {
            navToDiscoverUrlViewerNavigation(webUrl)
            return true
        }

        override fun onDiscoverDeepLink(path: String): Boolean {
            navToDiscoverWithPath(path)
            return true
        }

        override fun onCardsDeepLink(path: String?): Boolean {
            navToCardsFragment(path)
            return true
        }

        override fun onStakingDeepLink(path: String?): Boolean {
            navToStakingFragment(path)
            return true
        }

        override fun onAssetInboxDeepLink(accountAddress: String): Boolean {
            handleAssetInboxDeepLink(accountAddress)
            return true
        }

        override fun onKeyRegDeeplink(deepLink: DeepLink.KeyReg): Boolean {
            handleKeyRegDeepLink(deepLink)
            return true
        }

        override fun onRecoverAccountDeepLink(mnemonic: String): Boolean {
            handleRecoverAccountDeeplink(mnemonic)
            return true
        }

        override fun onAccountDetailDeepLink(address: String): Boolean {
            handleAccountDetailDeeplink(address)
            return true
        }

        override fun onAddWatchAccountDeepLink(address: String, label: String?): Boolean {
            navToRegisterWatchAccountNavigation(address)
            return true
        }

        override fun onAddressActionsDeepLink(address: String, label: String?): Boolean {
            navToAccountsAddressScanActionBottomSheet(address, label)
            return true
        }

        override fun onAssetDetailDeepLink(address: String, assetId: Long): Boolean {
            mainViewModel.handleAssetDetailDeeplink(address, assetId)
            return true
        }

        override fun onBuyDeepLink(address: String, path: String?): Boolean {
            navToOnramp(address, path)
            return true
        }

        override fun onSellDeepLink(address: String): Boolean {
            navToBidaliNavigation(address)
            return true
        }

        override fun onSwapDeepLink(address: String, assetInId: Long?, assetOutId: Long?): Boolean {
            navToSwapNavigation(
                address = address,
                assetInId = assetInId,
                assetOutId = assetOutId
            )
            return true
        }

        override fun onHomeDeeplink(): Boolean {
            handleHomeDeeplink()
            return true
        }
    }

    private val transactionManagerResultObserver = Observer<Event<TransactionManagerResult>?> {
        it?.consume()?.let { result ->
            when (result) {
                is TransactionManagerResult.Success -> {
                    hideLedgerLoadingDialog()
                    val signedTransactionDetail = result.signedTransactionDetail
                    if (signedTransactionDetail is SignedTransactionDetail.AssetOperation) {
                        assetOperationViewModel.sendAssetOperationSignedTransaction(signedTransactionDetail)
                    }
                }

                is TransactionManagerResult.Error.GlobalWarningError -> {
                    hideLedgerLoadingDialog()
                    val (title, errorMessage) = result.getMessage(this)
                    showGlobalError(title = title, errorMessage = errorMessage, tag = activityTag)
                }

                is TransactionManagerResult.Error.SnackbarError -> {
                    hideLedgerLoadingDialog()
                    CustomSnackbar.Builder()
                        .setTitleTextResId(result.titleResId)
                        .setDescriptionTextResId(result.descriptionResId)
                        .setActionButtonTextResId(result.buttonTextResId)
                        .setActionButtonClickListener {
                            retryLatestAssetAdditionTransaction().also { dismiss() }
                        }
                        .build()
                        .show(binding.root)
                }

                is TransactionManagerResult.LedgerWaitingForApproval -> showLedgerLoadingDialog(result.bluetoothName)
                is TransactionManagerResult.Loading -> showProgress()
                is TransactionManagerResult.LedgerScanFailed -> {
                    hideLedgerLoadingDialog()
                    navToLedgerConnectionIssueBottomSheet()
                }

                else -> {
                    sendErrorLog("Unhandled else case in transactionManagerResultLiveData")
                }
            }
        }
    }

    private val ledgerLoadingDialogListener = LedgerLoadingDialog.Listener { shouldStopResources ->
        hideLedgerLoadingDialog()
        if (shouldStopResources) {
            transactionManager.manualStopAllResources()
        }
    }

    private val alertDialogDelegationListener = AlertDialogDelegationImpl.Listener { deepLinkUri ->
        handleDeepLink(deepLinkUri)
    }

    private val activeNodeCollector: suspend (Node?) -> Unit = { activatedNode ->
        checkIfConnectedToTestNet(activatedNode)
    }

    private val sessionResultFlowCollector: suspend (Event<Resource<WalletConnectSessionProposal>>) -> Unit = { event ->
        event.consume()?.use(
            onSuccess = ::onSessionConnected,
            onFailed = ::onSessionFailed,
            onLoading = ::showProgress,
            onLoadingFinished = ::hideProgress
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }

    override fun initializeMainActivity(savedInstanceState: Bundle?) {
        mainViewModel.initializeApp(lifecycle)
        mainViewModel.fetchInstallReferrer()
        mainViewModel.setDeepLinkHandlerListener(deepLinkHandlerListener)

        initObservers()
        registerAlertDialogDelegation(this, alertDialogDelegationListener)

        if (savedInstanceState == null) {
            handleDeeplinkAndNotificationNavigation()
        }

        mainViewModel.increaseAppOpeningCount()
    }

    override fun onMenuItemClicked(item: MenuItem) {
        when (item.itemId) {
            R.id.accountsFragment -> mainViewModel.logHomeClick()
            R.id.discoverHomeNavigation -> mainViewModel.logDiscoverClick()
            R.id.stakingFragment -> mainViewModel.logStakeClick()
            R.id.collectiblesFragment -> mainViewModel.logCollectiblesClick()
            R.id.menuFragment -> mainViewModel.logMenuClick()
        }
    }

    override fun observeAutoLockManager() {
        collectLatestOnLifecycle(
            flow = autoLockManager.eventFlow,
            collection = autoLockManagerCollector
        )
    }

    override fun onAccountSelected(address: String) {
        val qrCodeTitle = getString(R.string.qr_code)
        navToShowQrNavigation(qrCodeTitle, address)
    }

    override fun onSessionRequestResult(wCSessionRequestResult: WCSessionRequestResult) {
        with(walletConnectViewModel) {
            when (wCSessionRequestResult) {
                is WCSessionRequestResult.ApproveRequest -> approveSession(wCSessionRequestResult)
                is WCSessionRequestResult.RejectRequest -> rejectSession(wCSessionRequestResult.sessionProposal)
                is WCSessionRequestResult.RejectScamRequest -> rejectScamSession(wCSessionRequestResult.sessionProposal)
            }
        }
    }

    fun handleDeepLink(uri: String) {
        mainViewModel.handleDeepLink(uri)
    }

    fun handleHomeDeeplink() {
        navToHome()
    }

    fun handleAssetInboxDeepLink(accountAddress: String) {
        mainViewModel.handleAssetInboxDeepLink(accountAddress)
    }

    fun handleNotificationDeepLink(
        accountAddress: String,
        assetId: Long,
        notificationGroupType: NotificationGroupType
    ) {
        mainViewModel.handleNotificationDeepLink(accountAddress, assetId, notificationGroupType)
    }

    fun handleWalletConnectUrl(walletConnectUrl: String) {
        walletConnectViewModel.handleWalletConnectUrl(
            url = walletConnectUrl,
            listener = walletConnectUrlHandlerListener
        )
    }

    fun handleKeyRegDeepLink(deepLink: DeepLink.KeyReg) {
        mainViewModel.handleKeyRegDeepLink(deepLink)
    }

    fun handleRecoverAccountDeeplink(mnemonic: String) {
        mainViewModel.onRecoverAccountDeepLink(mnemonic)
    }

    fun handleOptInDeeplink(assetAction: AssetAction) {
        if (assetAction.publicKey != null) {
            navToAssetAdditionActionNavigation(assetAction)
        } else {
            navToAddAssetAccountSelectionFragment(assetAction.assetId)
        }
    }

    fun handleAssetDetailDeeplink(address: String, assetId: Long) {
        mainViewModel.handleAssetDetailDeeplink(address, assetId)
    }

    fun handleAccountDetailDeeplink(address: String) {
        mainViewModel.handleAccountDetailDeeplink(address)
    }

    fun isBasePeraWebViewFragmentActive(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navigationHostFragment.id)
        val currentFragment = (navHostFragment as NavHostFragment).childFragmentManager.fragments.first()
        return currentFragment is BasePeraWebViewFragment || currentFragment is XoSwapFragment
    }

    fun launchIntentWithUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri.toUri()
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    fun signAddAssetTransaction(assetActionResult: AssetActionResult) {
        assetOperationViewModel.createAddAssetTransaction(assetActionResult)
    }

    fun signRemoveAssetTransaction(assetActionResult: AssetActionResult) {
        assetOperationViewModel.createRemoveAssetTransaction(assetActionResult)
    }

    fun showMaxAccountLimitExceededError() {
        showGlobalError(
            title = getString(R.string.too_many_accounts),
            errorMessage = getString(R.string.looks_like_already_have_accounts, MAX_NUMBER_OF_ACCOUNTS),
            tag = activityTag
        )
    }

    fun navToDiscoverWithPath(path: String) {
        binding.apply {
            nav(
                actionGlobalDiscoverHomeNavigation(mainViewModel.getDiscoverUrlWithPath(path))
            )
        }
    }

    private fun navToHome() {
        if (navController.currentDestination?.id != R.id.accountsFragment) {
            nav(MainNavigationDirections.actionGlobalMainNavigation())
        }
    }

    fun navToContactAdditionNavigation(address: String, label: String?) {
        nav(
            HomeNavigationDirections.actionGlobalContactAdditionNavigation(
                contactPublicKey = address,
                contactName = label
            )
        )
    }

    fun navToSendAlgoNavigation(assetTransaction: AssetTransaction) {
        nav(HomeNavigationDirections.actionGlobalSendAlgoNavigation(assetTransaction))
    }

    private fun navToAssetInboxOneAccountNavigation(accountAddress: String) {
        nav(
            HomeNavigationDirections.actionGlobalAssetInboxOneAccountNavigation(
                AssetInboxOneAccountNavArgs(
                    accountAddress
                )
            )
        )
    }

    private fun navToAccountDetailFragment(address: String) {
        nav(
            HomeNavigationDirections.actionGlobalAccountDetailFragment(address)
        )
    }

    fun navToRegisterWatchAccountNavigation(address: String) {
        nav(
            HomeNavigationDirections.actionGlobalRegisterWatchAccountNavigation(address)
        )
    }

    fun navToAccountsAddressScanActionBottomSheet(address: String, label: String?) {
        nav(
            HomeNavigationDirections
                .actionGlobalAccountsAddressScanActionBottomSheet(address, label)
        )
    }

    fun navToOnramp(address: String, path: String?) {
        if (mainViewModel.isXoSwapFeatureEnabled()) {
            navToXoSwapFragment(path)
        } else {
            nav(HomeNavigationDirections.actionGlobalMeldNavigation(address))
        }
    }

    fun navToBidaliNavigation(address: String) {
        nav(
            HomeNavigationDirections.actionGlobalBidaliNavigation(address)
        )
    }

    fun navToInternalBrowser(url: String) {
        nav(
            HomeNavigationDirections.actionGlobalDiscoverUrlViewerNavigation(url)
        )
    }

    fun navToSwapNavigation(address: String, assetInId: Long?, assetOutId: Long?) {
        nav(HomeNavigationDirections.actionGlobalSwapV2Navigation(address, assetInId ?: -1L, assetOutId ?: -1L))
    }

    fun navToCardsFragment(path: String? = null) {
        nav(HomeNavigationDirections.actionGlobalCardsFragment(path))
    }

    fun navToStakingFragment(path: String? = null) {
        nav(HomeNavigationDirections.actionGlobalStakingFragment(path))
    }

    private fun navToRecoverWithPassphraseNavigation(mnemonic: String) {
        nav(HomeNavigationDirections.actionGlobalRecoverWithPassphraseNavigation(mnemonic))
    }

    private fun navToKeyRegTransactionFragment(transactionDetail: KeyRegTransactionDetail) {
        nav(HomeNavigationDirections.actionGlobalKeyRegTransactionFragment(transactionDetail))
    }

    fun navToAddAssetTryLaterBottomSheet(assetAction: AssetAction) {
        nav(
            HomeNavigationDirections.actionGlobalUnsupportedAddAssetTryLaterBottomSheet(assetAction)
        )
    }

    fun navToDiscoverUrlViewerNavigation(webUrl: String) {
        nav(HomeNavigationDirections.actionGlobalDiscoverUrlViewerNavigation(webUrl))
    }

    fun navToWebImportNavigation(webImportQrCode: WebImportQrCode) {
        nav(
            HomeNavigationDirections.actionGlobalWebImportNavigation(webImportQrCode)
        )
    }

    private fun navToAssetDetailNavigation(accountAddress: String, assetId: Long) {
        nav(
            HomeNavigationDirections.actionGlobalAssetDetailNavigation(
                assetId = assetId,
                accountAddress = accountAddress
            )
        )
    }

    private fun navToLockPreferenceNavigation() {
        nav(actionToLockPreferenceNavigation())
    }

    private fun showKeyRegDeeplinkError(accountAddress: String) {
        showGlobalError(
            errorMessage = getString(R.string.you_dont_have_any, accountAddress),
            tag = activityTag
        )
    }

    private fun startInAppReview() {
        val isStarted = inAppReviewManager.start(this@MainActivity)
        if (!isStarted) {
            mainViewModel.startAutoLockSuggestion()
        }
    }

    private fun showLockSuggestion() {
        navToLockPreferenceNavigation()
    }

    private fun navToAddAssetAccountSelectionFragment(assetId: Long) {
        nav(
            HomeNavigationDirections.actionGlobalAddAssetAccountSelectionFragment(assetId)
        )
    }

    private fun navToShowQrNavigation(qrCodeTitle: String, address: String) {
        nav(
            HomeNavigationDirections.actionGlobalShowQrNavigation(qrCodeTitle, address)
        )
    }

    private fun navToLockFragment() {
        nav(MainNavigationDirections.actionGlobalLockFragment())
    }

    private fun navToLockFragmentPop() {
        nav(MainNavigationDirections.actionGlobalLockFragmentPop())
    }

    private fun navToAssetAdditionActionNavigation(assetAction: AssetAction) {
        nav(
            HomeNavigationDirections.actionGlobalAssetAdditionActionNavigation(assetAction)
        )
    }

    private fun navToGlobalSingleButtonBottomSheet(annotatedDescriptionErrorString: AnnotatedString) {
        nav(
            MainNavigationDirections.actionGlobalSingleButtonBottomSheet(
                titleAnnotatedString = AnnotatedString(R.string.uh_oh_something),
                drawableResId = R.drawable.ic_error,
                drawableTintResId = R.color.error_tint_color,
                descriptionAnnotatedString = annotatedDescriptionErrorString,
                isDraggable = false
            )
        )
    }

    private fun navToWalletConnectTransactionRequestNavigation(wcRequestId: Long) {
        nav(
            directions = MainNavigationDirections.actionGlobalWalletConnectTransactionRequestNavigation(
                shouldSkipConfirmation = isBasePeraWebViewFragmentActive()
            ),
            onError = { saveWcTransactionToPendingIntent(wcRequestId) }
        )
    }

    private fun navToWalletConnectArbitraryDataRequestNavigation(wcRequestId: Long) {
        nav(
            directions = MainNavigationDirections.actionGlobalWalletConnectArbitraryDataRequestNavigation(
                shouldSkipConfirmation = isBasePeraWebViewFragmentActive()
            ),
            onError = { saveWcTransactionToPendingIntent(wcRequestId) }
        )
    }

    private fun navToWalletConnectSessionTimeoutDialog() {
        hideProgress()
        nav(
            MainNavigationDirections.actionGlobalSingleButtonBottomSheet(
                titleAnnotatedString = AnnotatedString(R.string.connection_failed),
                drawableResId = R.drawable.ic_error,
                drawableTintResId = R.color.error_tint_color,
                descriptionAnnotatedString = AnnotatedString(R.string.we_are_sorry_but_the),
            )
        )
    }

    private fun navToWalletConnectSessionScamDialog() {
        hideProgress()
        nav(
            MainNavigationDirections.actionGlobalSingleButtonBottomSheet(
                titleAnnotatedString = AnnotatedString(R.string.malicious_website_blocked),
                drawableResId = R.drawable.ic_error,
                drawableTintResId = R.color.error_tint_color,
                descriptionAnnotatedString = AnnotatedString(R.string.you_attempted_to_connect_malicious_website),
            )
        )
    }

    private fun navToLedgerConnectionIssueBottomSheet() {
        nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
    }

    private fun retryLatestAssetAdditionTransaction() {
        assetOperationViewModel.getLatestAddAssetTransaction()?.let { transactionData ->
            sendAssetOperationTransaction(transactionData)
        }
    }

    private fun onSessionConnected(wcSessionRequest: WalletConnectSessionProposal) {
        nav(HomeNavigationDirections.actionGlobalWalletConnectConnectionNavigation(wcSessionRequest))
    }

    private fun onSessionFailed(error: Resource.Error) {
        accountsQrScannerViewModel.setQrCodeInProgress(false)
        val errorMessage = error.parse(this)
        showGlobalError(errorMessage = errorMessage, tag = activityTag)
    }

    private fun showAssetOperationForegroundNotification(assetOperationResult: AssetOperationResult) {
        val safeAssetName = assetOperationResult.assetName.getName(resources)
        val messageDescription = getString(assetOperationResult.resultTitleResId, safeAssetName)
        showAlertSuccess(title = messageDescription, description = null, tag = activityTag)
    }

    private fun initObservers() {
        peraNotificationManager.newNotificationLiveData.observe(this, newNotificationObserver)

        collectLatestOnLifecycle(
            flow = assetOperationViewModel.assetOperationResultFlow,
            collection = assetOperationResultCollector
        )

        collectLatestOnLifecycle(
            flow = assetOperationViewModel.assetTransactionDataFlow,
            collection = assetTransactionDataCollector
        )

        transactionManager.transactionManagerResultLiveData.observe(this, transactionManagerResultObserver)

        walletConnectViewModel.walletConnectRequestLiveData.observe(this, ::handleWalletConnectRequest)

        walletConnectViewModel.invalidTransactionCauseLiveData.observe(this, invalidTransactionCauseObserver)

        collectOnLifecycle(
            flow = walletConnectViewModel.sessionResultFlow,
            collection = sessionResultFlowCollector
        )

        walletConnectViewModel.setWalletConnectSessionTimeoutListener(::onWalletConnectSessionTimedOut)

        collectLatestOnLifecycle(
            walletConnectViewModel.sessionSettleFlow,
            walletConnectSessionSettleCollector
        )

        collectLatestOnLifecycle(
            flow = mainViewModel.activeNodeFlow,
            collection = activeNodeCollector
        )

        collectLatestOnLifecycle(
            mainViewModel.viewEvent,
            mainViewEventCollector
        )
    }

    private fun onInvalidWalletConnectTransactionReceived(error: Resource.Error) {
        val annotatedDescriptionErrorString = AnnotatedString(
            stringResId = R.string.your_walletconnect_request_failed,
            replacementList = listOf("error_message" to error.parse(this).toString())
        )
        navToGlobalSingleButtonBottomSheet(annotatedDescriptionErrorString)
    }

    private fun handleWalletConnectRequest(requestEvent: Event<Resource<WalletConnectRequest>>?) {
        requestEvent?.consume()?.use(onSuccess = ::onNewWalletConnectRequest)
    }

    private fun onNewWalletConnectRequest(wcRequest: WalletConnectRequest) {
        if (mainViewModel.isAppUnlocked()) {
            when (wcRequest) {
                is WalletConnectTransaction -> {
                    navToWalletConnectTransactionRequestNavigation(wcRequest.requestId)
                }

                is WalletConnectArbitraryDataRequest -> {
                    navToWalletConnectArbitraryDataRequestNavigation(wcRequest.requestId)
                }
            }
        } else {
            saveWcTransactionToPendingIntent(wcRequest.requestId)
        }
    }

    private fun saveWcTransactionToPendingIntent(transactionRequestId: Long) {
        val pendingIntent = Intent().apply {
            putExtra(WC_TRANSACTION_ID_INTENT_KEY, transactionRequestId)
        }
        mainViewModel.setPendingIntent(pendingIntent)
    }

    private fun handleDeeplinkAndNotificationNavigation() {
        intent.getSafeParcelableExtra<Intent?>(DEEPLINK_AND_NAVIGATION_INTENT)?.apply {
            mainViewModel.setPendingIntent(this)
            handlePendingIntent()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val pendingIntent = intent.getSafeParcelableExtra<Intent?>(DEEPLINK_AND_NAVIGATION_INTENT)
        mainViewModel.setPendingIntent(pendingIntent)
        handlePendingIntent()
    }

    private fun handlePendingIntent() {
        return mainViewModel.handlePendingIntent()
    }

    private fun onNewNodeActivated() {
        hideProgress()
        mainViewModel.onNewNodeActivated(lifecycle)
    }

    private fun rejectScamSession(sessionProposal: WalletConnectSessionProposal) {
        walletConnectViewModel.rejectSession(sessionProposal)
        navToWalletConnectSessionScamDialog()
    }

    private fun sendAssetOperationTransaction(transactionData: TransactionSignData) {
        transactionManager.setup(lifecycle)
        transactionManager.initSigningTransactions(
            isGroupTransaction = false,
            transactionData
        )
    }

    private fun onWalletConnectSessionTimedOut() {
        navToWalletConnectSessionTimeoutDialog()
    }

    private fun hideLedgerLoadingDialog() {
        hideProgress()
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }

    private fun showLedgerLoadingDialog(ledgerName: String?) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(ledgerName, ledgerLoadingDialogListener)
            ledgerLoadingDialog?.showWithStateCheck(supportFragmentManager)
        }
    }

    private fun showForegroundNotification(newNotificationData: NotificationMetadata) {
        showForegroundNotification(notificationMetadata = newNotificationData, tag = activityTag)
    }

    private fun showDeeplinkAccountNotFoundError() {
        showGlobalError(errorMessage = getString(R.string.you_cannot_take), tag = activityTag)
    }

    private fun showInvalidDeeplinkError() {
        showGlobalError(errorMessage = getString(R.string.invalid_link_found), tag = activityTag)
    }

    fun navToXoSwapFragment(path: String?) {
        nav(HomeNavigationDirections.actionGlobalXoSwapFragment(path.orEmpty()))
    }

    companion object {
        fun newIntentWithDeeplinkOrNavigation(
            context: Context,
            deepLinkIntent: Intent
        ): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(DEEPLINK_AND_NAVIGATION_INTENT, deepLinkIntent)
            }
        }

        const val DEEPLINK_KEY: String = "deeplinkKey"
        const val DEEPLINK_AND_NAVIGATION_INTENT: String = "deeplinkNavIntent"
        const val WC_TRANSACTION_ID_INTENT_KEY: String = "wcTransactionId"
        const val WC_ARBITRARY_DATA_ID_INTENT_KEY: String = "wcArbitraryDataId"
    }
}
