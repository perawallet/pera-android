package com.algorand.android.modules.walletconnect.cards.domain.usecase

import com.algorand.android.modules.walletconnect.cards.domain.repository.WalletConnectCardsRepository
import javax.inject.Inject

internal class CacheWalletConnectRequestPreselectedAccountAddressesUseCase @Inject constructor(
    private val walletConnectCardsRepository: WalletConnectCardsRepository
) : CacheWalletConnectRequestPreselectedAccountAddresses {

    override fun invoke(wcRequestUrl: String) {
        val address = getSelectedAccountAddress(wcRequestUrl)
        if (address != null) {
            walletConnectCardsRepository.cachePreselectedAccountAddresses(listOf(address))
        }
    }

    private fun getSelectedAccountAddress(wcRequestUrl: String): String? {
        return wcRequestUrl.split("?")
            .lastOrNull()
            ?.split("&")
            ?.firstOrNull { it.contains(SELECTED_ACCOUNT_QUERY_KEY) }
            ?.split("=")
            ?.lastOrNull()
    }

    private companion object {
        const val SELECTED_ACCOUNT_QUERY_KEY = "selectedAccount"
    }
}
