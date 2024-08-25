package com.algorand.android.asb.component.domain.usecase

import kotlinx.coroutines.flow.Flow

fun interface GetBackedUpAccountAddressesFlow {
    operator fun invoke(): Flow<Set<String>>
}
