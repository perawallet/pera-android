package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.LedgerUsbEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount

internal interface LedgerUsbEntityMapper {

    operator fun invoke(localAccount: LocalAccount.LedgerUsb): LedgerUsbEntity
}
