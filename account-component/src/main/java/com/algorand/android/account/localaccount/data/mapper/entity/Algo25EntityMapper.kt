package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.Algo25Entity
import com.algorand.android.account.localaccount.domain.model.LocalAccount

internal interface Algo25EntityMapper {

    operator fun invoke(localAccount: LocalAccount.Algo25): Algo25Entity
}
