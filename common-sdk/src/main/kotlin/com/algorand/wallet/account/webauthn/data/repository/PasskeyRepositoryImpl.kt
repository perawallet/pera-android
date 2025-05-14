package com.algorand.wallet.account.webauthn.data.repository

import com.algorand.wallet.account.webauthn.data.database.dao.PasskeyDao
import com.algorand.wallet.account.webauthn.data.database.dao.SiteDao
import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import com.algorand.wallet.account.webauthn.data.database.model.SiteEntity
import com.algorand.wallet.account.webauthn.data.database.model.SiteWithPasskeysQuery
import com.algorand.wallet.account.webauthn.domain.model.Passkey
import com.algorand.wallet.account.webauthn.domain.repository.PasskeyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.KeyPair
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPrivateKeySpec
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Implementation of the `PasskeyRepository` interface.
 *
 * Provides methods for managing passkeys and associated site metadata.
 * This class communicates with the data layer through the `PasskeyDao` and `SiteDao` interfaces.
 *
 * @property passkeyDao An instance of `PasskeyDao` for performing database operations
 * related to passkeys.
 * @property siteDao An instance of `SiteDao` for performing database operations
 * related to sites.
 */
class PasskeyRepositoryImpl(
    private val passkeyDao: PasskeyDao,
    private val siteDao: SiteDao,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): PasskeyRepository {
    /**
     * Clears all stored data in the repository by removing all entries
     * from both the passkeys and sites tables in the database.
     *
     * This method ensures that the data managed by the repository,
     * including stored passkey entities and site entities, is completely removed.
     *
     * It performs the following operations:
     * - Invokes `clearAll` on the `passkeyDao` to delete all records from the passkeys table.
     * - Invokes `clearAll` on the `siteDao` to delete all records from the sites table.
     *
     * This operation is typically used when a full reset of the stored data is required.
     */
    override suspend fun clear() {
        withContext(coroutineDispatcher) {
            passkeyDao.clearAll()
            siteDao.clearAll()
        }
    }

    /**
     * Retrieves a stream of site data along with their associated passkeys from the database.
     *
     * This method interacts with the `siteDao` to fetch a real-time stream of `SiteWithPasskeysQuery` objects.
     * Each object contains information about a site (`SiteEntity`) and the list of associated passkeys (`PasskeyEntity` objects).
     *
     * @return A Flow that emits lists of `SiteWithPasskeysQuery` objects, representing sites and their related passkeys.
     */
    override fun getSitePasskeysAsFlow(): Flow<List<SiteWithPasskeysQuery>> {
        return siteDao.getPasskeysAsFlow()
    }

    /**
     * Retrieves passkeys associated with a specific site by its URL.
     *
     * @param url The URL of the site for which passkey data is requested. If null, the method returns null.
     * @return A `SiteWithPasskeysQuery` object containing the site and its associated passkeys,
     *         or null if the URL is null or no matching site is found.
     */
    override fun getSitePasskeys(url: String?): SiteWithPasskeysQuery? {
        if (url == null) {
            return null
        }
        return siteDao.getPasskeys(url)
    }

    override suspend fun getAllPasskeysAsFlow(): Flow<List<PasskeyEntity>> {
        return passkeyDao.getAllAsFlow()
    }
    override suspend fun getSite(siteId: Long): SiteEntity? {
        return siteDao.get(siteId)
    }
    /**
     * Adds a new site to the database.
     *
     * This method inserts a `SiteEntity` into the database using the `siteDao` component.
     * If a site with the same primary key already exists, the existing record is replaced.
     *
     * @param siteMetaData The metadata of the site to be added, encapsulated in a `SiteEntity`.
     *                     This includes details such as the site's URL, package name, and human-readable name.
     * @return The row ID of the newly inserted or updated site entry as a `Long`.
     */
    override suspend fun addSite(siteMetaData: SiteEntity): Long {
        return siteDao.insert(siteMetaData)
    }

    /**
     * Deletes a site entry from the repository based on the specified URL.
     *
     * @param url The URL of the site to be deleted. This is used to identify the
     *            specific site entry in the database that should be removed.
     */
    override suspend fun deleteSite(url: String) {
        return siteDao.delete(url)
    }


    /**
     * Derives a cryptographic key pair from the given passkey entity.
     *
     * This method processes the provided `PasskeyEntity` and generates a key pair
     * using the DP256 curve. It utilizes the encoded private and public key data
     * to create the specified key pair.
     *
     * @param passkey The `PasskeyEntity` containing the data necessary for key pair derivation.
     *                This includes fields such as user information, credential ID, and others.
     * @return A `KeyPair` consisting of the derived public and private keys.
     */
    @OptIn(ExperimentalEncodingApi::class)
    override fun deriveKeyPairFromPasskey(passkey: PasskeyEntity): KeyPair {
        val keyPair = TODO("Derive keypair from DP256")
        val publicKeyBytes = Base64.decode("")
        val privateKeyBytes = Base64.decode("")

        val params = AlgorithmParameters.getInstance("EC")
        params.init(ECGenParameterSpec("secp256r1"))
        val spec = params.getParameterSpec(ECParameterSpec::class.java)

        // Convert the private key bytes to a BigInteger.
        val bi = BigInteger(1, privateKeyBytes)
        // Create an EC private key specification from the BigInteger and the EC parameter specification.
        val privateKeySpec = ECPrivateKeySpec(bi, spec)

        val factory = KeyFactory.getInstance("EC")

        val publicKey = factory.generatePublic(X509EncodedKeySpec(publicKeyBytes))
        val privateKey = factory.generatePrivate(privateKeySpec)
        return KeyPair(publicKey, privateKey)
    }

    /**
     * Updates an existing passkey entity in the repository.
     *
     * This method updates the provided `PasskeyEntity` in the database by delegating
     * the operation to `passkeyDao`. The entity is identified and replaced based on
     * its primary key.
     *
     * @param passkey The `PasskeyEntity` containing updated passkey details, such as
     *                user information, credential data, or usage metadata.
     */
    override suspend fun updatePasskey(passkey: PasskeyEntity) {
        passkeyDao.update(passkey)
    }

    /**
     * Removes a given passkey from the repository. If the site associated with the passkey
     * no longer has any other passkeys, the site itself is also removed.
     *
     * @param passkey The `PasskeyEntity` representing the passkey to be removed. This includes
     *                details such as the credential ID and site association.
     */
    override suspend fun removePasskey(passkey: PasskeyEntity) {
        val siteId = passkey.siteId
        passkeyDao.delete(passkey.credentialId)
        if (siteDao.getPasskeySize(siteId) == 0) {
            siteDao.delete(siteId)
        }
    }

    /**
     * Adds a new passkey to the repository.
     *
     * This method inserts the provided `Passkey` into the database. If the site
     * associated with the passkey does not already exist in the database, a new
     * site entry is created before associating the passkey with it.
     *
     * @param passkeyMetadata The metadata of the passkey to be added. This includes information
     *                        such as the unique user ID (`uid`), the site's relying party ID (`rpid`),
     *                        the username, display name, and the credential ID (`credId`).
     */
    override suspend fun addNewPasskey(passkeyMetadata: Passkey) {
        val site = siteDao.get(passkeyMetadata.origin!!)
        val siteId = site?.id ?: addSite(SiteEntity(url = passkeyMetadata.origin, name = ""))

        passkeyDao.insert(
            PasskeyEntity(
                seedId = passkeyMetadata.seedId!!,
                userId = passkeyMetadata.uid,
                username = passkeyMetadata.username,
                userHandle = passkeyMetadata.displayName,
                credentialId = passkeyMetadata.credId,
                siteId = siteId,
                count = 0,
                lastUsedTimeMs = 0L,
            ),
        )
    }

    /**
     * Retrieves a passkey entity from the repository based on the specified credential ID.
     *
     * This method queries the `passkeyDao` to fetch a passkey entry associated with the provided
     * credential ID. If no matching passkey is found, the method returns null.
     *
     * @param credId The unique credential ID used to identify the desired passkey entity.
     * @return The corresponding `PasskeyEntity` if found; otherwise, null.
     */
    override suspend fun getPasskey(credId: String): PasskeyEntity? {
        return passkeyDao.get(credId)
    }
}
