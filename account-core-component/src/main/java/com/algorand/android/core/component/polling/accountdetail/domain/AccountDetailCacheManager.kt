package com.algorand.android.core.component.polling.accountdetail.domain

import androidx.lifecycle.DefaultLifecycleObserver

interface AccountDetailCacheManager : DefaultLifecycleObserver {
    fun initialize()
}
