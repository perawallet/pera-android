package com.algorand.android.core.component.detail.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface AccountType : Parcelable {

    @Parcelize
    data object Algo25 : AccountType

    @Parcelize
    data object LedgerBle : AccountType

    @Parcelize
    data object Rekeyed : AccountType

    @Parcelize
    data object RekeyedAuth : AccountType

    @Parcelize
    data object NoAuth : AccountType

    companion object {
        fun AccountType.canSignTransaction(): Boolean {
            return this is Algo25 || this is LedgerBle || this is RekeyedAuth
        }
    }
}
