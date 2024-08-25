package com.algorand.android.accountinfo.component.data.mapper.entity

import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity

internal interface AccountInformationErrorEntityMapper {
    operator fun invoke(address: String): AccountInformationEntity
}
