package com.algorand.android.module.asb.domain.usecase

import kotlinx.coroutines.flow.Flow

fun interface GetBackedUpAccountAddressesFlow {
    operator fun invoke(): Flow<Set<String>>
}
