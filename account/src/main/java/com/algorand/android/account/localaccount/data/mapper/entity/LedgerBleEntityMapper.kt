package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.LedgerBleEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount

internal interface LedgerBleEntityMapper {

    operator fun invoke(localAccount: LocalAccount.LedgerBle): LedgerBleEntity
}
