package com.algorand.wallet

import android.content.Context
import android.content.SharedPreferences
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class InstallReferrerHelperTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var referrerClient: InstallReferrerClient
    private lateinit var referrerDetails: ReferrerDetails
    private lateinit var referrerClientBuilder: InstallReferrerClient.Builder

    private val sut = InstallReferrerHelper

    val referrerUrl = "utm_source=pera_website&utm_medium=referral&utm_campaign=download_app"

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        sharedPreferences = mockk(relaxed = true)
        sharedPreferencesEditor = mockk(relaxed = true)
        referrerClient = mockk(relaxed = true)
        referrerDetails = mockk(relaxed = true)
        referrerClientBuilder = mockk(relaxed = true)

        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.putString(any(), any()) } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.apply() } returns Unit

        mockkStatic(InstallReferrerClient::class)
        every { InstallReferrerClient.newBuilder(any<Context>()) } returns referrerClientBuilder
        every { referrerClientBuilder.build() } returns referrerClient
        every { referrerClient.endConnection() } returns Unit
        every { referrerClient.installReferrer } returns referrerDetails
    }

    @Test
    fun `EXPECT referrer data to be saved WHEN fetchInstallReferrer completes successfully`() {
        every { referrerDetails.installReferrer } returns referrerUrl

        every { referrerClient.startConnection(any()) } answers {
            val listener = firstArg<InstallReferrerStateListener>()
            listener.onInstallReferrerSetupFinished(InstallReferrerClient.InstallReferrerResponse.OK)
        }

        sut.fetchInstallReferrer(context)

        verify { referrerClient.startConnection(any()) }
        verify { referrerClient.installReferrer }
        verify { referrerClient.endConnection() }

        verify { sharedPreferencesEditor.putString("utm_source", "pera_website") }
        verify { sharedPreferencesEditor.putString("utm_medium", "referral") }
        verify { sharedPreferencesEditor.putString("utm_campaign", "download_app") }
        verify { sharedPreferencesEditor.apply() }
    }

    @Test
    fun `EXPECT no data to be saved WHEN feature is not supported`() {
        // Arrange
        every { referrerClient.startConnection(any()) } answers {
            val listener = firstArg<InstallReferrerStateListener>()
            listener.onInstallReferrerSetupFinished(InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED)
        }

        sut.fetchInstallReferrer(context)

        verify { referrerClient.startConnection(any()) }
        verify { referrerClient.endConnection() }
        verify(exactly = 0) { referrerClient.installReferrer }
        verify(exactly = 0) { sharedPreferencesEditor.putString(any(), any()) }
    }

    @Test
    fun `EXPECT no data to be saved WHEN service is unavailable`() {
        every { referrerClient.startConnection(any()) } answers {
            val listener = firstArg<InstallReferrerStateListener>()
            listener.onInstallReferrerSetupFinished(InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE)
        }

        sut.fetchInstallReferrer(context)

        verify { referrerClient.startConnection(any()) }
        verify { referrerClient.endConnection() }
        verify(exactly = 0) { referrerClient.installReferrer }
        verify(exactly = 0) { sharedPreferencesEditor.putString(any(), any()) }
    }

    @Test
    fun `EXPECT no data to be saved WHEN service is disconnected`() {
        every { referrerClient.startConnection(any()) } answers {
            val listener = firstArg<InstallReferrerStateListener>()
            listener.onInstallReferrerServiceDisconnected()
        }

        sut.fetchInstallReferrer(context)

        verify { referrerClient.startConnection(any()) }
        verify(exactly = 0) { referrerClient.endConnection() }
        verify(exactly = 0) { referrerClient.installReferrer }
        verify(exactly = 0) { sharedPreferencesEditor.putString(any(), any()) }
    }

    @Test
    fun `EXPECT no data to be saved WHEN referrerString is null`() {
        sut.saveReferrerData(context, null)

        verify(exactly = 0) { sharedPreferencesEditor.putString(any(), any()) }
    }

    @Test
    fun `EXPECT UTM parameters to be saved WHEN referrerString is valid`() {
        sut.saveReferrerData(context, referrerUrl)

        verify { sharedPreferencesEditor.putString("utm_source", "pera_website") }
        verify { sharedPreferencesEditor.putString("utm_medium", "referral") }
        verify { sharedPreferencesEditor.putString("utm_campaign", "download_app") }
        verify { sharedPreferencesEditor.apply() }
    }

    @Test
    fun `EXPECT empty map WHEN query is null`() {
        val result = sut.decodeQueryParams(null)

        assertEquals(emptyMap<String, String>(), result)
    }

    @Test
    fun `EXPECT empty map WHEN query is empty`() {
        val result = sut.decodeQueryParams("")

        assertEquals(emptyMap<String, String>(), result)
    }

    @Test
    fun `EXPECT decoded parameters WHEN query has multiple parameters`() {

        val result = sut.decodeQueryParams(referrerUrl)

        assertEquals(
            mapOf(
                "utm_source" to "pera_website",
                "utm_medium" to "referral",
                "utm_campaign" to "download_app"
            ),
            result
        )
    }

    @Test
    fun `EXPECT decoded parameters WHEN query has URL encoded values`() {
        val result = sut.decodeQueryParams("$referrerUrl%21")

        assertEquals(
            mapOf(
                "utm_source" to "pera_website",
                "utm_medium" to "referral",
                "utm_campaign" to "download_app!"
            ),
            result
        )
    }

    @Test
    fun `EXPECT last value WHEN query has duplicate parameters`() {
        val query = "utm_source=play_store&utm_source=play_store&utm_campaign=download_app"

        val result = sut.decodeQueryParams(query)

        assertEquals(
            mapOf(
                "utm_source" to "play_store",
                "utm_campaign" to "download_app"
            ),
            result
        )
    }
}
