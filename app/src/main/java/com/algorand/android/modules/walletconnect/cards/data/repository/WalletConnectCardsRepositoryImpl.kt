package com.algorand.android.modules.walletconnect.cards.data.repository

import com.algorand.android.modules.walletconnect.cards.data.cache.WalletConnectCardsRequestAccountCache
import com.algorand.android.modules.walletconnect.cards.domain.repository.WalletConnectCardsRepository
import javax.inject.Inject

internal class WalletConnectCardsRepositoryImpl @Inject constructor(
    private val cardsRequestAccountCache: WalletConnectCardsRequestAccountCache
) : WalletConnectCardsRepository {

    override fun cachePreselectedAccountAddresses(accountAddresses: List<String>) {
        cardsRequestAccountCache.cache(accountAddresses)
    }

    override fun getPreselectedAccountAddresses(): List<String>? {
        return cardsRequestAccountCache.get()
    }

    override fun clearPreselectedAccountAddressesCache() {
        cardsRequestAccountCache.clear()
    }
}
