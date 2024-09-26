/*
 * Copyright 2022 Pera Wallet, LDA
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

import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.algorand.android.account.localaccount.domain.usecase.IsThereAnyLocalAccount
import com.algorand.android.module.algosdk.transaction.model.Transaction
import com.algorand.android.module.appcache.InitializeAppCache
import com.algorand.android.module.appcache.usecase.GetAppCacheStatusFlow
import com.algorand.android.module.contacts.domain.model.Contact
import com.algorand.android.core.BaseViewModel
import com.algorand.android.module.deeplink.DeepLinkHandler
import com.algorand.android.module.deeplink.model.BaseDeepLink
import com.algorand.android.module.deeplink.usecase.CreateNotificationDeepLink
import com.algorand.android.deviceregistration.domain.usecase.MigrateDeviceIdIfNeed
import com.algorand.android.models.AssetOperationResult
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.NotificationDeepLinkPayload
import com.algorand.android.models.NotificationMetadata
import com.algorand.android.modules.appopencount.domain.usecase.IncreaseAppOpeningCountUseCase
import com.algorand.android.modules.autolockmanager.ui.usecase.AutoLockManagerUseCase
import com.algorand.android.modules.tracking.main.MainActivityEventTracker
import com.algorand.android.modules.tutorialdialog.domain.usecase.TutorialUseCase
import com.algorand.android.node.domain.Node
import com.algorand.android.node.domain.usecase.GetActiveNodeAsFlow
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.AccountSelection
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.Introduction
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.Swap
import com.algorand.android.module.swap.component.common.usecase.GetSwapNavigationDestination
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.ui.addasset.model.AddAssetTransactionPayload
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult
import com.algorand.android.utils.Event
import com.algorand.android.utils.Resource
import com.algorand.android.utils.launchIO
import com.algorand.android.utils.preference.getRegisterSkip
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val migrateDeviceIdIfNeed: MigrateDeviceIdIfNeed,
    private val mainActivityEventTracker: MainActivityEventTracker,
    private val deepLinkHandler: DeepLinkHandler,
    private val increaseAppOpeningCountUseCase: IncreaseAppOpeningCountUseCase,
    private val tutorialUseCase: TutorialUseCase,
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount,
    private val getActiveNodeAsFlow: GetActiveNodeAsFlow,
    private val autoLockManagerUseCase: AutoLockManagerUseCase,
    private val getSwapNavigationDestination: GetSwapNavigationDestination,
    private val initializeAppCache: InitializeAppCache,
    private val getAppCacheStatusFlow: GetAppCacheStatusFlow,
    private val createNotificationDeepLink: CreateNotificationDeepLink,
    private val transactionProcessor: MainActivityTransactionProcessor
) : BaseViewModel() {

    // TODO: Replace this with Flow whenever have time
    val assetOperationResultLiveData = MutableLiveData<Event<Resource<AssetOperationResult>>>()

    private val _swapNavigationResultFlow = MutableStateFlow<Event<NavDirections>?>(null)
    val swapNavigationResultFlow: StateFlow<Event<NavDirections>?>
        get() = _swapNavigationResultFlow

    private val _startDestinationFlow = MutableStateFlow<Event<Int>?>(null)
    val startDestinationFlow: StateFlow<Event<Int>?> get() = _startDestinationFlow.asStateFlow()

    private val _activeNodeFlow = MutableStateFlow<Node?>(null)
    val activeNodeFlow: StateFlow<Node?> get() = _activeNodeFlow

    val signTransactionUiResultFlow: StateFlow<Event<SignTransactionUiResult>?>
        get() = transactionProcessor.signTransactionUiResultFlow

    private val _notificationDeepLinkFlow = MutableStateFlow<Event<NotificationDeepLinkPayload>?>(null)
    val notificationDeepLinkFlow: StateFlow<Event<NotificationDeepLinkPayload>?>
        get() = _notificationDeepLinkFlow.asStateFlow()

    val appCacheStatusFlow = getAppCacheStatusFlow()

    init {
        initActiveNodeFlow()
        migrateDeviceIdIfNecessary()
        initializeTutorial()
    }

    fun shouldAppLocked(): Boolean {
        return autoLockManagerUseCase.shouldAppLocked()
    }

    fun cacheTransaction(payload: AddAssetTransactionPayload) {
        transactionProcessor.cacheTransaction(payload)
    }

    fun cacheTransaction(address: String, assetId: Long, waitForConfirmation: Boolean) {
        transactionProcessor.cacheTransaction(address, assetId, waitForConfirmation)
    }

    fun processTransaction(lifecycle: Lifecycle, transaction: Transaction) {
        transactionProcessor.processTransaction(lifecycle, transaction, viewModelScope)
    }

    fun stopSignTransactionResources() {
        transactionProcessor.stopSignTransactionResources()
    }

    fun initializeAppCoreCache(lifecycle: Lifecycle) {
        viewModelScope.launch {
            initializeAppCache(lifecycle)
        }
    }

    fun initializeStartDestinationFlow() {
        viewModelScope.launchIO {
            val startDestinationFragmentId = if (isThereAnyLocalAccount() || sharedPref.getRegisterSkip()) {
                R.id.homeNavigation
            } else {
                R.id.loginNavigation
            }
            _startDestinationFlow.emit(Event(startDestinationFragmentId))
        }
    }

    private fun migrateDeviceIdIfNecessary() {
        viewModelScope.launch(Dispatchers.IO) {
            migrateDeviceIdIfNeed()
        }
    }

    fun handleNewNotification(newNotificationData: NotificationMetadata) {
        viewModelScope.launchIO {
            val notificationDeepLink = createNotificationDeepLink(newNotificationData.url.orEmpty())
            val payload = NotificationDeepLinkPayload(
                metadata = newNotificationData,
                deepLink = notificationDeepLink
            )
            _notificationDeepLinkFlow.emit(Event(payload))
        }
    }

    fun getAssetTransaction(
        deepLink: com.algorand.android.module.deeplink.model.BaseDeepLink.AssetTransferDeepLink,
        receiverAddress: String,
        receiverName: String
    ): AssetTransaction {
        return AssetTransaction(
            assetId = deepLink.assetId,
            note = deepLink.note, // normal note
            xnote = deepLink.xnote, // locked note
            amount = deepLink.amount,
            receiverUser = Contact(address = receiverAddress, name = receiverName, imageUri = null)
        )
    }

    fun handleDeepLink(uri: String) {
        viewModelScope.launchIO {
            deepLinkHandler.handleDeepLink(uri)
        }
    }

    fun setDeepLinkHandlerListener(listener: DeepLinkHandler.Listener) {
        deepLinkHandler.setListener(listener)
    }

    fun logBottomNavAccountsTapEvent() {
        viewModelScope.launch {
            mainActivityEventTracker.logAccountsTapEvent()
        }
    }

    fun logBottomNavigationBuyAlgoEvent() {
        viewModelScope.launch {
            mainActivityEventTracker.logBottomNavigationAlgoBuyTapEvent()
        }
    }

    fun increseAppOpeningCount() {
        viewModelScope.launch {
            increaseAppOpeningCountUseCase.increaseAppOpeningCount()
        }
    }

    fun onSwapActionButtonClick() {
        viewModelScope.launch {
            mainActivityEventTracker.logQuickActionSwapButtonClickEvent()
            val navDestination = when (val destination = getSwapNavigationDestination(null)) {
                AccountSelection -> HomeNavigationDirections.actionGlobalSwapAccountSelectionNavigation()
                Introduction -> HomeNavigationDirections.actionGlobalSwapIntroductionNavigation()
                is Swap -> HomeNavigationDirections.actionGlobalSwapNavigation(destination.address)
            }
            _swapNavigationResultFlow.emit(Event(navDestination))
        }
    }

    fun sendSignedTransaction(signedTransaction: SignedTransaction) {
        viewModelScope.launch {
            transactionProcessor.sendSignedTransaction(signedTransaction)?.use(
                onSuccess = {
                    assetOperationResultLiveData.postValue(Event(Resource.Success(it)))
                },
                onFailed = { exception, _ ->
                    assetOperationResultLiveData.postValue(Event(Resource.Error.Api(exception)))
                }
            )
        }
    }

    private fun initializeTutorial() {
        viewModelScope.launch {
            tutorialUseCase.initializeTutorial()
        }
    }

    private fun initActiveNodeFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            getActiveNodeAsFlow().collectLatest {
                _activeNodeFlow.value = it
            }
        }
    }
}
