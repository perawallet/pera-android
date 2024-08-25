package com.algorand.android.core.component.polling.accountdetail.domain

import androidx.lifecycle.LifecycleOwner
import com.algorand.android.account.localaccount.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.android.core.component.polling.accountdetail.domain.usecase.IsAccountCacheUpdateRequired
import com.algorand.android.core.component.polling.accountdetail.domain.usecase.UpdateAccountCache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

internal class AccountDetailCacheManagerImpl @Inject constructor(
    private val getAllLocalAccountAddressesAsFlow: GetAllLocalAccountAddressesAsFlow,
    private val isAccountCacheUpdateRequired: IsAccountCacheUpdateRequired,
    private val updateAccountCache: UpdateAccountCache
) : AccountDetailCacheManager {

    private var initializationJob: Job? = null
    private var currentJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onResume(owner: LifecycleOwner) {
        initialize()
    }

    override fun onPause(owner: LifecycleOwner) {
        clearResources()
    }

    override fun initialize() {
        clearResources()
        initializationJob = coroutineScope.launch(Dispatchers.IO) {
            getAllLocalAccountAddressesAsFlow().collectLatest {
                if (it.isNotEmpty()) startJob() else stopCurrentJob()
            }
        }
    }

    private suspend fun startJob() {
        if (currentJob?.isActive == true) stopCurrentJob()
        currentJob = coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                updateIfAccountCacheUpdateRequired()
                delay(NEXT_BLOCK_DELAY_AFTER)
            }
        }
    }

    private fun stopCurrentJob() {
        currentJob?.cancel()
    }

    private suspend fun updateIfAccountCacheUpdateRequired() {
        isAccountCacheUpdateRequired().fold(
            onSuccess = { shouldRefresh ->
                if (shouldRefresh) {
                    updateAccountCache()
                }
            },
            onFailure = {
                updateAccountCache() // is this necessary?
            }
        )
    }

    private fun clearResources() {
        initializationJob?.cancel()
        initializationJob = null
        currentJob?.cancel()
        initializationJob = null
        coroutineScope.coroutineContext.cancelChildren()
    }

    companion object {
        private const val NEXT_BLOCK_DELAY_AFTER = 3500L
    }
}

