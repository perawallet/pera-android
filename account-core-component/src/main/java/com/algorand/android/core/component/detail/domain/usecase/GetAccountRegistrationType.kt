package com.algorand.android.core.component.detail.domain.usecase

import com.algorand.android.core.component.detail.domain.model.AccountRegistrationType

internal interface GetAccountRegistrationType {
    suspend operator fun invoke(address: String): AccountRegistrationType?
}
