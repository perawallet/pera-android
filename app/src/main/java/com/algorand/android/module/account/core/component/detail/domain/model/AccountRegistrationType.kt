package com.algorand.android.module.account.core.component.detail.domain.model

sealed interface AccountRegistrationType {

    data object Algo25 : AccountRegistrationType

    data object LedgerBle : AccountRegistrationType

    data object NoAuth : AccountRegistrationType
}