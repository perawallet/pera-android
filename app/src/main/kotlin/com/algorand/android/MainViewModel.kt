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
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.algorand.android.core.BaseViewModel
import com.algorand.android.database.NodeDao
import com.algorand.android.deviceregistration.domain.usecase.DeviceIdMigrationUseCase
import com.algorand.android.models.Node
import com.algorand.android.modules.accountstatehelper.domain.usecase.AccountStateHelperUseCase
import com.algorand.android.modules.appopencount.domain.usecase.IncreaseAppOpeningCountUseCase
import com.algorand.android.modules.autolockmanager.ui.usecase.AutoLockManagerUseCase
import com.algorand.android.modules.deeplink.ui.DeeplinkHandler
import com.algorand.android.modules.swap.utils.SwapNavigationDestinationHelper
import com.algorand.android.modules.tracking.main.MainActivityEventTracker
import com.algorand.android.modules.tutorialdialog.domain.usecase.TutorialUseCase
import com.algorand.android.network.AlgodInterceptor
import com.algorand.android.network.IndexerInterceptor
import com.algorand.android.network.MobileHeaderInterceptor
import com.algorand.android.repository.NodeRepository
import com.algorand.android.usecase.AccountCacheStatusUseCase
import com.algorand.android.utils.AccountCacheManager
import com.algorand.android.utils.Event
import com.algorand.android.utils.coremanager.AccountDetailCacheManager
import com.algorand.android.utils.findAllNodes
import com.algorand.wallet.cache.domain.usecase.InitializeAppCache
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val nodeDao: NodeDao,
    private val indexerInterceptor: IndexerInterceptor,
    private val mobileHeaderInterceptor: MobileHeaderInterceptor,
    private val algodInterceptor: AlgodInterceptor,
    private val accountCacheManager: AccountCacheManager,
    private val deviceIdMigrationUseCase: DeviceIdMigrationUseCase,
    private val mainActivityEventTracker: MainActivityEventTracker,
    private val deepLinkHandler: DeeplinkHandler,
    private val increaseAppOpeningCountUseCase: IncreaseAppOpeningCountUseCase,
    private val tutorialUseCase: TutorialUseCase,
    private val swapNavigationDestinationHelper: SwapNavigationDestinationHelper,
    private val accountDetailCacheManager: AccountDetailCacheManager,
    private val nodeRepository: NodeRepository,
    accountCacheStatusUseCase: AccountCacheStatusUseCase,
    private val autoLockManagerUseCase: AutoLockManagerUseCase,
    private val accountStateHelperUseCase: AccountStateHelperUseCase,
    private val initializeAppCache: InitializeAppCache
) : BaseViewModel() {

    // TODO I'll change after checking usage of flow in activity.
    val accountBalanceSyncStatus = accountCacheStatusUseCase.getAccountCacheStatusFlow().asLiveData()

    private val _swapNavigationResultFlow = MutableStateFlow<Event<NavDirections>?>(null)
    val swapNavigationResultFlow: StateFlow<Event<NavDirections>?>
        get() = _swapNavigationResultFlow

    private val _activeNodeFlow = MutableStateFlow<Node?>(null)
    val activeNodeFlow: StateFlow<Node?> get() = _activeNodeFlow
    var refreshBalanceJob: Job? = null

    init {
        initActiveNodeFlow()
        initializeAccountCacheManager()
        initializeNodeInterceptor()
        initializeTutorial()
    }

    fun initAppCache(lifecycle: Lifecycle) {
        viewModelScope.launch {
            initializeAppCache(lifecycle)
        }
    }

    fun shouldAppLocked(): Boolean {
        return autoLockManagerUseCase.shouldAppLocked()
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

    private suspend fun migrateDeviceIdIfNeed() {
        deviceIdMigrationUseCase.migrateDeviceIdIfNeed()
    }

    private fun initializeAccountCacheManager() {
        viewModelScope.launch(Dispatchers.IO) {
            accountCacheManager.initializeAccountCacheMap()
        }
    }

    fun onNewNodeActivated() {
        resetBlockPolling()
    }

    /**
     * If we are going to re-enable block polling manager again, we should enable this job here.
     */
    private fun resetBlockPolling() {
        refreshBalanceJob?.cancel()
        accountDetailCacheManager.startJob()
        // blockPollingManager.startJob()
    }

    fun handleDeepLink(uri: String) {
        deepLinkHandler.handleDeepLink(uri)
    }

    fun setDeepLinkHandlerListener(listener: DeeplinkHandler.Listener) {
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

    fun hasAccountAuthority(accountAddress: String): Boolean {
        return accountStateHelperUseCase.hasAccountAuthority(accountAddress)
    }
}
