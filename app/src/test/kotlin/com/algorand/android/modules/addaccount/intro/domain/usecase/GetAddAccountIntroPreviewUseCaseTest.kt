/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.addaccount.intro.domain.usecase

import com.algorand.android.modules.addaccount.intro.domain.model.AddAccountIntroPreview
import com.algorand.android.modules.addaccount.intro.mapper.AddAccountIntroPreviewMapper
import com.algorand.wallet.account.local.domain.usecase.GetHasAnyHdSeedId
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GetAddAccountIntroPreviewUseCaseTest {

    private val addAccountIntroPreviewMapper: AddAccountIntroPreviewMapper = mockk()
    private val hasAnyHdSeedId: GetHasAnyHdSeedId = mockk()
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount = mockk()

    private val sut = GetAddAccountIntroPreviewUseCase(
        addAccountIntroPreviewMapper = addAccountIntroPreviewMapper,
        hasAnyHdSeedId = hasAnyHdSeedId,
        isThereAnyLocalAccount = isThereAnyLocalAccount
    )

    @Test
    fun `EXPECT preview WHEN flow emits successfully`() = runTest {
        val expectedPreview = mockk<AddAccountIntroPreview>()
        coEvery { hasAnyHdSeedId() } returns true
        coEvery { isThereAnyLocalAccount() } returns true
        every {
            addAccountIntroPreviewMapper(
                isShowingCloseButton = true,
                hasHdWallet = true,
                hasLocalAccount = true
            )
        } returns expectedPreview

        val result = sut(isShowingCloseButton = true).first()

        assertEquals(expectedPreview, result)
    }

    @Test
    fun `EXPECT hasHdWallet false WHEN no HD seed exists`() = runTest {
        val expectedPreview = mockk<AddAccountIntroPreview>()
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns true
        every {
            addAccountIntroPreviewMapper(
                isShowingCloseButton = false,
                hasHdWallet = false,
                hasLocalAccount = true
            )
        } returns expectedPreview

        sut(isShowingCloseButton = false).first()

        coVerify {
            addAccountIntroPreviewMapper(
                isShowingCloseButton = false,
                hasHdWallet = false,
                hasLocalAccount = true
            )
        }
    }

    @Test
    fun `EXPECT hasLocalAccount false WHEN no local account exists`() = runTest {
        val expectedPreview = mockk<AddAccountIntroPreview>()
        coEvery { hasAnyHdSeedId() } returns true
        coEvery { isThereAnyLocalAccount() } returns false
        every {
            addAccountIntroPreviewMapper(
                isShowingCloseButton = true,
                hasHdWallet = true,
                hasLocalAccount = false
            )
        } returns expectedPreview

        sut(isShowingCloseButton = true).first()

        coVerify {
            addAccountIntroPreviewMapper(
                isShowingCloseButton = true,
                hasHdWallet = true,
                hasLocalAccount = false
            )
        }
    }

    @Test
    fun `EXPECT mapper called with correct isShowingCloseButton value`() = runTest {
        val expectedPreview = mockk<AddAccountIntroPreview>()
        coEvery { hasAnyHdSeedId() } returns false
        coEvery { isThereAnyLocalAccount() } returns false
        every {
            addAccountIntroPreviewMapper(
                isShowingCloseButton = false,
                hasHdWallet = false,
                hasLocalAccount = false
            )
        } returns expectedPreview

        sut(isShowingCloseButton = false).first()

        coVerify {
            addAccountIntroPreviewMapper(
                isShowingCloseButton = false,
                hasHdWallet = false,
                hasLocalAccount = false
            )
        }
    }
}
