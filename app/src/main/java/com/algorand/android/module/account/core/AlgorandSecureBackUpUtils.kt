package com.algorand.android.module.account.core

import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Algo25
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.LedgerBle
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.NoAuth
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Rekeyed
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.RekeyedAuth

object AlgorandSecureBackUpUtils {
    val ELIGIBLE_ACCOUNT_TYPES = listOf(Algo25)
    val EXCLUDED_ACCOUNT_TYPES = listOf(LedgerBle, Rekeyed, RekeyedAuth, NoAuth)
}
