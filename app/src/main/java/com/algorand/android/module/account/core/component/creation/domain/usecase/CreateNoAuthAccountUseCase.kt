package com.algorand.android.module.account.core.component.creation.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.*
import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
import javax.inject.Inject

internal class CreateNoAuthAccountUseCase @Inject constructor(
    private val addNoAuthAccount: AddNoAuthAccount,
    private val cacheAccountDetail: CacheAccountDetail
) : CreateNoAuthAccount {

    override suspend fun invoke(address: String) {
        val noAuthAccount = LocalAccount.NoAuth(address)
        addNoAuthAccount(noAuthAccount)
        cacheAccountDetail(address)
    }
}
