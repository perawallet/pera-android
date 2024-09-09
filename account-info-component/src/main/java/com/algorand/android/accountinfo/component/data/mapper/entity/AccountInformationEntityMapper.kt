package com.algorand.android.accountinfo.component.data.mapper.entity

import com.algorand.android.accountinfo.component.data.model.AccountInformationResponse
import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity

internal interface AccountInformationEntityMapper {
    operator fun invoke(response: AccountInformationResponse): AccountInformationEntity?
}
