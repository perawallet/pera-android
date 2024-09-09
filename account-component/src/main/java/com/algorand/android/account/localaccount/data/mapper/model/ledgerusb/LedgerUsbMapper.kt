package com.algorand.android.account.localaccount.data.mapper.model.ledgerusb

import com.algorand.android.account.localaccount.data.database.model.LedgerUsbEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount

internal interface LedgerUsbMapper {
    operator fun invoke(entity: LedgerUsbEntity): LocalAccount.LedgerUsb
}
