package com.algorand.android.module.account.core.component.detail.domain.model

import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.custominfo.component.domain.model.CustomInfo

data class AccountDetail(
    val address: String,
    val customInfo: CustomInfo,
    val accountRegistrationType: AccountRegistrationType?,
    val isBackedUp: Boolean,
    val accountType: AccountType?
) {
    fun canSignTransaction(): Boolean {
        return accountType?.canSignTransaction() == true
    }
}
