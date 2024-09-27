package com.algorand.android.module.account.info.data.helper.fetch

import com.algorand.android.module.account.info.data.model.AccountInformationResponse
import com.algorand.android.module.foundation.PeraResult

internal interface AccountInformationFetchHelper {
    suspend fun fetchAccount(address: String): PeraResult<AccountInformationResponse>
}
