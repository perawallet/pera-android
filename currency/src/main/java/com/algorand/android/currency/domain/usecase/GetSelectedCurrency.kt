package com.algorand.android.currency.domain.usecase

import com.algorand.android.currency.domain.model.SelectedCurrency

interface GetSelectedCurrency {
    operator fun invoke(): SelectedCurrency
}
