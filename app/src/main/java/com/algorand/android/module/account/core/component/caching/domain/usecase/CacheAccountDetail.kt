package com.algorand.android.module.account.core.component.caching.domain.usecase

import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.foundation.PeraResult

interface CacheAccountDetail {
    suspend operator fun invoke(address: String): PeraResult<AccountInformation>
}
