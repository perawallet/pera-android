package com.algorand.android.modules.assets.manager

import androidx.lifecycle.Lifecycle
import com.algorand.android.modules.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.utils.CacheResult
import com.algorand.wallet.asset.domain.usecase.CacheAlgoAssetDetail
import com.algorand.wallet.asset.manager.AlgoAssetDetailCacheManager
import com.algorand.wallet.cache.LifecycleAwareCacheManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

class AlgoAssetDetailCacheManagerImpl @Inject constructor(
    private val cacheManager: LifecycleAwareCacheManager,
    private val cacheAlgoAssetDetail: CacheAlgoAssetDetail,
    private val parityUseCase: ParityUseCase
) : AlgoAssetDetailCacheManager {

    private val cacheManagerListener = object : LifecycleAwareCacheManager.CacheManagerListener {
        override suspend fun onInitializeManager(coroutineScope: CoroutineScope) {
            initialize()
        }

        override suspend fun onStartJob(coroutineScope: CoroutineScope) {
            runManagerJob()
        }
    }

    private val currencyDetailCollector: suspend (CacheResult<SelectedCurrencyDetail>?) -> Unit = {
        if (it is CacheResult.Success) {
            cacheAlgoAssetDetail(usdValue = it.data.algoUsdExchangePrice)
        }
    }

    override fun initialize(lifecycle: Lifecycle) {
        cacheManager.setListener(cacheManagerListener)
        lifecycle.addObserver(cacheManager)
    }

    private suspend fun initialize() {
        cacheAlgoAssetDetail(usdValue = null)
        cacheManager.startJob()
    }

    private suspend fun runManagerJob() {
        parityUseCase.getSelectedCurrencyDetailCacheFlow().collect(currencyDetailCollector)
    }
}
