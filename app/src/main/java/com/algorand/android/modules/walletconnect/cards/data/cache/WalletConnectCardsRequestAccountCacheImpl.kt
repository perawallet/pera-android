package com.algorand.android.modules.walletconnect.cards.data.cache

import javax.inject.Inject

internal class WalletConnectCardsRequestAccountCacheImpl @Inject constructor() : WalletConnectCardsRequestAccountCache {

    private var cache: List<String>? = null

    override fun cache(accountAddresses: List<String>) {
        cache = accountAddresses.toList()
    }

    override fun get(): List<String>? = cache

    override fun clear() {
        cache = null
    }
}
