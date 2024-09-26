package com.algorand.android.module.account.info.domain.usecase

interface InitializeAccountInformation {
    suspend operator fun invoke(addresses: List<String>): Map<String, List<Long>>
}
