package com.algorand.android.module.account.info.data.mapper.entity

import com.algorand.android.module.shareddb.accountinformation.model.AccountInformationEntity

internal interface AccountInformationErrorEntityMapper {
    operator fun invoke(address: String): AccountInformationEntity
}
