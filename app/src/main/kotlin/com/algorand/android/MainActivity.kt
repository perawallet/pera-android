/*
 * Copyright 2025 Pera Wallet, LDA
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
 * Copyright 2025 Pera Wallet, LDA
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
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.algorand.android.HomeNavigationDirections.Companion.actionGlobalDiscoverHomeNavigation
import com.algorand.android.MainNavigationDirections.Companion.actionToLockPreferenceNavigation
import com.algorand.android.core.transaction.TransactionSignManager
import com.algorand.android.customviews.CoreActionsTabBarView
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
import com.algorand.android.modules.firebase.token.model.FirebaseTokenResult
import com.algorand.android.modules.keyreg.ui.model.KeyRegTransactionDetail
import com.algorand.android.modules.perawebview.ui.BasePeraWebViewFragment
import com.algorand.android.modules.qrscanning.QrScannerViewModel
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.modules.transaction.refactor.ui.AssetOperationViewModel
import com.algorand.android.modules.walletconnect.connectionrequest.ui.WalletConnectConnectionBottomSheet
import com.algorand.android.modules.walletconnect.connectionrequest.ui.model.WCSessionRequestResult
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionIdentifier
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionProposal
import com.algorand.android.notification.domain.model.NotificationMetadata
import com.algorand.android.ui.accountselection.receive.ReceiveAccountSelectionFragment
import com.algorand.android.usecase.IsAccountLimitExceedUseCase.Companion.MAX_NUMBER_OF_ACCOUNTS
import com.algorand.android.utils.Event
import com.algorand.android.utils.Resource
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.collectOnLifecycle
import com.algorand.android.utils.getSafeParcelableExtra
import com.algorand.android.utils.inappreview.InAppReviewManager
import com.algorand.android.utils.navigateSafe
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.showWithStateCheck
import com.algorand.android.utils.walletconnect.WalletConnectUrlHandler
import com.algorand.android.utils.walletconnect.WalletConnectViewModel
import com.algorand.wallet.cache.domain.model.AppCacheStatus
import com.algorand.wallet.deeplink.model.DeepLink
import com.algorand.wallet.deeplink.model.NotificationGroupType
import com.google.firebase.analytics.FirebaseAnalytics
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
            is MainViewModel.ViewEvent.HandleAssetTransactionDeepLink -> navToAssetProfileNavigation(
                event.address,
                event.assetId
            )

            is MainViewModel.ViewEvent.HandleAssetOptInRequestDeepLink -> navToAssetAdditionActionNavigation(
                event.address,
                event.assetId
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

            is MainViewModel.ViewEvent.ShowGlobalNotificationError -> showGlobalNotificationError()

            is MainViewModel.ViewEvent.NavToWalletConnectArbitraryDataRequestNavigation ->
                navToWalletConnectArbitraryDataRequestNavigation(event.wcRequestId)

            is MainViewModel.ViewEvent.NavToWalletConnectTransactionRequestNavigation ->
                navToWalletConnectTransactionRequestNavigation(event.wcRequestId)

            is MainViewModel.ViewEvent.ShowLockSuggestion -> showLockSuggestion()

            is MainViewModel.ViewEvent.StartInAppReview -> startInAppReview()
        }
    }

    private fun startInAppReview() {
        val isStarted = inAppReviewManager.start(this@MainActivity)
        if (!isStarted) {
            mainViewModel.startAutoLockSuggestion()
        }
    }

    private fun showLockSuggestion() {
        nav(actionToLockPreferenceNavigation())
    }

    private val qrScannerViewEventCollector: suspend (QrScannerViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is QrScannerViewModel.ViewEvent.NavigateToKeyRegTransactionFragment -> navToKeyRegTransactionFragment(
                event.transactionDetail
            )

            is QrScannerViewModel.ViewEvent.ShowKeyRegDeeplinkError -> showKeyRegDeeplinkError(
                event.address
            )
        }
    }

    val mainViewModel: MainViewModel by viewModels()
    val assetOperationViewModel: AssetOperationViewModel by viewModels()
    private val coreActionsTabBarViewModel: CoreActionsTabBarViewModel by viewModels()
    private val walletConnectViewModel: WalletConnectViewModel by viewModels()
    private val qrScannerViewModel: QrScannerViewModel by viewModels()

    private var ledgerLoadingDialog: LedgerLoadingDialog? = null

    @Inject
    lateinit var transactionManager: TransactionSignManager

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var inAppReviewManager: InAppReviewManager

    private val autoLockManagerListener = object : AutoLockManager.AutoLockManagerListener {
        override fun onLock() {
            nav(MainNavigationDirections.actionGlobalLockFragment())
        }

        override fun onUnlock() {
            nav(MainNavigationDirections.actionGlobalLockFragmentPop())
            mainViewModel.handlePendingIntent(true)
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

    private val appCacheStatusCollector: suspend (AppCacheStatus) -> Unit = {
        mainViewModel.isAssetSetupCompleted = it == AppCacheStatus.INITIALIZED
        binding.coreActionsTabBarView.setCoreActionButtonEnabled(it == AppCacheStatus.INITIALIZED)
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
        cause.consume()?.let { onInvalidWalletConnectTransacitonReceived(it) }
    }

    private val swapNavigationDirectionCollector: suspend (Event<NavDirections>?) -> Unit = {
        it?.consume()?.let { navDirection -> nav(navDirection) }
    }

    private val walletConnectUrlHandlerListener = object : WalletConnectUrlHandler.Listener {
        override fun onValidWalletConnectUrl(url: String) {
            if (!isBasePeraWebViewFragmentActive()) showProgress()
            walletConnectViewModel.connectToSessionByUrl(url)
        }

        override fun onInvalidWalletConnectUrl(errorResId: Int) {
            qrScannerViewModel.setQrCodeInProgress(false)
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
                navController.navigateSafe(HomeNavigationDirections.actionGlobalSendAlgoNavigation(assetTransaction))
            }
        }

        override fun onAccountAddressDeeplink(accountAddress: String, label: String?): Boolean {
            return true.also {
                navController.navigateSafe(
                    HomeNavigationDirections.actionGlobalAccountsAddressScanActionBottomSheet(accountAddress, label)
                )
            }
        }

        override fun onWalletConnectConnectionDeeplink(wcUrl: String): Boolean {
            return true.also {
                handleWalletConnectUrl(wcUrl)
            }
        }

        override fun onAssetTransferWithNotOptInDeepLink(assetId: Long): Boolean {
            return true.also {
                val assetAction = AssetAction(assetId = assetId)
                navController.navigateSafe(
                    HomeNavigationDirections.actionGlobalUnsupportedAddAssetTryLaterBottomSheet(assetAction)
                )
            }
        }

        override fun onAssetOptInDeepLink(assetAction: AssetAction): Boolean {
            return true.also {
                navController.navigateSafe(
                    HomeNavigationDirections.actionGlobalAddAssetAccountSelectionFragment(assetAction.assetId)
                )
            }
        }

        override fun onNotificationDeepLink(
            accountAddress: String,
            assetId: Long,
            notificationGroupType: NotificationGroupType
        ): Boolean {
            mainViewModel.handleNotificationDeepLink(accountAddress, assetId, notificationGroupType)
            return true
        }

        override fun onUndefinedDeepLink(deepLink: DeepLink.Undefined) {
            // TODO show error after discussing with the team
        }

        override fun onDeepLinkNotHandled(deepLink: DeepLink) {
            // TODO show error after discussing with the team
        }

        override fun onDiscoverBrowserDeepLink(webUrl: String): Boolean {
            navToDiscoverUrlViewerNavigation(webUrl)
            return true
        }

        override fun onDiscoverDeepLink(path: String): Boolean {
            navToDiscoverWithPath(path)
            return true
        }

        override fun onCardsDeepLink(path: String): Boolean {
            navToCardsFragment(path)
            return true
        }

        override fun onStakingDeepLink(path: String): Boolean {
            navToStakingFragment(path)
            return true
        }

        override fun onAssetInboxDeepLink(
            accountAddress: String,
            notificationGroupType: NotificationGroupType
        ): Boolean {
            return true.also {
                mainViewModel.handleAssetInboxDeepLink(accountAddress)
            }
        }

        override fun onKeyRegDeeplink(deepLink: DeepLink.KeyReg): Boolean {
            return true.also {
                qrScannerViewModel.handleKeyRegDeepLink(deepLink)
            }
        }
    }

    private fun navToAccountDetailFragment(accountAddress: String) {
        navController.navigateSafe(
            HomeNavigationDirections.actionGlobalAccountDetailFragment(
                accountAddress
            )
        )
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
                    navigateToConnectionIssueBottomSheet()
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

    private val firebaseTokenResultCollector: suspend (FirebaseTokenResult) -> Unit = { firebaseTokenResult ->
        when (firebaseTokenResult) {
            FirebaseTokenResult.TokenLoaded -> onNewNodeActivated()
            FirebaseTokenResult.TokenLoading -> Unit
            FirebaseTokenResult.TokenFailed -> Unit
        }
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
        mainViewModel.initializeApp(lifecycle)
        mainViewModel.fetchInstallReferrer()
        mainViewModel.setDeepLinkHandlerListener(deepLinkHandlerListener)
        mainViewModel.setAutoLockManagerListener(autoLockManagerListener)
        setupCoreActionsTabBarView()

        initObservers()
        registerAlertDialogDelegation(this, alertDialogDelegationListener)

        if (savedInstanceState == null) {
            handleDeeplinkAndNotificationNavigation()
        }

        mainViewModel.increaseAppOpeningCount()
    }

    override fun onMenuItemClicked(item: MenuItem) {
        when (item.itemId) {
            R.id.accountsFragment -> mainViewModel.logEvent(PeraClickEvent.TAP_LOWERMENU_HOME)
            R.id.discoverHomeNavigation -> mainViewModel.logEvent(PeraClickEvent.TAP_LOWERMENU_DISCOVER)
            R.id.collectiblesFragment -> mainViewModel.logEvent(PeraClickEvent.TAP_LOWERMENU_NFTS)
            R.id.settingsFragment -> mainViewModel.logEvent(PeraClickEvent.TAP_LOWERMENU_SETTINGS)
        }
    }

    override fun onAccountSelected(publicKey: String) {
        val qrCodeTitle = getString(R.string.qr_code)
        nav(HomeNavigationDirections.actionGlobalShowQrNavigation(qrCodeTitle, publicKey))
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

    fun handleWalletConnectUrl(walletConnectUrl: String) {
        walletConnectViewModel.handleWalletConnectUrl(
            url = walletConnectUrl,
            listener = walletConnectUrlHandlerListener
        )
    }

    fun isBasePeraWebViewFragmentActive(): Boolean {
        return (supportFragmentManager.findFragmentById(binding.navigationHostFragment.id) as NavHostFragment)
            .childFragmentManager.fragments.first() is BasePeraWebViewFragment
    }

    fun signAddAssetTransaction(assetActionResult: AssetActionResult) {
        assetOperationViewModel.createAddAssetTransaction(assetActionResult)
    }

    fun signRemoveAssetTransaction(assetActionResult: AssetActionResult) {
        assetOperationViewModel.createRemoveAssetTransaction(assetActionResult)
    }

    fun navToCardsFragment(path: String? = null) {
        nav(HomeNavigationDirections.actionGlobalCardsFragment(path))
    }

    fun navToStakingFragment(path: String? = null) {
        nav(HomeNavigationDirections.actionGlobalStakingFragment(path))
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
            coreActionsTabBarView.hideWithAnimation()
            bottomNavigationView.menu.findItem(R.id.discoverHomeNavigation).isChecked = true
            navController.navigateSafe(
                actionGlobalDiscoverHomeNavigation(
                    coreActionsTabBarViewModel.getDiscoverUrlWithPath(path)
                )
            )
        }
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
        qrScannerViewModel.setQrCodeInProgress(false)
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

        collectLatestOnLifecycle(
            flow = mainViewModel.appCacheStatusFlow,
            collection = appCacheStatusCollector
        )

        walletConnectViewModel.walletConnectRequestLiveData.observe(this, ::handleWalletConnectRequest)

        walletConnectViewModel.invalidTransactionCauseLiveData.observe(this, invalidTransactionCauseObserver)

        collectLatestOnLifecycle(
            mainViewModel.swapNavigationResultFlow,
            swapNavigationDirectionCollector
        )

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
            flow = mainViewModel.firebaseTokenResultFlow,
            collection = firebaseTokenResultCollector
        )

        collectLatestOnLifecycle(
            flow = coreActionsTabBarViewModel.viewState,
            collection = { binding.coreActionsTabBarView.initViewState(it) }
        )

        collectLatestOnLifecycle(
            mainViewModel.viewEvent,
            mainViewEventCollector
        )

        collectLatestOnLifecycle(
            qrScannerViewModel.viewEvent,
            qrScannerViewEventCollector
        )
    }

    private fun navigateToConnectionIssueBottomSheet() {
        nav(HomeNavigationDirections.actionGlobalLedgerConnectionIssueBottomSheet())
    }

    private fun onInvalidWalletConnectTransacitonReceived(error: Resource.Error) {
        val annotatedDescriptionErrorString = AnnotatedString(
            stringResId = R.string.your_walletconnect_request_failed,
            replacementList = listOf("error_message" to error.parse(this).toString())
        )
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

    private fun setupCoreActionsTabBarView() {
        coreActionsTabBarViewModel.changeViewStateForFeatureFlag()
        binding.coreActionsTabBarView.setListener(object : CoreActionsTabBarView.Listener {
            override fun onSendClick() {
                mainViewModel.logEvent(PeraClickEvent.TAP_TAB_SEND)
                nav(HomeNavigationDirections.actionGlobalSendAlgoNavigation(null))
            }

            override fun onReceiveClick() {
                mainViewModel.logEvent(PeraClickEvent.TAP_TAB_RECEIVE)
                nav(HomeNavigationDirections.actionGlobalReceiveAccountSelectionFragment())
            }

            override fun onBuySellClick() {
                mainViewModel.logEvent(PeraClickEvent.TAP_BOTTOM_NAVIGATION_BUY_ALGO)
                navToBuySellActionsBottomSheet()
            }

            override fun onScanQRClick() {
                mainViewModel.logEvent(PeraClickEvent.TAP_BOTTOM_NAVIGATION_QR_SCAN)
                navToQRCodeScannerNavigation()
            }

            override fun onCoreActionsClick(isCoreActionsOpen: Boolean) {
                if (isCoreActionsOpen)
                    mainViewModel.logEvent(PeraClickEvent.TAP_LOWERMENU_PERA)
                binding.bottomNavigationView.menu.forEach { menuItem ->
                    menuItem.isEnabled = isCoreActionsOpen.not()
                }
                handleNavigationButtonsForChosenNetwork()
            }

            override fun onSwapClick() {
                mainViewModel.logEvent(PeraClickEvent.TAP_BOTTOM_NAVIGATION_SWAP)
                mainViewModel.onSwapActionButtonClick()
            }

            override fun onBrowseDappsClick() {
                mainViewModel.logEvent(PeraClickEvent.TAP_BOTTOM_NAVIGATION_BROWSE_DAPPS)
                handleBrowseDappsClick()
            }

            override fun onCardsClick() {
                mainViewModel.logEvent(PeraClickEvent.TAP_BOTTOM_NAVIGATION_CARDS)
                navToCardsFragment()
            }

            override fun onStakingClick() {
                mainViewModel.logEvent(PeraClickEvent.TAP_BOTTOM_NAVIGATION_STAKE)
                navToStakingFragment()
            }
        })
    }

    private fun onNewNodeActivated() {
        hideProgress()
        mainViewModel.onNewNodeActivated(lifecycle)
        coreActionsTabBarViewModel.changeViewStateForFeatureFlag()
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

    private fun navToBuySellActionsBottomSheet() {
        nav(HomeNavigationDirections.actionGlobalBuySellActionsBottomSheet())
    }

    private fun navToQRCodeScannerNavigation() {
        nav(HomeNavigationDirections.actionGlobalAccountsQrScannerFragment())
    }

    private fun navToDiscoverUrlViewerNavigation(webUrl: String) {
        nav(HomeNavigationDirections.actionGlobalDiscoverUrlViewerNavigation(webUrl))
    }

    private fun hideLedgerLoadingDialog() {
        hideProgress()
        ledgerLoadingDialog?.dismissAllowingStateLoss()
        ledgerLoadingDialog = null
    }

    private fun handleBrowseDappsClick() {
        binding.apply {
            coreActionsTabBarView.hideWithAnimation()
            bottomNavigationView.menu.findItem(R.id.discoverHomeNavigation).isChecked = true
            navController.navigateSafe(
                actionGlobalDiscoverHomeNavigation(
                    coreActionsTabBarViewModel.getDiscoverBrowseDappUrl()
                )
            )
        }
    }

    private fun showLedgerLoadingDialog(ledgerName: String?) {
        if (ledgerLoadingDialog == null) {
            ledgerLoadingDialog = LedgerLoadingDialog.createLedgerLoadingDialog(ledgerName, ledgerLoadingDialogListener)
            ledgerLoadingDialog?.showWithStateCheck(supportFragmentManager)
        }
    }

    private fun navToAssetProfileNavigation(accountAddress: String, assetId: Long) {
        nav(
            HomeNavigationDirections.actionGlobalAssetProfileNavigation(
                assetId = assetId,
                accountAddress = accountAddress
            )
        )
    }

    private fun navToAssetAdditionActionNavigation(accountAddress: String, assetId: Long) {
        val assetAction = AssetAction(publicKey = accountAddress, assetId = assetId)
        nav(
            HomeNavigationDirections.actionGlobalAssetAdditionActionNavigation(
                assetAction = assetAction
            )
        )
    }

    private fun navToAssetInboxOneAccountNavigation(accountAddress: String) {
        navController.navigateSafe(
            HomeNavigationDirections.actionGlobalAssetInboxOneAccountNavigation(
                AssetInboxOneAccountNavArgs(
                    accountAddress
                )
            )
        )
    }

    private fun showForegroundNotification(newNotificationData: NotificationMetadata) {
        showForegroundNotification(notificationMetadata = newNotificationData, tag = activityTag)
    }

    private fun showGlobalNotificationError() {
        showGlobalError(errorMessage = getString(R.string.you_cannot_take), tag = activityTag)
    }

    private fun navToKeyRegTransactionFragment(transactionDetail: KeyRegTransactionDetail) {
        nav(HomeNavigationDirections.actionGlobalKeyRegTransactionFragment(transactionDetail))
    }

    private fun showKeyRegDeeplinkError(accountAddress: String) {
        showGlobalError(getString(R.string.you_dont_have_any, accountAddress), tag = activityTag)
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

        const val DEEPLINK_KEY = "deeplinkKey"
        const val DEEPLINK_AND_NAVIGATION_INTENT = "deeplinknavIntent"
        const val WC_TRANSACTION_ID_INTENT_KEY = "wcTransactionId"
        const val WC_ARBITRARY_DATA_ID_INTENT_KEY = "wcArbitraryDataId"
    }
}
