package com.algorand.android.account.localaccount.data.mapper.entity

import com.algorand.android.account.localaccount.data.database.model.NoAuthEntity
import com.algorand.android.account.localaccount.domain.model.LocalAccount

internal interface NoAuthEntityMapper {

    operator fun invoke(localAccount: LocalAccount.NoAuth): NoAuthEntity
}
