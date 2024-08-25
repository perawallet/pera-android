package com.algorand.android.account.localaccount.data.mapper.model.noauth

import com.algorand.android.account.localaccount.data.database.model.NoAuthEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount

internal interface NoAuthMapper {
    operator fun invoke(entity: NoAuthEntity): LocalAccount.NoAuth
}
