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

package com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.usecase

import com.algorand.android.R
import com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.decider.RekeyToJointAccountIntroductionPreviewDecider
import com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.mapper.RekeyToJointAccountIntroductionPreviewMapper
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class RekeyToJointAccountInstructionPreviewUseCaseTest {

    private val getAccountType: GetAccountType = mockk()
    private val decider = RekeyToJointAccountIntroductionPreviewDecider()
    private val mapper = RekeyToJointAccountIntroductionPreviewMapper()

    private val sut = RekeyToJointAccountInstructionPreviewUseCase(
        rekeyToJointAccountIntroductionPreviewMapper = mapper,
        rekeyToJointAccountIntroductionPreviewDecider = decider,
        getAccountType = getAccountType
    )

    @Test
    fun `EXPECT correct title WHEN preview created for Joint account`() = runTest {
        coEvery { getAccountType(TEST_ADDRESS) } returns AccountType.Joint

        val result = sut.getInitialRekeyToJointAccountInstructionPreview(TEST_ADDRESS)

        assertEquals(R.string.rekey_to_joint_account_lower_case, result.titleAnnotatedString.stringResId)
    }

    @Test
    fun `EXPECT start process action button WHEN preview created`() = runTest {
        coEvery { getAccountType(TEST_ADDRESS) } returns AccountType.Joint

        val result = sut.getInitialRekeyToJointAccountInstructionPreview(TEST_ADDRESS)

        assertEquals(R.string.start_process, result.actionButtonAnnotatedString.stringResId)
    }

    @Test
    fun `EXPECT null feature tag WHEN preview created`() = runTest {
        coEvery { getAccountType(TEST_ADDRESS) } returns AccountType.Joint

        val result = sut.getInitialRekeyToJointAccountInstructionPreview(TEST_ADDRESS)

        assertNull(result.featureTag)
    }

    @Test
    fun `EXPECT null description WHEN account type is Joint`() = runTest {
        coEvery { getAccountType(TEST_ADDRESS) } returns AccountType.Joint

        val result = sut.getInitialRekeyToJointAccountInstructionPreview(TEST_ADDRESS)

        assertNull(result.descriptionAnnotatedString)
    }

    @Test
    fun `EXPECT two expectation list items WHEN Joint account`() = runTest {
        coEvery { getAccountType(TEST_ADDRESS) } returns AccountType.Joint

        val result = sut.getInitialRekeyToJointAccountInstructionPreview(TEST_ADDRESS)

        assertEquals(2, result.expectationListItems.size)
    }

    @Test
    fun `EXPECT rekey from rekeyed banner WHEN Joint account`() = runTest {
        coEvery { getAccountType(TEST_ADDRESS) } returns AccountType.Joint

        val result = sut.getInitialRekeyToJointAccountInstructionPreview(TEST_ADDRESS)

        assertEquals(R.drawable.ic_rekey_from_rekeyed_banner, result.bannerDrawableResId)
    }

    private companion object {
        const val TEST_ADDRESS = "TEST_JOINT_ACCOUNT_ADDRESS"
    }
}
