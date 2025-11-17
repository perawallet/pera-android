package com.algorand.wallet.account.webauthn.data.mapper.entity

import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import com.algorand.wallet.account.webauthn.domain.model.Passkey

class PasskeyEntityMapperImpl() {
    fun invoke(passkey: Passkey): PasskeyEntity {
        return PasskeyEntity(
            userId = passkey.uid,
            username = passkey.username,
            userHandle = passkey.userHandle,
            seedId = passkey.seedId!!,
            siteId = passkey.siteId!!,
            count = passkey.count,
            lastUsedTimeMs = passkey.lastUsed,
            credentialId = passkey.credId
        )
    }
}
