package com.algorand.wallet.account.webauthn.data.mapper.entity

import com.algorand.wallet.account.webauthn.data.database.model.SiteEntity

internal interface SiteEntityMapper {
    operator fun invoke(url: String, name: String = "", packageName: String = ""): SiteEntity
}