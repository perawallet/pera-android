package com.algorand.android.module.account.local.data.mapper.model.noauth

import com.algorand.android.module.account.local.data.database.model.NoAuthEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount

internal interface NoAuthMapper {
    operator fun invoke(entity: NoAuthEntity): LocalAccount.NoAuth
}
