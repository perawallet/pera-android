package com.algorand.android.account.localaccount.data.mapper.model.algo25

import com.algorand.android.account.localaccount.data.database.model.Algo25Entity
import com.algorand.android.account.localaccount.domain.model.LocalAccount

internal interface Algo25Mapper {
    operator fun invoke(entity: Algo25Entity): LocalAccount.Algo25
}
