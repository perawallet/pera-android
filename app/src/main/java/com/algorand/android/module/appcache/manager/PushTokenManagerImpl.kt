package com.algorand.android.module.appcache.manager

import androidx.lifecycle.LifecycleOwner
import com.algorand.android.account.localaccount.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.android.module.appcache.model.PushTokenStatus
import com.algorand.android.module.appcache.model.PushTokenStatus.NOT_INITIALIZED
import com.algorand.android.banner.domain.usecase.InitializeBanners
import com.algorand.android.deviceid.component.domain.usecase.RegisterDeviceId
import com.algorand.android.foundation.coroutine.CoroutineExtensions.launchIO
import com.algorand.android.pushtoken.MessagingTokenProvider
import com.algorand.android.pushtoken.domain.usecase.GetPushTokenCacheFlow
import com.algorand.android.pushtoken.domain.usecase.SetPushToken
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class PushTokenManagerImpl @Inject constructor(
    private val getPushTokenCacheFlow: GetPushTokenCacheFlow,
    private val getAllLocalAccountAddressesAsFlow: GetAllLocalAccountAddressesAsFlow,
    private val messagingTokenProvider: MessagingTokenProvider,
    private val setPushToken: SetPushToken,
    private val registerDeviceId: RegisterDeviceId,
    private val initializeBanners: InitializeBanners
) : PushTokenManager {

    private var coroutineScope: CoroutineScope? = null
    private var registerDeviceJob: Job? = null
    private var initializeFirebasePushTokenJob: Job? = null

    private val _pushTokenStatusFlow = MutableStateFlow<PushTokenStatus>(NOT_INITIALIZED)
    override val pushTokenStatusFlow: StateFlow<PushTokenStatus>
        get() = _pushTokenStatusFlow.asStateFlow()

    private val localAccountsCollector: suspend (value: List<String>) -> Unit = {
        refreshPushToken()
    }

    private val firebasePushTokenCollector: suspend (value: String?) -> Unit = { token ->
        if (!token.isNullOrBlank()) {
            registerFirebasePushToken(token)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initializeCoroutineScope()
        initializeObservers()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        clearCoroutineScope()
    }

    private fun initializeObservers() {
        coroutineScope?.launchIO {
            getAllLocalAccountAddressesAsFlow().collectLatest(localAccountsCollector)
        }
        coroutineScope?.launchIO {
            getPushTokenCacheFlow().collect(firebasePushTokenCollector)
        }
    }

    override fun refreshPushToken() {
        initializeFirebasePushTokenJob?.cancel()
        initializeFirebasePushTokenJob = coroutineScope?.launchIO {
            messagingTokenProvider.getToken().getDataOrNull()?.let { token ->
                coroutineScope?.launch { setPushToken(token) }
            }
        }
    }

    private fun registerFirebasePushToken(token: String) {
        registerDeviceJob?.cancel()
        registerDeviceJob = coroutineScope?.launchIO {
            registerDeviceId(token).use(
                onSuccess = {
                    _pushTokenStatusFlow.emit(PushTokenStatus.INITIALIZED)
                    initializeBanners()
                },
                onFailed = { _, _ ->
                    _pushTokenStatusFlow.emit(PushTokenStatus.INITIALIZED)
                }
            )
        }
    }

    private fun initializeCoroutineScope() {
        coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    }

    private fun clearCoroutineScope() {
        coroutineScope?.cancel()
        coroutineScope = null
    }
}
