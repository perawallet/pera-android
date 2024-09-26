package com.algorand.android.module.currency.domain.usecase

import com.algorand.android.module.currency.domain.model.SelectedCurrency

interface GetSelectedCurrency {
    operator fun invoke(): SelectedCurrency
}
