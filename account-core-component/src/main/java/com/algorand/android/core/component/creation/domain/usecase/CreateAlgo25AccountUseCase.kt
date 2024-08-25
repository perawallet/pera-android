package com.algorand.android.core.component.creation.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.*
import com.algorand.android.core.component.caching.domain.usecase.CacheAccountDetail
import javax.inject.Inject

internal class CreateAlgo25AccountUseCase @Inject constructor(
    private val addAlgo25Account: AddAlgo25Account,
    private val cacheAccountDetail: CacheAccountDetail
) : CreateAlgo25Account {

    override suspend fun invoke(address: String, secretKey: ByteArray) {
        val algo25Account = LocalAccount.Algo25(address, secretKey)
        addAlgo25Account(algo25Account)
        cacheAccountDetail(address)
    }
}
