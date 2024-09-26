package com.algorand.android.module.account.local.data.mapper.model.ledgerble

import com.algorand.android.module.account.local.data.database.model.LedgerBleEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount

internal interface LedgerBleMapper {
    operator fun invoke(entity: LedgerBleEntity): LocalAccount.LedgerBle
}
