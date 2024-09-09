package com.algorand.android.account.localaccount.data.mapper.model.ledgerble

import com.algorand.android.account.localaccount.data.database.model.LedgerBleEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount

internal interface LedgerBleMapper {
    operator fun invoke(entity: LedgerBleEntity): LocalAccount.LedgerBle
}
