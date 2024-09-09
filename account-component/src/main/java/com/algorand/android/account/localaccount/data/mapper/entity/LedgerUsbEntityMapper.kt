package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.LedgerUsbEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount

internal interface LedgerUsbEntityMapper {

    operator fun invoke(localAccount: LocalAccount.LedgerUsb): LedgerUsbEntity
}
