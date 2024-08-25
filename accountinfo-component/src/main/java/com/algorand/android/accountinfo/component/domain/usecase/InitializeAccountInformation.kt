package com.algorand.android.accountinfo.component.domain.usecase

interface InitializeAccountInformation {
    suspend operator fun invoke(addresses: List<String>): Map<String, List<Long>>
}
