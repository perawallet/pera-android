package com.algorand.android.module.account.core

import com.algorand.android.core.component.detail.domain.model.AccountType.*

object AlgorandSecureBackUpUtils {
    val ELIGIBLE_ACCOUNT_TYPES = listOf(Algo25)
    val EXCLUDED_ACCOUNT_TYPES = listOf(LedgerBle, Rekeyed, RekeyedAuth, NoAuth)
}
