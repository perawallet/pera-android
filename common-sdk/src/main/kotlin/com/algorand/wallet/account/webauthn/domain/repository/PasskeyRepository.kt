package com.algorand.wallet.account.webauthn.domain.repository

import com.algorand.wallet.account.custom.domain.model.CustomHdSeedInfo
import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import com.algorand.wallet.account.webauthn.data.database.model.SiteEntity
import com.algorand.wallet.account.webauthn.data.database.model.SiteWithPasskeysQuery
import com.algorand.wallet.account.webauthn.domain.model.Passkey
import kotlinx.coroutines.flow.Flow
import java.security.KeyPair

/**
 * Provides an interface for managing passkeys and site-related data.
 *
 * The `PasskeyRepository` interface serves as a centralized contract for managing operations
 * involving `SiteEntity`, `PasskeyEntity`, and their relationships. It supports CRUD operations
 * for sites and passkeys, as well as retrieving passkey data for usage or key pair derivation.
 *
 * Key functionalities include:
 * - Clearing all stored passkey and site data.
 * - Fetching passkeys associated with a site, either as a flow or synchronously.
 * - Adding, deleting, and updating site and passkey data.
 * - Deriving cryptographic key pairs from stored passkeys.
 */
interface PasskeyRepository {
    suspend fun clear()
    suspend fun getSitePasskeysSize(url: String): Int?
    fun getSitePasskeysAsFlow(): Flow<List<SiteWithPasskeysQuery>>
    suspend fun getSitePasskeys(url: String): SiteWithPasskeysQuery
    suspend fun getSite(siteId: Long): SiteEntity?
    suspend fun addSite(siteMetaData: SiteEntity): Long
    suspend fun deleteSite(url: String)
    suspend fun updatePasskey(passkey: PasskeyEntity)
    suspend fun removePasskey(passkey: PasskeyEntity)
    suspend fun addNewPasskey(passkeyMetadata: Passkey)
    suspend fun getPasskey(credId: String): PasskeyEntity?
    suspend fun getAllPasskeysAsFlow(): Flow<List<PasskeyEntity>>
    suspend fun getAllCustomHdSeedInfo(): List<CustomHdSeedInfo>
}
