package com.algorand.android.core.component.caching.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.foundation.PeraResult

interface CacheAccountDetail {
    suspend operator fun invoke(address: String): PeraResult<AccountInformation>
}
