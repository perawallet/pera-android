package com.algorand.android.module.account.local.data.mapper.model.ledgerusb

import com.algorand.android.module.account.local.data.database.model.LedgerUsbEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount

internal interface LedgerUsbMapper {
    operator fun invoke(entity: LedgerUsbEntity): LocalAccount.LedgerUsb
}
