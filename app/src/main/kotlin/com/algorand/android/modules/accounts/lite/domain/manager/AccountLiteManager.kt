package com.algorand.android.modules.accounts.lite.domain.manager

import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface AccountLiteManager {

    val localAccountLitesFlow: StateFlow<AccountLiteCacheStatus>

    fun initialize(scope: CoroutineScope)
}
