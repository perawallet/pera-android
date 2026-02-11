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

package com.algorand.android.modules.rekey.rekeytojointaccount.accountselection.ui.usecase

import com.algorand.android.modules.accountcore.domain.usecase.GetAccountTotalValue
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.accountsorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.modules.basesingleaccountselection.ui.mapper.SingleAccountSelectionListItemMapper
import com.algorand.android.modules.basesingleaccountselection.ui.model.SingleAccountSelectionListItem
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.currency.domain.usecase.GetSecondaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.rekey.rekeytojointaccount.accountselection.ui.mapper.RekeyToJointAccountSelectionPreviewMapper
import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class RekeyToJointAccountSelectionPreviewUseCaseTest {

    private val previewMapper = RekeyToJointAccountSelectionPreviewMapper()
    private val getSortedLocalAccounts: GetSortedLocalAccounts = mockk()
    private val getAccountType: GetAccountType = mockk()
    private val getAccountDisplayName: GetAccountDisplayName = mockk()
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName = mockk()
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol = mockk()
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo = mockk()
    private val getAccountTotalValue: GetAccountTotalValue = mockk()
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview = mockk()
    private val singleAccountSelectionListItemMapper: SingleAccountSelectionListItemMapper = mockk()

    private val sut = RekeyToJointAccountSelectionPreviewUseCase(
        rekeyToJointAccountSelectionPreviewMapper = previewMapper,
        getSortedLocalAccounts = getSortedLocalAccounts,
        getAccountType = getAccountType,
        getAccountDisplayName = getAccountDisplayName,
        getPrimaryCurrencySymbolOrName = getPrimaryCurrencySymbolOrName,
        getSecondaryCurrencySymbol = getSecondaryCurrencySymbol,
        isPrimaryCurrencyAlgo = isPrimaryCurrencyAlgo,
        getAccountTotalValue = getAccountTotalValue,
        getAccountIconDrawablePreview = getAccountIconDrawablePreview,
        singleAccountSelectionListItemMapper = singleAccountSelectionListItemMapper
    )

    @Test
    fun `EXPECT loading initial preview WHEN getInitialRekeyToJointAccountSelectionPreview called`() {
        val result = sut.getInitialRekeyToJointAccountSelectionPreview()

        assertTrue(result.isLoading)
        assertTrue(result.singleAccountSelectionListItems.isEmpty())
        assertNull(result.screenState)
    }

    @Test
    fun `EXPECT only joint accounts listed WHEN multiple account types exist`() = runTest {
        val accounts = listOf(
            AccountOrderIndex(JOINT_ADDRESS_1, 0),
            AccountOrderIndex(STANDARD_ADDRESS, 1),
            AccountOrderIndex(JOINT_ADDRESS_2, 2),
            AccountOrderIndex(LEDGER_ADDRESS, 3)
        )
        coEvery { getSortedLocalAccounts() } returns accounts
        coEvery { getAccountType(JOINT_ADDRESS_1) } returns AccountType.Joint
        coEvery { getAccountType(STANDARD_ADDRESS) } returns AccountType.Algo25
        coEvery { getAccountType(JOINT_ADDRESS_2) } returns AccountType.Joint
        coEvery { getAccountType(LEDGER_ADDRESS) } returns AccountType.LedgerBle

        setupCurrencyMocks()
        setupAccountDetailMocks(JOINT_ADDRESS_1)
        setupAccountDetailMocks(JOINT_ADDRESS_2)

        val mockTitleItem = mockk<SingleAccountSelectionListItem.TitleItem>()
        val mockDescriptionItem = mockk<SingleAccountSelectionListItem.DescriptionItem>()
        every { singleAccountSelectionListItemMapper.mapToTitleItem(any()) } returns mockTitleItem
        every { singleAccountSelectionListItemMapper.mapToDescriptionItem(any()) } returns mockDescriptionItem

        val result = sut.getRekeyToJointAccountSelectionPreview(SOURCE_ADDRESS)

        assertNull(result.screenState)
        // title + description + 2 joint accounts = 4
        val accountItems =
            result.singleAccountSelectionListItems.filterIsInstance<SingleAccountSelectionListItem.AccountItem>()
        assertEquals(2, accountItems.size)
    }

    @Test
    fun `EXPECT same address excluded WHEN joint account is the source account`() = runTest {
        val accounts = listOf(
            AccountOrderIndex(JOINT_ADDRESS_1, 0),
            AccountOrderIndex(JOINT_ADDRESS_2, 1)
        )
        coEvery { getSortedLocalAccounts() } returns accounts
        coEvery { getAccountType(JOINT_ADDRESS_1) } returns AccountType.Joint
        coEvery { getAccountType(JOINT_ADDRESS_2) } returns AccountType.Joint

        setupCurrencyMocks()
        setupAccountDetailMocks(JOINT_ADDRESS_2)

        val mockTitleItem = mockk<SingleAccountSelectionListItem.TitleItem>()
        val mockDescriptionItem = mockk<SingleAccountSelectionListItem.DescriptionItem>()
        every { singleAccountSelectionListItemMapper.mapToTitleItem(any()) } returns mockTitleItem
        every { singleAccountSelectionListItemMapper.mapToDescriptionItem(any()) } returns mockDescriptionItem

        // Source address is JOINT_ADDRESS_1, so it should be excluded
        val result = sut.getRekeyToJointAccountSelectionPreview(JOINT_ADDRESS_1)

        val accountItems =
            result.singleAccountSelectionListItems.filterIsInstance<SingleAccountSelectionListItem.AccountItem>()
        assertEquals(1, accountItems.size)
    }

    @Test
    fun `EXPECT no account found screen state WHEN no joint accounts exist`() = runTest {
        val accounts = listOf(
            AccountOrderIndex(STANDARD_ADDRESS, 0),
            AccountOrderIndex(LEDGER_ADDRESS, 1)
        )
        coEvery { getSortedLocalAccounts() } returns accounts
        coEvery { getAccountType(STANDARD_ADDRESS) } returns AccountType.Algo25
        coEvery { getAccountType(LEDGER_ADDRESS) } returns AccountType.LedgerBle

        val mockTitleItem = mockk<SingleAccountSelectionListItem.TitleItem>()
        val mockDescriptionItem = mockk<SingleAccountSelectionListItem.DescriptionItem>()
        every { singleAccountSelectionListItemMapper.mapToTitleItem(any()) } returns mockTitleItem
        every { singleAccountSelectionListItemMapper.mapToDescriptionItem(any()) } returns mockDescriptionItem

        val result = sut.getRekeyToJointAccountSelectionPreview(SOURCE_ADDRESS)

        assertNotNull(result.screenState)
    }

    @Test
    fun `EXPECT no account found WHEN only source joint account exists`() = runTest {
        val accounts = listOf(
            AccountOrderIndex(JOINT_ADDRESS_1, 0)
        )
        coEvery { getSortedLocalAccounts() } returns accounts
        coEvery { getAccountType(JOINT_ADDRESS_1) } returns AccountType.Joint

        val mockTitleItem = mockk<SingleAccountSelectionListItem.TitleItem>()
        val mockDescriptionItem = mockk<SingleAccountSelectionListItem.DescriptionItem>()
        every { singleAccountSelectionListItemMapper.mapToTitleItem(any()) } returns mockTitleItem
        every { singleAccountSelectionListItemMapper.mapToDescriptionItem(any()) } returns mockDescriptionItem

        val result = sut.getRekeyToJointAccountSelectionPreview(JOINT_ADDRESS_1)

        assertNotNull(result.screenState)
    }

    @Test
    fun `EXPECT HdKey accounts excluded WHEN only joint accounts should be eligible`() = runTest {
        val accounts = listOf(
            AccountOrderIndex(HD_KEY_ADDRESS, 0),
            AccountOrderIndex(JOINT_ADDRESS_1, 1)
        )
        coEvery { getSortedLocalAccounts() } returns accounts
        coEvery { getAccountType(HD_KEY_ADDRESS) } returns AccountType.HdKey
        coEvery { getAccountType(JOINT_ADDRESS_1) } returns AccountType.Joint

        setupCurrencyMocks()
        setupAccountDetailMocks(JOINT_ADDRESS_1)

        val mockTitleItem = mockk<SingleAccountSelectionListItem.TitleItem>()
        val mockDescriptionItem = mockk<SingleAccountSelectionListItem.DescriptionItem>()
        every { singleAccountSelectionListItemMapper.mapToTitleItem(any()) } returns mockTitleItem
        every { singleAccountSelectionListItemMapper.mapToDescriptionItem(any()) } returns mockDescriptionItem

        val result = sut.getRekeyToJointAccountSelectionPreview(SOURCE_ADDRESS)

        val accountItems =
            result.singleAccountSelectionListItems.filterIsInstance<SingleAccountSelectionListItem.AccountItem>()
        assertEquals(1, accountItems.size)
    }

    private fun setupCurrencyMocks() {
        coEvery { getPrimaryCurrencySymbolOrName() } returns "$"
        coEvery { getSecondaryCurrencySymbol() } returns "ALGO"
        coEvery { isPrimaryCurrencyAlgo() } returns false
    }

    private fun setupAccountDetailMocks(address: String) {
        val accountValue = com.algorand.android.modules.accountcore.domain.model.AccountTotalValue(
            primaryAccountValue = java.math.BigDecimal.ZERO,
            secondaryAccountValue = java.math.BigDecimal.ZERO,
            assetCount = 0
        )
        coEvery { getAccountTotalValue(address, true) } returns accountValue
        coEvery { getAccountDisplayName(address) } returns AccountDisplayName(
            accountAddress = address,
            primaryDisplayName = address.take(6),
            secondaryDisplayName = null
        )
        coEvery { getAccountIconDrawablePreview(address) } returns AccountIconDrawablePreview(
            backgroundColorResId = android.R.color.black,
            iconTintResId = android.R.color.white,
            iconResId = android.R.drawable.ic_menu_add
        )
        val mockAccountItem = mockk<SingleAccountSelectionListItem.AccountItem>()
        every {
            singleAccountSelectionListItemMapper.mapToAccountItem(
                accountDisplayName = any(),
                accountIconDrawablePreview = any(),
                accountFormattedPrimaryValue = any(),
                accountFormattedSecondaryValue = any()
            )
        } returns mockAccountItem
    }

    private companion object {
        const val SOURCE_ADDRESS = "SOURCE_ADDR_12345"
        const val JOINT_ADDRESS_1 = "JOINT_ADDR_1_12345"
        const val JOINT_ADDRESS_2 = "JOINT_ADDR_2_12345"
        const val STANDARD_ADDRESS = "STANDARD_ADDR_12345"
        const val LEDGER_ADDRESS = "LEDGER_ADDR_12345"
        const val HD_KEY_ADDRESS = "HDKEY_ADDR_12345"
    }
}
