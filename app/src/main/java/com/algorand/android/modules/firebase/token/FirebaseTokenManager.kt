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

package com.algorand.android.modules.firebase.token

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.algorand.android.account.localaccount.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.android.appcache.usecase.ClearAppSessionCache
import com.algorand.android.banner.domain.usecase.InitializeBanners
import com.algorand.android.deviceid.component.domain.usecase.GetNodeDeviceId
import com.algorand.android.deviceregistration.domain.usecase.DeviceRegistrationUseCase
import com.algorand.android.deviceregistration.domain.usecase.UpdatePushTokenUseCase
import com.algorand.android.modules.firebase.token.mapper.FirebaseTokenResultMapper
import com.algorand.android.modules.firebase.token.model.FirebaseTokenResult
import com.algorand.android.node.domain.Node
import com.algorand.android.pushtoken.domain.usecase.GetPushTokenCacheFlow
import com.algorand.android.pushtoken.domain.usecase.SetPushToken
import com.algorand.android.utils.launchIO
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: separate this class into smaller classes
@Singleton
class FirebaseTokenManager @Inject constructor(
    private val getPushTokenCacheFlow: GetPushTokenCacheFlow,
    private val setPushToken: SetPushToken,
    private val deviceRegistrationUseCase: DeviceRegistrationUseCase,
    private val updatePushTokenUseCase: UpdatePushTokenUseCase,
    private val getAllLocalAccountAddressesAsFlow: GetAllLocalAccountAddressesAsFlow,
    private val clearAppSessionCache: ClearAppSessionCache,
    private val firebaseTokenResultMapper: FirebaseTokenResultMapper,
    private val getNodeDeviceId: GetNodeDeviceId,
    private val initializeBanners: InitializeBanners
) : DefaultLifecycleObserver {

    private val _firebaseTokenResultEventFlow = MutableStateFlow<FirebaseTokenResult>(FirebaseTokenResult.TokenLoading)
    val firebaseTokenResultFlow: StateFlow<FirebaseTokenResult> get() = _firebaseTokenResultEventFlow

    private val localAccountsCollector: suspend (value: List<String>) -> Unit = {
        refreshFirebasePushToken(null)
    }

    private val firebasePushTokenCollector: suspend (value: String?) -> Unit = { token ->
        if (!token.isNullOrBlank()) {
            registerFirebasePushToken(token)
        }
    }

    private var coroutineScope: CoroutineScope? = null
    private var registerDeviceJob: Job? = null
    private var refreshFirebasePushTokenJob: Job? = null

    fun refreshFirebasePushToken(previousNode: Node?) {
        refreshFirebasePushTokenJob?.cancel()
        refreshFirebasePushTokenJob = coroutineScope?.launchIO {
            try {
                _firebaseTokenResultEventFlow.emit(firebaseTokenResultMapper.mapToTokenLoading())
                if (previousNode != null) {
                    deletePreviousNodePushToken(previousNode)
                }
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    coroutineScope?.launch { setPushToken(token) }
                }
            } catch (exception: Exception) {
                // TODO: Re-active last activated node in case of failure
                _firebaseTokenResultEventFlow.emit(firebaseTokenResultMapper.mapToTokenLoaded())
            }
        }
    }

    private fun initialize() {
        initObservers()
        refreshFirebasePushToken(null)
    }

    private fun initObservers() {
        coroutineScope?.launchIO {
            getAllLocalAccountAddressesAsFlow().collectLatest(localAccountsCollector)
        }
        coroutineScope?.launchIO {
            getPushTokenCacheFlow().collect(firebasePushTokenCollector)
        }
    }

    private fun registerFirebasePushToken(token: String) {
        registerDeviceJob?.cancel()
        registerDeviceJob = coroutineScope?.launchIO {
            deviceRegistrationUseCase.registerDevice(token)
                .onSuccess {
                    onPushTokenUpdated()
                    initializeBanners()
                }
                .onFailure {
                    _firebaseTokenResultEventFlow.emit(firebaseTokenResultMapper.mapToTokenLoaded())
                    onPushTokenFailed()
                }
        }
    }

    private suspend fun deletePreviousNodePushToken(previousNode: Node) {
        val deviceId = getNodeDeviceId(previousNode) ?: return
        updatePushTokenUseCase(deviceId, null).onSuccess {
            clearAppSessionCache()
        }
    }

    private suspend fun onPushTokenUpdated() {
        _firebaseTokenResultEventFlow.emit(firebaseTokenResultMapper.mapToTokenLoaded())
    }

    private suspend fun onPushTokenFailed() {
        _firebaseTokenResultEventFlow.emit(firebaseTokenResultMapper.mapToTokenFailed())
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        initialize()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        coroutineScope?.cancel()
        coroutineScope = null
    }
}
