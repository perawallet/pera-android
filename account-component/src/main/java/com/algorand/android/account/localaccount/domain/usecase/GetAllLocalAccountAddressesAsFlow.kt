package com.algorand.android.account.localaccount.domain.usecase

import kotlinx.coroutines.flow.Flow

interface GetAllLocalAccountAddressesAsFlow {
    operator fun invoke(): Flow<List<String>>
}
