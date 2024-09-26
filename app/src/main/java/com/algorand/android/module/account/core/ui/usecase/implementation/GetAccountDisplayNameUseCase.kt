package com.algorand.android.module.account.core.ui.usecase.implementation

import android.content.res.Resources
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.LedgerBle
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.NoAuth
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Rekeyed
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.RekeyedAuth
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.component.utils.toShortenedAddress
import com.algorand.android.module.custominfo.domain.usecase.GetCustomInfoOrNull
import com.algorand.android.designsystem.R
import com.algorand.android.nameservice.domain.model.NameService
import com.algorand.android.nameservice.domain.usecase.GetAccountNameService
import javax.inject.Inject

/**
 *
 * Account Naming Rule;
 *
 * If account is renamed;
 *  - If account matches with NFDomain;
 *      + Primary display name: Given custom name (Spending Account)
 *      + Secondary display name: NFDomain service name (pera.algo)
 *  - If account doesn't match with NFDomain;
 *      + Primary display name: Given custom name (Spending Account)
 *      + Secondary display name: Shortened account address: (XCSASD...ZSFFSW)
 * If account didn't rename;
 *  - If account matches with NFDomain;
 *      + Primary display name: NFDomain service name (pera.algo)
 *      + Secondary display name: Shortened account address: (XCSASD...ZSFFSW)
 *  - If account doesn't match with NFDomain;
 *      + Primary display name: Shortened account address: (XCSASD...ZSFFSW)
 *      + Secondary display name:
 *          - If Ledger account: Ledger Account
 *          - If Rekeyed account: Rekeyed Account
 *          - If Watch account: Watch Account
 *          - else: empty field
 */
internal class GetAccountDisplayNameUseCase @Inject constructor(
    private val getCustomInfoOrNull: GetCustomInfoOrNull,
    private val getAccountDetail: GetAccountDetail,
    private val resources: Resources,
    private val getAccountNameService: GetAccountNameService
) : GetAccountDisplayName {

    override suspend fun invoke(address: String): AccountDisplayName {
        val customAccountName = getCustomInfoOrNull(address)?.customName
            ?: return getAccountDisplayNameWithAccountAddressOnly(address)
        val nameService = getAccountNameService(address)
        return AccountDisplayName(
            accountAddress = address,
            primaryDisplayName = getPrimaryName(address, customAccountName, nameService),
            secondaryDisplayName = getSecondaryName(address, customAccountName, nameService)
        )
    }

    override suspend fun invoke(address: String, name: String?, type: AccountType): AccountDisplayName {
        val safeName = name ?: return getAccountDisplayNameWithAccountAddressOnly(address)
        return AccountDisplayName(
            accountAddress = address,
            primaryDisplayName = safeName,
            secondaryDisplayName = getAccountTypeName(type)
        )
    }

    override suspend fun invoke(accountDetail: AccountDetail): AccountDisplayName {
        val nameService = getAccountNameService(accountDetail.address)
        return with(accountDetail) {
            AccountDisplayName(
                accountAddress = address,
                primaryDisplayName = getPrimaryName(address, customInfo.customName, nameService),
                secondaryDisplayName = getSecondaryName(address, customInfo.customName, nameService)
            )
        }
    }

    private fun getPrimaryName(address: String, customAccountName: String?, nameService: NameService?): String {
        val isAccountRenamed = customAccountName != address.toShortenedAddress() && !customAccountName.isNullOrBlank()
        val nameServiceName = nameService?.nameServiceName.orEmpty()
        val isAccountMatchedNfDomain = nameServiceName.isNotBlank()
        return when {
            isAccountRenamed -> customAccountName.orEmpty()
            isAccountMatchedNfDomain -> nameServiceName
            else -> address.toShortenedAddress()
        }
    }

    private suspend fun getSecondaryName(
        address: String,
        customAccountName: String?,
        nameService: NameService?
    ): String? {
        val isAccountRenamed = customAccountName != address.toShortenedAddress() && !customAccountName.isNullOrBlank()
        val nameServiceName = nameService?.nameServiceName.orEmpty()
        val isAccountMatchedNfDomain = nameServiceName.isNotBlank()
        return when {
            isAccountRenamed && isAccountMatchedNfDomain -> nameServiceName
            isAccountRenamed || isAccountMatchedNfDomain -> address.toShortenedAddress()
            else -> getAccountTypeName(address)
        }
    }

    private suspend fun getAccountTypeName(address: String): String? {
        val accountType = getAccountDetail(address).accountType
        return getAccountTypeName(accountType)
    }

    private fun getAccountTypeName(type: AccountType?): String? {
        return when (type) {
            LedgerBle -> R.string.ledger_account
            Rekeyed, RekeyedAuth -> R.string.rekeyed_account
            NoAuth -> R.string.watch_account
            else -> null
        }?.run { resources.getString(this) }
    }

    private fun getAccountDisplayNameWithAccountAddressOnly(address: String): AccountDisplayName {
        return AccountDisplayName(
            accountAddress = address,
            primaryDisplayName = address.toShortenedAddress(),
            secondaryDisplayName = address.toShortenedAddress()
        )
    }
}
