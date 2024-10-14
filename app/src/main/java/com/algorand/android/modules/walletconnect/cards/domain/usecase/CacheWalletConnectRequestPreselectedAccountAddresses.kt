package com.algorand.android.modules.walletconnect.cards.domain.usecase

fun interface CacheWalletConnectRequestPreselectedAccountAddresses {
    operator fun invoke(wcRequestUrl: String)
}
