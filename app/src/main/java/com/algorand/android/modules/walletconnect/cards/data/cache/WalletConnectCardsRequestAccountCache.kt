package com.algorand.android.modules.walletconnect.cards.data.cache

interface WalletConnectCardsRequestAccountCache {
    fun cache(accountAddresses: List<String>)
    fun get(): List<String>?
    fun clear()
}
