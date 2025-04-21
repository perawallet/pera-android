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

import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.algorand.android.MainActivity.Companion.DEEPLINK_KEY
import com.algorand.android.MainActivity.Companion.WC_ARBITRARY_DATA_ID_INTENT_KEY
import com.algorand.android.MainActivity.Companion.WC_TRANSACTION_ID_INTENT_KEY
import com.algorand.android.core.BaseViewModel
import com.algorand.android.database.NodeDao
import com.algorand.android.deviceregistration.domain.usecase.DeviceIdMigrationUseCase
import com.algorand.android.encryption.domain.usecase.AndroidEncryptionManager
import com.algorand.android.models.Node
import com.algorand.android.modules.appopencount.domain.usecase.IncreaseAppOpeningCountUseCase
import com.algorand.android.modules.autolockmanager.ui.AutoLockManager
import com.algorand.android.modules.autolockmanager.ui.usecase.AutoLockManagerUseCase
import com.algorand.android.modules.deeplink.ui.DeeplinkHandler
import com.algorand.android.modules.firebase.token.FirebaseTokenManager
import com.algorand.android.modules.firebase.token.model.FirebaseTokenResult
import com.algorand.android.modules.pendingintentkeeper.ui.PendingIntentKeeper
import com.algorand.android.modules.swap.utils.SwapNavigationDestinationHelper
import com.algorand.android.modules.tutorialdialog.domain.usecase.TutorialUseCase
import com.algorand.android.network.AlgodInterceptor
import com.algorand.android.network.IndexerInterceptor
import com.algorand.android.network.MobileHeaderInterceptor
import com.algorand.android.notification.domain.model.NotificationMetadata
import com.algorand.android.repository.NodeRepository
import com.algorand.android.ui.lockpreference.AutoLockSuggestionManager
import com.algorand.android.utils.Event
import com.algorand.android.utils.findAllNodes
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import com.algorand.wallet.analytics.domain.service.PeraReferrerManager
import com.algorand.wallet.cache.domain.usecase.GetAppCacheStatusFlow
import com.algorand.wallet.cache.domain.usecase.InitializeAppCache
import com.algorand.wallet.deeplink.model.DeepLink
import com.algorand.wallet.deeplink.model.NotificationGroupType
import com.algorand.wallet.deeplink.model.NotificationGroupType.ASSET_INBOX
import com.algorand.wallet.deeplink.model.NotificationGroupType.OPT_IN
import com.algorand.wallet.deeplink.model.NotificationGroupType.TRANSACTIONS
import com.algorand.wallet.deeplink.parser.CreateDeepLink
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val nodeDao: NodeDao,
    private val indexerInterceptor: IndexerInterceptor,
    private val mobileHeaderInterceptor: MobileHeaderInterceptor,
    private val algodInterceptor: AlgodInterceptor,
    private val deviceIdMigrationUseCase: DeviceIdMigrationUseCase,
    private val deepLinkHandler: DeeplinkHandler,
    private val increaseAppOpeningCountUseCase: IncreaseAppOpeningCountUseCase,
    private val tutorialUseCase: TutorialUseCase,
    private val swapNavigationDestinationHelper: SwapNavigationDestinationHelper,
    private val nodeRepository: NodeRepository,
    private val peraReferrerManager: PeraReferrerManager,
    private val autoLockManagerUseCase: AutoLockManagerUseCase,
    private val initializeAppCache: InitializeAppCache,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val createDeepLink: CreateDeepLink,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getAccountType: GetAccountType,
    private var pendingIntentKeeper: PendingIntentKeeper,
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount,
    private val autoLockManager: AutoLockManager,
    private val autoLockSuggestionManager: AutoLockSuggestionManager,
    private val androidEncryptionManager: AndroidEncryptionManager,
    firebaseTokenManager: FirebaseTokenManager,
    getAppCacheStatusFlow: GetAppCacheStatusFlow
) : BaseViewModel(), EventViewModel<MainViewModel.ViewEvent> by eventDelegate {

    val appCacheStatusFlow = getAppCacheStatusFlow()
    val activeNodeFlow: StateFlow<Node?> get() = _activeNodeFlow
    val swapNavigationResultFlow: StateFlow<Event<NavDirections>?>
        get() = _swapNavigationResultFlow

    var isAssetSetupCompleted: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (oldValue != newValue && newValue && isAppUnlocked()) {
            handlePendingIntent(true)
        }
    }

    val firebaseTokenResultFlow: SharedFlow<FirebaseTokenResult> = firebaseTokenManager.firebaseTokenResultFlow
        .shareIn(viewModelScope, started = SharingStarted.Lazily)

    private val _swapNavigationResultFlow = MutableStateFlow<Event<NavDirections>?>(null)
    private val _activeNodeFlow = MutableStateFlow<Node?>(null)

    private var refreshBalanceJob: Job? = null

    init {
        initActiveNodeFlow()
        initializeNodeInterceptor()
        initializeTutorial()
    }

    fun initializeApp(lifecycle: Lifecycle) {
        viewModelScope.launch {
            androidEncryptionManager.initializeEncryptionManager()
            initializeAppCache(lifecycle)
        }
    }

    fun onNewNodeActivated(lifecycle: Lifecycle) {
        refreshBalanceJob?.cancel()
        viewModelScope.launch { initializeAppCache(lifecycle) }
    }

    fun handleDeepLink(uri: String) {
        viewModelScope.launchIO {
            deepLinkHandler.handleDeepLink(uri)
        }
    }

    fun setDeepLinkHandlerListener(listener: DeeplinkHandler.Listener) {
        deepLinkHandler.setListener(listener)
    }

    fun increaseAppOpeningCount() {
        viewModelScope.launch {
            increaseAppOpeningCountUseCase.increaseAppOpeningCount()
        }
    }

    fun onSwapActionButtonClick() {
        viewModelScope.launch {
            var swapNavDirection: NavDirections? = null
            swapNavigationDestinationHelper.getSwapNavigationDestination(
                onNavToIntroduction = {
                    swapNavDirection = HomeNavigationDirections.actionGlobalSwapIntroductionNavigation()
                },
                onNavToAccountSelection = {
                    swapNavDirection = HomeNavigationDirections.actionGlobalSwapAccountSelectionNavigation()
                },
                onNavToSwap = { accountAddress ->
                    swapNavDirection = HomeNavigationDirections.actionGlobalSwapNavigation(accountAddress)
                }
            )
            swapNavDirection?.let { direction ->
                _swapNavigationResultFlow.emit(Event(direction))
            }
        }
    }

    fun fetchInstallReferrer() {
        viewModelScope.launch(Dispatchers.IO) {
            peraReferrerManager.fetchInstallReferrer()
        }
    }

    fun handleNewNotification(newNotificationData: NotificationMetadata) {
        when (val baseDeepLink = createDeepLink(newNotificationData.url.orEmpty())) {
            is DeepLink.Notification -> handleNotificationWithDeepLink(newNotificationData, baseDeepLink)
            else -> ViewEvent.ShowForegroundNotification(notificationMetadata = newNotificationData)
        }
    }

    fun handleNotificationDeepLink(
        accountAddress: String,
        assetId: Long,
        notificationGroupType: NotificationGroupType
    ) {
        viewModelScope.launch {
            if (!isThereAnyAccountWithAddress(accountAddress)) {
                eventDelegate.sendEvent(ViewEvent.ShowGlobalNotificationError)
                return@launch
            }

            val viewEvent = when (notificationGroupType) {
                TRANSACTIONS -> ViewEvent.HandleAssetTransactionDeepLink(accountAddress, assetId)
                OPT_IN -> ViewEvent.HandleAssetOptInRequestDeepLink(accountAddress, assetId)
                ASSET_INBOX -> getAssetInboxDeepLinkEvent(accountAddress)
            }

            eventDelegate.sendEvent(viewEvent)
        }
    }

    fun handleAssetInboxDeepLink(accountAddress: String) {
        viewModelScope.launch {
            eventDelegate.sendEvent(getAssetInboxDeepLinkEvent(accountAddress))
        }
    }

    fun isAppUnlocked(): Boolean {
        return autoLockManager.isAppUnlocked
    }

    fun setAutoLockManagerListener(autoLockManagerListener: AutoLockManager.AutoLockManagerListener) {
        autoLockManager.setListener(autoLockManagerListener)
    }

    fun handlePendingIntent(isAppStart: Boolean = false) {
        viewModelScope.launchIO {
            val isPendingIntentHandled = pendingIntentKeeper.pendingIntent?.let { intent ->
                if (isAssetSetupCompleted && (isAppUnlocked() || !shouldAppLocked())) {
                    val handled = intent.dataString?.let { data ->
                        handleDeepLink(data)
                        true
                    } ?: handlePendingIntentWithExtras(intent)
                    pendingIntentKeeper.clearPendingIntent()
                    handled
                } else {
                    false
                }
            } ?: false

            if (isAppStart && !isPendingIntentHandled) {
                startInAppReview()
            }
        }
    }

    fun startAutoLockSuggestion() {
        viewModelScope.launch {
            if (autoLockSuggestionManager.shouldSuggestAutoLock()) {
                eventDelegate.sendEvent(ViewEvent.ShowLockSuggestion)
            }
        }
    }

    fun setPendingIntent(intent: Intent?) {
        pendingIntentKeeper.setPendingIntent(intent)
    }

    private suspend fun shouldAppLocked(): Boolean {
        return autoLockManagerUseCase.shouldAppLocked()
    }

    private suspend fun handlePendingIntentWithExtras(pendingIntent: Intent): Boolean {
        val transactionId = pendingIntent.getLongExtra(WC_TRANSACTION_ID_INTENT_KEY, -1L)
        val arbitraryDataId = pendingIntent.getLongExtra(WC_ARBITRARY_DATA_ID_INTENT_KEY, -1L)

        return when {
            transactionId != -1L -> {
                eventDelegate.sendEvent(ViewEvent.NavToWalletConnectTransactionRequestNavigation(transactionId))
                true
            }

            arbitraryDataId != -1L -> {
                eventDelegate.sendEvent(ViewEvent.NavToWalletConnectArbitraryDataRequestNavigation(arbitraryDataId))
                true
            }

            else -> pendingIntent.getStringExtra(DEEPLINK_KEY)?.let {
                handleDeepLink(it)
                true
            } ?: false
        }
    }

    private suspend fun migrateDeviceIdIfNeed() {
        deviceIdMigrationUseCase.migrateDeviceIdIfNeed()
    }

    private fun startInAppReview() {
        viewModelScope.launchIO {
            if (isThereAnyLocalAccount()) {
                eventDelegate.sendEvent(ViewEvent.StartInAppReview)
            }
        }
    }

    private fun initializeNodeInterceptor() {
        viewModelScope.launch(Dispatchers.IO) {
            if (indexerInterceptor.currentActiveNode == null) {
                val lastActivatedNode = findAllNodes(sharedPref, nodeDao).find { it.isActive }
                lastActivatedNode?.activate(indexerInterceptor, mobileHeaderInterceptor, algodInterceptor)
            }
            migrateDeviceIdIfNeed()
        }
    }

    private fun initializeTutorial() {
        viewModelScope.launch {
            tutorialUseCase.initializeTutorial()
        }
    }

    private fun initActiveNodeFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            nodeRepository.getActiveNodeAsFlow().collectLatest {
                _activeNodeFlow.value = it
            }
        }
    }

    private fun handleNotificationWithDeepLink(
        newNotificationData: NotificationMetadata,
        deeplink: DeepLink.Notification
    ) {
        viewModelScope.launch {
            if (!isThereAnyAccountWithAddress(deeplink.address)) {
                eventDelegate.sendEvent(ViewEvent.ShowGlobalNotificationError)
                return@launch
            }

            val viewEvent = when (deeplink.notificationGroupType) {
                OPT_IN -> ViewEvent.HandleAssetOptInRequestDeepLink(deeplink.address, deeplink.assetId)
                ASSET_INBOX -> getAssetInboxDeepLinkEvent(deeplink.address)
                else -> ViewEvent.ShowForegroundNotification(notificationMetadata = newNotificationData)
            }

            eventDelegate.sendEvent(viewEvent)
        }
    }

    private suspend fun getAssetInboxDeepLinkEvent(accountAddress: String): ViewEvent {
        val canSignTransaction = getAccountType(accountAddress)?.canSignTransaction() == true
        return if (canSignTransaction) {
            ViewEvent.NavToAssetInboxOneAccountNavigation(accountAddress)
        } else {
            ViewEvent.NavToAccountDetailFragment(accountAddress)
        }
    }

    sealed interface ViewEvent {
        data class HandleAssetTransactionDeepLink(val address: String, val assetId: Long) : ViewEvent
        data class HandleAssetOptInRequestDeepLink(val address: String, val assetId: Long) : ViewEvent
        data class NavToAssetInboxOneAccountNavigation(val address: String) : ViewEvent
        data class NavToAccountDetailFragment(val address: String) : ViewEvent
        data class ShowForegroundNotification(val notificationMetadata: NotificationMetadata) : ViewEvent
        data class NavToWalletConnectTransactionRequestNavigation(val wcRequestId: Long) : ViewEvent
        data class NavToWalletConnectArbitraryDataRequestNavigation(val wcRequestId: Long) : ViewEvent
        data object ShowGlobalNotificationError : ViewEvent
        data object StartInAppReview : ViewEvent
        data object ShowLockSuggestion : ViewEvent
    }
}
