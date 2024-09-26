package com.algorand.android.module.account.info.data.mapper.entity

import com.algorand.android.module.account.info.data.model.AccountInformationResponse
import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity

internal interface AccountInformationEntityMapper {
    operator fun invoke(response: AccountInformationResponse): AccountInformationEntity?
}
