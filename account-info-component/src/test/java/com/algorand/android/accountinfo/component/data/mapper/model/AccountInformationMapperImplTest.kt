package com.algorand.android.account.accountinformation.data.mapper.model

import com.algorand.android.module.shareddb.accountinformation.model.AccountInformationEntity
import com.algorand.android.account.accountinformation.data.model.*
import com.algorand.android.account.accountinformation.domain.model.*
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigInteger

class AccountInformationMapperImplTest {

    private val appStateSchemeMapper: AppStateSchemeMapper = mock()
    private val assetHoldingMapper: AssetHoldingMapper = mock()
    private val encryptionManager: EncryptionManager = mock {
        on { decrypt(ENCRYPTED_ADDRESS) } doReturn ADDRESS
    }

    private val sut = AccountInformationMapperImpl(
        appStateSchemeMapper,
        assetHoldingMapper,
        encryptionManager
    )

    @Test
    fun `EXPECT mapped values to be correct`() {
        whenever(appStateSchemeMapper(APP_STATE_SCHEMA_RESPONSE)).thenReturn(APP_STATE_SCHEMA)
        whenever(assetHoldingMapper(ASSET_HOLDING_RESPONSE)).thenReturn(ASSET_HOLDING)
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = ACCOUNT_INFORMATION_RESPONSE,
            currentRound = 123L
        )

        val result = sut(accountInformationResponse)

        assertEquals(ACCOUNT_INFORMATION, result)
    }

    @Test
    fun `EXPECT mapped values to be correct with asset holdings`() {
        whenever(appStateSchemeMapper(APP_STATE_SCHEMA.numByteSlice, APP_STATE_SCHEMA.numUint))
            .thenReturn(APP_STATE_SCHEMA)
        val assetHoldingList = fixtureOf<List<AssetHolding>>()

        val result = sut(ACCOUNT_INFORMATION_ENTITY, assetHoldingList)

        val expected = AccountInformation(
            address = ADDRESS,
            amount = ACCOUNT_INFORMATION_ENTITY.algoAmount,
            lastFetchedRound = ACCOUNT_INFORMATION_ENTITY.lastFetchedRound,
            rekeyAdminAddress = ACCOUNT_INFORMATION_ENTITY.authAddress,
            totalAppsOptedIn = ACCOUNT_INFORMATION_ENTITY.optedInAppsCount,
            totalAssetsOptedIn = assetHoldingList.size,
            totalCreatedAssets = ACCOUNT_INFORMATION_ENTITY.totalCreatedAssetsCount,
            totalCreatedApps = ACCOUNT_INFORMATION_ENTITY.totalCreatedAppsCount,
            appsTotalExtraPages = ACCOUNT_INFORMATION_ENTITY.appsTotalExtraPages,
            appsTotalSchema = APP_STATE_SCHEMA,
            assetHoldings = assetHoldingList
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN fields are null`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = null,
            currentRound = null
        )

        val result = sut(accountInformationResponse)

        assertEquals(null, result)
    }

    @Test
    fun `EXPECT default values WHEN non crucial fields are null`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = ACCOUNT_INFORMATION_RESPONSE_WITH_OPTIONAL_FIELDS_NULL,
            currentRound = 1234,
        )

        val result = sut(accountInformationResponse)

        assertEquals(ACCOUNT_INFORMATION_WITH_DEFAULT_VALUES, result)
    }

    companion object {

        private const val ADDRESS = "address"
        private const val ENCRYPTED_ADDRESS = "encrypted_address"

        private val ASSET_HOLDING_RESPONSE = AssetHoldingResponse(
            assetId = 1L,
            amount = BigInteger.TEN,
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = 123L,
            optedOutAtRound = 123L
        )
        private val ASSET_HOLDING = AssetHolding(
            amount = BigInteger.TEN,
            assetId = 1L,
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = 123L,
            optedOutAtRound = 123L
        )
        private val APP_STATE_SCHEMA_RESPONSE = AppStateSchemaResponse(5, 5)
        private val APP_STATE_SCHEMA = AppStateScheme(5, 5)
        private val ACCOUNT_INFORMATION_RESPONSE = AccountInformationResponsePayloadResponse(
            address = "address",
            amount = BigInteger.TEN,
            rekeyAdminAddress = "rekeyAdminAddress",
            totalAppsOptedIn = 1,
            totalAssetsOptedIn = 2,
            totalCreatedApps = 3,
            totalCreatedAssets = 4,
            participation = ParticipationResponse(voteParticipationKey = "voteParticipationKey"),
            appsTotalExtraPages = 5,
            appStateSchemaResponse = APP_STATE_SCHEMA_RESPONSE,
            allAssetHoldingList = mutableListOf(ASSET_HOLDING_RESPONSE),
            createdAtRound = 123L
        )

        private val ACCOUNT_INFORMATION = AccountInformation(
            address = "address",
            amount = BigInteger.TEN,
            lastFetchedRound = 123L,
            rekeyAdminAddress = "rekeyAdminAddress",
            totalAppsOptedIn = 1,
            totalAssetsOptedIn = 2,
            totalCreatedApps = 3,
            totalCreatedAssets = 4,
            appsTotalExtraPages = 5,
            appsTotalSchema = APP_STATE_SCHEMA,
            assetHoldings = listOf(ASSET_HOLDING)
        )

        private val ACCOUNT_INFORMATION_RESPONSE_WITH_OPTIONAL_FIELDS_NULL = AccountInformationResponsePayloadResponse(
            address = "address",
            amount = BigInteger.TEN,
            rekeyAdminAddress = null,
            totalAppsOptedIn = null,
            totalAssetsOptedIn = null,
            totalCreatedApps = null,
            totalCreatedAssets = null,
            participation = null,
            appsTotalExtraPages = null,
            appStateSchemaResponse = null,
            allAssetHoldingList = null,
            createdAtRound = null
        )

        private val ACCOUNT_INFORMATION_WITH_DEFAULT_VALUES = AccountInformation(
            address = "address",
            amount = BigInteger.TEN,
            lastFetchedRound = 1234,
            rekeyAdminAddress = null,
            totalAppsOptedIn = 0,
            totalAssetsOptedIn = 0,
            totalCreatedApps = 0,
            totalCreatedAssets = 0,
            appsTotalExtraPages = 0,
            appsTotalSchema = null,
            assetHoldings = emptyList()
        )

        private val ACCOUNT_INFORMATION_ENTITY = fixtureOf<AccountInformationEntity>().copy(
            encryptedAddress = ENCRYPTED_ADDRESS,
            appStateSchemaUint = APP_STATE_SCHEMA.numUint,
            appStateNumByteSlice = APP_STATE_SCHEMA.numByteSlice
        )
    }
}
