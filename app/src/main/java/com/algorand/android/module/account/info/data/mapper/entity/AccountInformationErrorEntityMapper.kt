package com.algorand.android.module.account.info.data.mapper.entity

import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity

internal interface AccountInformationErrorEntityMapper {
    operator fun invoke(address: String): AccountInformationEntity
}
