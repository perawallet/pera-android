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

package com.algorand.android.modules.accountcore.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews.getRekeyedDrawable
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import javax.inject.Inject

internal class GetAccountIconDrawablePreviewUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail,
    private val getAccountRegistrationType: GetAccountRegistrationType,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress,
    private val getAccountIconDrawablePreviewByType: GetAccountIconDrawablePreviewByType
) : GetAccountIconDrawablePreview {

    override suspend fun invoke(address: String): AccountIconDrawablePreview {
        val accountDetail = getAccountDetail(address)
        return getAccountIconDrawablePreview(
            address = address,
            accountType = accountDetail.accountType,
            rekeyAuthAddress = null,
            rekeyAuthRegistrationType = null
        )
    }

    override suspend fun invoke(accountDetail: AccountDetail): AccountIconDrawablePreview {
        return getAccountIconDrawablePreview(
            address = accountDetail.address,
            accountType = accountDetail.accountType,
            rekeyAuthAddress = null,
            rekeyAuthRegistrationType = null
        )
    }

    override suspend fun invoke(accountLite: AccountLite): AccountIconDrawablePreview {
        return with(accountLite) {
            if (cachedInfo == null) {
                getAccountIconDrawablePreviewByType(registrationType)
            } else {
                getAccountIconDrawablePreview(
                    address = address,
                    accountType = cachedInfo.type,
                    rekeyAuthAddress = cachedInfo.rekeyAuthAddress,
                    rekeyAuthRegistrationType = cachedInfo.rekeyAuthRegistrationType
                )
            }
        }
    }

    private suspend fun getAccountIconDrawablePreview(
        address: String,
        accountType: AccountType?,
        rekeyAuthAddress: String?,
        rekeyAuthRegistrationType: AccountRegistrationType?
    ): AccountIconDrawablePreview {
        return when (accountType) {
            AccountType.Algo25 -> AccountIconDrawablePreviews.getAlgo25Drawable()
            AccountType.HdKey -> AccountIconDrawablePreviews.getHdKeyDrawable()
            AccountType.LedgerBle -> AccountIconDrawablePreviews.getLedgerBleDrawable()
            AccountType.NoAuth -> AccountIconDrawablePreviews.getNoAuthDrawable()
            AccountType.Rekeyed -> getRekeyedDrawable()
            AccountType.RekeyedAuth -> getRekeyedAuthDrawable(
                rekeyAuthAddress = rekeyAuthAddress,
                rekeyAuthRegistrationType = rekeyAuthRegistrationType,
                address = address
            )
            AccountType.Joint -> AccountIconDrawablePreviews.getJointDrawable()
            null -> AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
        }
    }

    private suspend fun getRekeyedAuthDrawable(
        rekeyAuthAddress: String?,
        rekeyAuthRegistrationType: AccountRegistrationType?,
        address: String
    ): AccountIconDrawablePreview {
        val rekeyAdminType = when {
            rekeyAuthRegistrationType != null -> rekeyAuthRegistrationType
            rekeyAuthAddress != null -> getAccountRegistrationType(rekeyAuthAddress)
            else -> {
                val adminAddr = getAccountRekeyAdminAddress(address)
                if (adminAddr != null) getAccountRegistrationType(adminAddr) else null
            }
        } ?: return getRekeyedDrawable()
        val backgroundColorResId = when (rekeyAdminType) {
            AccountRegistrationType.Algo25,
            AccountRegistrationType.HdKey -> AccountIconResource.STANDARD.backgroundColorResId
            AccountRegistrationType.Joint -> AccountIconResource.JOINT.backgroundColorResId
            else -> AccountIconResource.REKEYED_AUTH.backgroundColorResId
        }
        val iconTintResId = when (rekeyAdminType) {
            AccountRegistrationType.Algo25,
            AccountRegistrationType.HdKey -> AccountIconResource.STANDARD.iconTintResId
            AccountRegistrationType.Joint -> AccountIconResource.JOINT.iconTintResId
            else -> AccountIconResource.LEDGER.iconTintResId
        }
        return AccountIconDrawablePreview(
            backgroundColorResId = backgroundColorResId,
            iconTintResId = iconTintResId,
            iconResId = R.drawable.ic_rekey_shield
        )
    }
}
