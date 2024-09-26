package com.algorand.android.module.account.local.domain.usecase

import kotlinx.coroutines.flow.Flow

interface GetAllLocalAccountAddressesAsFlow {
    operator fun invoke(): Flow<List<String>>
}
