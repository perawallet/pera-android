package com.algorand.android.accountinfo.component.data.helper.fetch

import com.algorand.android.accountinfo.component.data.model.AccountInformationResponse
import com.algorand.android.foundation.PeraResult

internal interface AccountInformationFetchHelper {
    suspend fun fetchAccount(address: String): PeraResult<AccountInformationResponse>
}
