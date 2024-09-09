package com.algorand.android.parity.domain.usecase

interface InitializeParityCache {
    suspend operator fun invoke()
}
