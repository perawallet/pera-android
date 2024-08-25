package com.algorand.android.core.component.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.*
import com.algorand.android.accountsorting.component.domain.usecase.SetAccountOrderIndex
import com.algorand.android.asb.component.domain.usecase.SetAccountAsbBackUpStatus
import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.custominfo.component.domain.usecase.SetCustomInfo
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

internal class AddAccountUseCase @Inject constructor(
    private val createAlgo25Account: CreateAlgo25Account,
    private val createLedgerBleAccount: CreateLedgerBleAccount,
    private val createNoAuthAccount: CreateNoAuthAccount,
    private val setCustomInfo: SetCustomInfo,
    private val setAccountAsbBackUpStatus: SetAccountAsbBackUpStatus,
    private val setAccountOrderIndex: SetAccountOrderIndex
) : AddAccount {

    override suspend fun addAlgo25(address: String, secretKey: ByteArray, isBackedUp: Boolean, customName: String?) {
        setCustomInfo(CustomInfo(address, customName))
        setAccountOrderIndex(address, MAX_VALUE)
        setAccountAsbBackUpStatus(address, isBackedUp)
        createAlgo25Account(address, secretKey)
    }

    override suspend fun addLedgerBle(
        address: String,
        deviceMacAddress: String,
        indexInLedger: Int,
        customName: String?
    ) {
        setCustomInfo(CustomInfo(address, customName))
        setAccountOrderIndex(address, MAX_VALUE)
        setAccountAsbBackUpStatus(address, true)
        createLedgerBleAccount(address, deviceMacAddress, indexInLedger)
    }

    override suspend fun addNoAuth(address: String, customName: String?) {
        setCustomInfo(CustomInfo(address, customName))
        setAccountOrderIndex(address, MAX_VALUE)
        setAccountAsbBackUpStatus(address, true)
        createNoAuthAccount(address)
    }
}
