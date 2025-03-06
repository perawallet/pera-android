package com.algorand.wallet.account.info.data.mapper.model

import com.algorand.wallet.account.info.data.model.AccountFastLookupResponse
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import javax.inject.Inject

internal class AccountFastLookupMapperImpl @Inject constructor() : AccountFastLookupMapper {
    override fun invoke(
        response: AccountFastLookupResponse
    ): AccountFastLookup {
        return AccountFastLookup(
            algoValue = response.algoValue,
            usdValue = response.usdValue,
            calculationType = response.calculationType,
            accountExists = response.accountExists,
        )
    }
}
