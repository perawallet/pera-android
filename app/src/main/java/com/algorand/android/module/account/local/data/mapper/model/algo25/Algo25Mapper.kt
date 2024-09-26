package com.algorand.android.module.account.local.data.mapper.model.algo25

import com.algorand.android.module.account.local.data.database.model.Algo25Entity
import com.algorand.android.module.account.local.domain.model.LocalAccount

internal interface Algo25Mapper {
    operator fun invoke(entity: Algo25Entity): LocalAccount.Algo25
}
