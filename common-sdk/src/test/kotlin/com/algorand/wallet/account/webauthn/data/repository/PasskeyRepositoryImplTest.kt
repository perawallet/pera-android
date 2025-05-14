package com.algorand.wallet.account.webauthn.data.repository

import com.algorand.test.test
import com.algorand.wallet.account.webauthn.data.database.dao.PasskeyDao
import com.algorand.wallet.account.webauthn.data.database.dao.SiteDao
import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PasskeyRepositoryImplTest {
    private val passkeyDao: PasskeyDao = mockk()
    private val siteDao: SiteDao = mockk()

    private val sut = PasskeyRepositoryImpl(
        passkeyDao,
        siteDao
    )
    @Test
    fun `EXPECT all passkeys as flow WHEN getAllPasskeysAsFlow is invoked`() = runTest {
        val entitiesFlow = MutableStateFlow(
            listOf(
                PasskeyEntity(1, 1, "1234", "USER_ID_1", "UserName", "User Handle", "CREDENTIAL_ID", 0, 0),
                PasskeyEntity(1, 1, "1235", "USER_ID_1", "UserName", "User Handle", "CREDENTIAL_ID", 0, 0),
            )
        )
        val expectedPasskeys = listOf(
            PasskeyEntity(1, 1, "1234", "USER_ID_1", "UserName", "User Handle", "CREDENTIAL_ID", 0, 0),
            PasskeyEntity(1, 1, "1235", "USER_ID_1", "UserName", "User Handle", "CREDENTIAL_ID", 0, 0),
        )

        coEvery { passkeyDao.getAllAsFlow() } returns entitiesFlow

        val testObserver = sut.getAllPasskeysAsFlow().test()
        entitiesFlow.update { emptyList() }

        testObserver.assertValueHistory(expectedPasskeys, emptyList())
    }
}