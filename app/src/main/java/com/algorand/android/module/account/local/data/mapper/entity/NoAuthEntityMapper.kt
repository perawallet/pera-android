package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.NoAuthEntity
import com.algorand.android.module.account.local.domain.model.LocalAccount

internal interface NoAuthEntityMapper {

    operator fun invoke(localAccount: LocalAccount.NoAuth): NoAuthEntity
}
