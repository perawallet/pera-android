package com.algorand.wallet.asset.manager

import androidx.lifecycle.Lifecycle

interface AlgoAssetDetailCacheManager {
    fun initialize(lifecycle: Lifecycle)
}
