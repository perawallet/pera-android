package com.algorand.android.module.account.local.data.mapper.entity

import com.algorand.android.module.account.local.data.database.model.Algo25Entity
import com.algorand.android.module.account.local.domain.model.LocalAccount

internal interface Algo25EntityMapper {

    operator fun invoke(localAccount: LocalAccount.Algo25): Algo25Entity
}
