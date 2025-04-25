/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.rekeyedaccounts.mapper

import com.algorand.android.R
import com.algorand.android.models.PluralAnnotatedString
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.AccountItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.AuthAddressHeaderItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.DescriptionItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.IconItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject

internal class RekeyedAccountSelectionItemMapperImpl @Inject constructor() : RekeyedAccountSelectionItemMapper {

    override fun invoke(args: List<RekeyedAccountSelectionNavArg>): List<RekeyedAccountSelectionItem> {
        return mutableListOf<RekeyedAccountSelectionItem>().apply {
            add(getIconItem())
            add(getTitleItem(args))
            add(getDescriptionItem())
            addAll(getAccountItems(args))
        }
    }

    private fun getIconItem(): IconItem = IconItem(R.drawable.ic_wallet)

    private fun getTitleItem(args: List<RekeyedAccountSelectionNavArg>): RekeyedAccountSelectionItem.TitleItem {
        val rekeyedAddressCount = args.sumOf { it.rekeyedAccountAddresses.size }
        val title = PluralAnnotatedString(R.plurals.rekeyed_accounts_found, quantity = rekeyedAddressCount)
        return RekeyedAccountSelectionItem.TitleItem(title)
    }

    private fun getDescriptionItem(): DescriptionItem = DescriptionItem(R.string.select_the_rekeyed_accounts_you)

    private fun getAccountItems(args: List<RekeyedAccountSelectionNavArg>): List<RekeyedAccountSelectionItem> {
        val accountItems = mutableListOf<RekeyedAccountSelectionItem>()
        args.forEach { navArg ->
            accountItems.add(getAuthAddressHeaderItem(navArg))
            accountItems.addAll(getRekeyedAccountItems(navArg))
        }
        return accountItems
    }

    private fun getAuthAddressHeaderItem(navArg: RekeyedAccountSelectionNavArg): AuthAddressHeaderItem {
        return AuthAddressHeaderItem(
            authAddress = navArg.authAddress,
            iconDrawablePreview = navArg.authAddressIconDrawablePreview
        )
    }

    private fun getRekeyedAccountItems(navArg: RekeyedAccountSelectionNavArg): List<AccountItem> {
        return navArg.rekeyedAccountAddresses.map { rekeyedAccountAddress ->
            val accountDisplayName = AccountDisplayName(
                accountAddress = rekeyedAccountAddress,
                primaryDisplayName = rekeyedAccountAddress.toShortenedAddress(),
                secondaryDisplayName = null
            )
            val accountIconDrawablePreview = AccountIconDrawablePreview(
                backgroundColorResId = R.color.wallet_4,
                iconTintResId = R.color.wallet_4_icon,
                iconResId = R.drawable.ic_rekey_shield
            )
            AccountItem(
                authAddress = navArg.authAddress,
                accountIconDrawablePreview = accountIconDrawablePreview,
                accountDisplayName = accountDisplayName,
                selectorDrawableRes = R.drawable.selector_found_account_checkbox,
                isSelected = false
            )
        }
    }
}
