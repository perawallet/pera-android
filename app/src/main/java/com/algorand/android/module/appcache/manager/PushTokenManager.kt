package com.algorand.android.module.appcache.manager

import androidx.lifecycle.DefaultLifecycleObserver
import com.algorand.android.module.appcache.model.PushTokenStatus
import kotlinx.coroutines.flow.StateFlow

interface PushTokenManager : DefaultLifecycleObserver {
    val pushTokenStatusFlow: StateFlow<PushTokenStatus>

    fun refreshPushToken()
}
