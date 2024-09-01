package com.algorand.android.appcache.manager

import androidx.lifecycle.DefaultLifecycleObserver
import com.algorand.android.appcache.model.PushTokenStatus
import kotlinx.coroutines.flow.StateFlow

interface PushTokenManager : DefaultLifecycleObserver {
    val pushTokenStatusFlow: StateFlow<PushTokenStatus>

    fun refreshPushToken()
}
