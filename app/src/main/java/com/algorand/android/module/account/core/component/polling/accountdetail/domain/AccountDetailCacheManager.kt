package com.algorand.android.module.account.core.component.polling.accountdetail.domain

import androidx.lifecycle.DefaultLifecycleObserver

interface AccountDetailCacheManager : DefaultLifecycleObserver {
    fun initialize()
}
