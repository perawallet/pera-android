package com.algorand.android.account.accountinformation.data.mapper.entity

import com.algorand.android.module.shareddb.accountinformation.model.AccountInformationEntity
import com.algorand.android.account.accountinformation.data.model.*
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigInteger

class AccountInformationEntityMapperImplTest {

    private val encryptionManager: EncryptionManager = mock {
        on { encrypt(com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ADDRESS) } doReturn com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ENCRYPTED_ADDRESS
    }
    private val sut = com.algorand.android.module.account.info.data.mapper.entity.AccountInformationEntityMapperImpl(
        encryptionManager
    )

    @Test
    fun `EXPECT account information to be mapped successfully`() {
        val result =
            sut(com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ACCOUNT_INFORMATION_RESPONSE)

        assertEquals(
            com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ACCOUNT_INFORMATION_ENTITY,
            result
        )
    }

    @Test
    fun `EXPECT null WHEN address is missing`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ACCOUNT_INFORMATION_PAYLOAD.copy(
                address = null
            ),
            currentRound = 9
        )

        val result = sut(accountInformationResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN current round is missing`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ACCOUNT_INFORMATION_PAYLOAD.copy(
                address = null
            ),
            currentRound = 9
        )

        val result = sut(accountInformationResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN algo amount is missing`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ACCOUNT_INFORMATION_PAYLOAD.copy(
                address = null
            ),
            currentRound = 9
        )

        val result = sut(accountInformationResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT default values WHEN optional fields are missing`() {
        val accountInformationResponse = AccountInformationResponse(
            accountInformation = com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ACCOUNT_INFORMATION_PAYLOAD.copy(
                totalAppsOptedIn = null,
                totalCreatedApps = null,
                totalCreatedAssets = null,
                appsTotalExtraPages = null
            ),
            currentRound = 9
        )

        val result = sut(accountInformationResponse)

        val expected =
            com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ACCOUNT_INFORMATION_ENTITY.copy(
                optedInAppsCount = 0,
                totalCreatedAppsCount = 0,
                totalCreatedAssetsCount = 0,
                appsTotalExtraPages = 0,
            )
        assertEquals(expected, result)
    }

    companion object {
        private const val ADDRESS = "address"
        private const val ENCRYPTED_ADDRESS = "encrypted_address"

        private val ACCOUNT_INFORMATION_PAYLOAD = fixtureOf<AccountInformationResponsePayloadResponse>().copy(
            address = com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ADDRESS,
            amount = BigInteger.TEN,
            participation = null,
            rekeyAdminAddress = "rekeyAddress",
            allAssetHoldingList = emptyList(),
            createdAtRound = 9,
            appStateSchemaResponse = AppStateSchemaResponse(
                numByteSlice = 21,
                numUint = 12
            ),
            appsTotalExtraPages = 2,
            totalAppsOptedIn = 9,
            totalAssetsOptedIn = 4,
            totalCreatedAssets = 0,
            totalCreatedApps = 0
        )
        private val ACCOUNT_INFORMATION_RESPONSE = AccountInformationResponse(
            accountInformation = com.algorand.android.account.accountinformation.data.mapper.entity.AccountInformationEntityMapperImplTest.Companion.ACCOUNT_INFORMATION_PAYLOAD,
            currentRound = 9
        )

        private val ACCOUNT_INFORMATION_ENTITY = AccountInformationEntity(
            encryptedAddress = ENCRYPTED_ADDRESS,
            algoAmount = BigInteger.TEN,
            lastFetchedRound = 9,
            authAddress = "rekeyAddress",
            optedInAppsCount = 9,
            appsTotalExtraPages = 2,
            createdAtRound = 9,
            totalCreatedAssetsCount = 0,
            totalCreatedAppsCount = 0,
            appStateNumByteSlice = 21,
            appStateSchemaUint = 12
        )
    }
}
