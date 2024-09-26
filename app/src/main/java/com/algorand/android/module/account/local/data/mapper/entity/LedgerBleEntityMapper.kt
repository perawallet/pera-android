package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.LedgerBleEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount

internal interface LedgerBleEntityMapper {

    operator fun invoke(localAccount: LocalAccount.LedgerBle): LedgerBleEntity
}
