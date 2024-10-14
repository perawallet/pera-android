package com.algorand.android.modules.walletconnect.cards.domain.repository

interface WalletConnectCardsRepository {
    fun cachePreselectedAccountAddresses(accountAddresses: List<String>)
    fun getPreselectedAccountAddresses(): List<String>?
    fun clearPreselectedAccountAddressesCache()
}
