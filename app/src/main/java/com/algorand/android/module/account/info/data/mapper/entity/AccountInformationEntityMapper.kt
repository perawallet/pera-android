package com.algorand.android.module.account.info.data.mapper.entity

import com.algorand.android.module.account.info.data.model.AccountInformationResponse
import com.algorand.android.module.shareddb.accountinformation.model.AccountInformationEntity

internal interface AccountInformationEntityMapper {
    operator fun invoke(response: AccountInformationResponse): AccountInformationEntity?
}
