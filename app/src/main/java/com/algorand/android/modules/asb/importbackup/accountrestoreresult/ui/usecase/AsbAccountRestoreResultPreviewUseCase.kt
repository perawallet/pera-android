/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.asb.importbackup.accountrestoreresult.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.PluralAnnotatedString
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.asb.importbackup.accountrestoreresult.ui.mapper.AsbAccountRestoreResultPreviewMapper
import com.algorand.android.modules.asb.importbackup.accountrestoreresult.ui.model.AsbAccountRestoreResultPreview
import com.algorand.android.modules.asb.importbackup.accountselection.ui.model.AsbAccountImportResult
import com.algorand.android.modules.baseresult.ui.mapper.ResultListItemMapper
import com.algorand.android.modules.baseresult.ui.model.ResultListItem
import com.algorand.android.modules.baseresult.ui.usecase.BaseResultPreviewUseCase
import javax.inject.Inject

class AsbAccountRestoreResultPreviewUseCase @Inject constructor(
    private val asbAccountRestoreResultPreviewMapper: AsbAccountRestoreResultPreviewMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    resultListItemMapper: ResultListItemMapper
) : BaseResultPreviewUseCase(resultListItemMapper) {

    suspend fun getAsbAccountRestoreResultPreview(
        asbAccountImportResult: AsbAccountImportResult
    ): AsbAccountRestoreResultPreview {
        val importedAccountSize = asbAccountImportResult.importedAccountList.size

        val iconItem = createIconItem(
            iconTintColorResId = R.color.link_icon,
            iconResId = R.drawable.ic_check
        )
        val titleItem = createPluralTitleItem(
            titleTextResId = R.plurals.accounts_restored,
            quantity = importedAccountSize
        )
        val descriptionItem = createPluralDescriptionItem(
            pluralAnnotatedString = PluralAnnotatedString(R.plurals.accounts_were_restored, importedAccountSize),
            isClickable = false
        )
        val infoItem = createInfoBoxItemIfNeeded(
            unsupportedAccountCount = asbAccountImportResult.unsupportedAccountList.size,
            existingAccountCount = asbAccountImportResult.existingAccountList.size
        )
        val accountItems = asbAccountImportResult.importedAccountList.map { accountAddress ->
            // Since these account are not in our local, we have to create them manually BUT
            // do not forget that now are supporting only standard accounts in ASB
            val accountIconDrawablePreview = AccountIconDrawablePreview(
                iconResId = R.drawable.ic_wallet,
                iconTintResId = R.color.wallet_4_icon,
                backgroundColorResId = R.color.wallet_4
            )
            createAccountItem(
                accountDisplayName = getAccountDisplayName(accountAddress),
                accountIconDrawablePreview = accountIconDrawablePreview
            )
        }
        val resultItemList = mutableListOf<ResultListItem>().apply {
            add(iconItem)
            add(titleItem)
            add(descriptionItem)
            if (infoItem != null) add(infoItem)
            addAll(accountItems)
        }
        return asbAccountRestoreResultPreviewMapper.mapToAsbAccountRestoreResultPreview(
            resultListItems = resultItemList
        )
    }

    private fun createInfoBoxItemIfNeeded(
        existingAccountCount: Int,
        unsupportedAccountCount: Int
    ): ResultListItem.InfoBoxItem? {
        val infoDescriptionAnnotatedString = when {
            existingAccountCount > 0 && unsupportedAccountCount > 0 -> {
                PluralAnnotatedString(
                    pluralStringResId = R.plurals.account_was_not_restored,
                    quantity = existingAccountCount + unsupportedAccountCount
                )
            }
            existingAccountCount > 0 -> {
                PluralAnnotatedString(
                    pluralStringResId = R.plurals.account_was_not_restored_because_exist,
                    quantity = existingAccountCount
                )
            }
            unsupportedAccountCount > 0 -> {
                PluralAnnotatedString(
                    pluralStringResId = R.plurals.account_was_not_restored_because_unsupported,
                    quantity = unsupportedAccountCount
                )
            }
            else -> return null
        }
        return createPluralInfoBoxItem(
            infoIconResId = R.drawable.ic_info,
            infoIconTintResId = R.color.positive,
            infoTitleTextResId = R.string.unimported_accounts,
            infoTitleTintResId = R.color.positive,
            infoDescriptionPluralAnnotatedString = infoDescriptionAnnotatedString,
            infoDescriptionTintResId = R.color.positive,
            infoBoxTintColorResId = R.color.positive_lighter
        )
    }
}
