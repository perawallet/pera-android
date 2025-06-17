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

package com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.viewmodel

import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyUsd
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.mapper.RegisteredHdKeyItemMapper
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.model.RegisteredHdKeyItem
import com.algorand.wallet.account.info.domain.usecase.GetRegisteredHdKeys
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultRecoverRegisteredAccountsAccountProcessor @Inject constructor(
    private val aesPlatformManager: AESPlatformManager,
    private val getRegisteredHdKeys: GetRegisteredHdKeys,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val isPrimaryCurrencyUsd: IsPrimaryCurrencyUsd,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val registeredHdKeyItemMapper: RegisteredHdKeyItemMapper,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName
) : RecoverRegisteredAccountsAccountProcessor {

    override suspend fun getRegisteredHdKeyItems(encryptedEntropy: ByteArray): List<RegisteredHdKeyItem> {
        val entropy = aesPlatformManager.decryptByteArray(encryptedEntropy)
        val registeredAccounts = getRegisteredHdKeys(entropy.copyOf())
        entropy.clearFromMemory()
        val usdToSelectedCurrencyMultiplier = getUsdToSelectedCurrencyMultiplier()
        val selectedCurrencySymbol = getSelectedCurrencySymbol()
        return registeredAccounts.map { hdKey ->
            registeredHdKeyItemMapper(hdKey, usdToSelectedCurrencyMultiplier, selectedCurrencySymbol)
        }
    }

    private fun getUsdToSelectedCurrencyMultiplier(): BigDecimal {
        return if (isPrimaryCurrencyUsd() || isPrimaryCurrencyAlgo()) {
            return BigDecimal.ONE
        } else {
            getUsdToPrimaryCurrencyConversionRate()
        }
    }

    private fun getSelectedCurrencySymbol(): String {
        return if (isPrimaryCurrencyUsd() || isPrimaryCurrencyAlgo()) {
            Currency.USD.symbol
        } else {
            getPrimaryCurrencySymbolOrName()
        }
    }
}
