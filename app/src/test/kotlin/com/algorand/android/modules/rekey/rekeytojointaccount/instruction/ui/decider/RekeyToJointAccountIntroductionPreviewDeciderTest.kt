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

package com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.decider

import com.algorand.android.R
import com.algorand.wallet.account.detail.domain.model.AccountType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class RekeyToJointAccountIntroductionPreviewDeciderTest {

    private val decider = RekeyToJointAccountIntroductionPreviewDecider()

    @Test
    fun `EXPECT rekey from rekeyed banner WHEN account type is Joint`() {
        val result = decider.decideBannerDrawableResId(AccountType.Joint)

        assertEquals(R.drawable.ic_rekey_from_rekeyed_banner, result)
    }

    @Test
    fun `EXPECT null description WHEN account type is Joint`() {
        val result = decider.decideDescriptionAnnotatedString(AccountType.Joint)

        assertNull(result)
    }

    @Test
    fun `EXPECT non-empty expectation list WHEN account type is Joint`() {
        val result = decider.decideExpectationListItems(AccountType.Joint)

        assertTrue(result.isNotEmpty())
        assertEquals(2, result.size)
    }

    @Test
    fun `EXPECT expectation list contains future transactions item`() {
        val result = decider.decideExpectationListItems(AccountType.Joint)

        assertEquals(R.string.future_transactions_can_only, result[0].stringResId)
    }

    @Test
    fun `EXPECT expectation list contains public key item`() {
        val result = decider.decideExpectationListItems(AccountType.Joint)

        assertEquals(R.string.your_account_s_public_key, result[1].stringResId)
    }

    @Test
    fun `EXPECT banner for null account type`() {
        val result = decider.decideBannerDrawableResId(null)

        assertEquals(R.drawable.ic_rekey_from_rekeyed_banner, result)
    }
}
