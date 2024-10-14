package com.algorand.android.modules.walletconnect.cards.domain.usecase

fun interface GetWalletConnectRequestPreselectedAccountAddresses {
    operator fun invoke(): List<String>?
}
